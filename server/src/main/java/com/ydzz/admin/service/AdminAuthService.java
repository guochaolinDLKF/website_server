package com.ydzz.admin.service;

import cn.dev33.satoken.SaManager;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.dto.AdminInfoVo;
import com.ydzz.admin.dto.AdminLoginRequest;
import com.ydzz.admin.dto.AdminLoginVo;
import com.ydzz.admin.entity.AdminUser;
import com.ydzz.admin.mapper.AdminUserMapper;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 后台认证服务：登录、登出、当前信息、改密。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final AdminUserService adminUserService;
    private final AdminRbacService rbacService;
    private final AdminPermissionService permissionService;
    private final AdminLogService logService;
    private final IpRegionService ipRegionService;
    private final StringRedisTemplate stringRedisTemplate;

    /** 验证码 Redis key 前缀 */
    private static final String CAPTCHA_KEY_PREFIX = "admin:captcha:";
    /** 验证码有效期（分钟） */
    private static final long CAPTCHA_TTL_MINUTES = 2;

    public AdminAuthService(AdminUserMapper adminUserMapper, AdminUserService adminUserService,
                            AdminRbacService rbacService, AdminPermissionService permissionService,
                            AdminLogService logService, IpRegionService ipRegionService,
                            StringRedisTemplate stringRedisTemplate) {
        this.adminUserMapper = adminUserMapper;
        this.adminUserService = adminUserService;
        this.rbacService = rbacService;
        this.permissionService = permissionService;
        this.logService = logService;
        this.ipRegionService = ipRegionService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成算术图形验证码（20 以内的加 / 减，要求输入计算结果）。
     *
     * <p>图片上绘制形如「7 + 8 = ?」的算式，Redis 中存放「计算结果」，校验时与用户输入比对。</p>
     *
     * @return { captchaId, image(base64 data url) }
     */
    public Map<String, String> generateCaptcha() {
        String[] challenge = buildMathChallenge();     // [算式文本, 答案]
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(140, 40, 1, 10);
        captcha.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 22));
        // 自定义生成器：固定绘制本次算式（verify 不走此处，由 Redis 答案比对）
        captcha.setGenerator(new CodeGenerator() {
            @Override
            public String generate() {
                return challenge[0];
            }

            @Override
            public boolean verify(String code, String userInputCode) {
                return false;
            }
        });
        captcha.createCode();

        String id = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + id, challenge[1], CAPTCHA_TTL_MINUTES, TimeUnit.MINUTES);
        Map<String, String> data = new LinkedHashMap<>();
        data.put("captchaId", id);
        data.put("image", captcha.getImageBase64Data());
        return data;
    }

    /**
     * 随机生成一道「20 以内的加 / 减」算式，返回 [算式文本, 答案]。
     * 加法保证两数之和 ≤ 20；减法保证被减数 ≤ 20 且结果非负。
     */
    private String[] buildMathChallenge() {
        if (RandomUtil.randomBoolean()) {          // 减法：被减数≤20，结果非负
            int a = RandomUtil.randomInt(2, 21);   // [2,20]
            int b = RandomUtil.randomInt(1, a + 1); // [1,a]
            return new String[]{a + " - " + b + " = ?", String.valueOf(a - b)};
        }
        // 加法：两数之和 ≤ 20
        int a = RandomUtil.randomInt(1, 20);       // [1,19]
        int b = RandomUtil.randomInt(1, 21 - a);   // [1,20-a]
        return new String[]{a + " + " + b + " = ?", String.valueOf(a + b)};
    }

    /** 校验图形验证码（一次性，校验后即删除）；不通过抛业务异常 */
    private void verifyCaptcha(String captchaId, String captcha) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captcha)) {
            throw new BusinessException(ErrorCode.BadRequest, "请输入验证码");
        }
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        String saved = stringRedisTemplate.opsForValue().get(key);
        stringRedisTemplate.delete(key);
        if (saved == null) {
            throw new BusinessException(ErrorCode.BadRequest, "验证码已过期，请刷新后重试");
        }
        if (!saved.equalsIgnoreCase(captcha)) {
            throw new BusinessException(ErrorCode.BadRequest, "验证码错误");
        }
    }

    public AdminLoginVo login(AdminLoginRequest req, HttpServletRequest request) {
        // 1. 先校验图形验证码（一次性）
        verifyCaptcha(req.getCaptchaId(), req.getCaptcha());

        String ip = ipRegionService.resolvePublicIp(request);
        String ua = request.getHeader("User-Agent");

        AdminUser user = adminUserService.getByUsername(req.getUsername());
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            logService.saveLoginLog(user == null ? null : user.getId(), req.getUsername(), ip, ua,
                    1, 0, "账号或密码错误");
            throw new BusinessException(ErrorCode.Unauthorized, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            logService.saveLoginLog(user.getId(), user.getUsername(), ip, ua, 1, 0, "账号已被禁用");
            throw new BusinessException(ErrorCode.Forbidden, "账号已被禁用");
        }

        // 单设备登录（新登录顶掉旧的）：先把该账号此前在其它设备的会话顶下线，旧设备下次请求时被挤下线
        AdminStpUtil.replaced(user.getId());

        // 登录后台独立域
        AdminStpUtil.login(user.getId());

        // 更新最后登录信息
        AdminUser update = new AdminUser();
        update.setId(user.getId());
        update.setLastLoginTime(LocalDateTime.now());
        update.setLastLoginIp(ip);
        adminUserMapper.updateById(update);

        logService.saveLoginLog(user.getId(), user.getUsername(), ip, ua, 1, 1, "登录成功");

        AdminLoginVo vo = new AdminLoginVo();
        vo.setTokenName(AdminStpUtil.getTokenInfo().getTokenName());
        vo.setTokenValue(AdminStpUtil.getTokenValue());
        // 若配置了 token-prefix，前端需以「前缀 + 空格 + Token」格式提交请求头，故一并下发前缀
        vo.setTokenPrefix(SaManager.getConfig().getTokenPrefix());
        vo.setAdminInfo(buildInfo(user));
        return vo;
    }

    public void logout(HttpServletRequest request) {
        Object loginId = AdminStpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            AdminUser user = adminUserMapper.selectById(Long.valueOf(loginId.toString()));
            String username = user == null ? null : user.getUsername();
            logService.saveLoginLog(user == null ? null : user.getId(), username,
                    ipRegionService.resolvePublicIp(request), request.getHeader("User-Agent"),
                    2, 1, "登出成功");
        }
        AdminStpUtil.logout();
    }

    public AdminInfoVo currentInfo() {
        long adminId = AdminStpUtil.getLoginIdAsLong();
        AdminUser user = adminUserMapper.selectById(adminId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NotFound, "管理员不存在");
        }
        return buildInfo(user);
    }

    public void changePassword(String oldPassword, String newPassword) {
        long adminId = AdminStpUtil.getLoginIdAsLong();
        adminUserService.changePassword(adminId, oldPassword, newPassword);
    }

    private AdminInfoVo buildInfo(AdminUser user) {
        AdminInfoVo info = new AdminInfoVo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setRealName(user.getRealName());
        info.setAvatar(user.getAvatar());
        info.setRoles(rbacService.listRoleCodes(user.getId()));
        info.setPermissions(rbacService.listPermissionCodes(user.getId()));
        info.setMenus(permissionService.buildTree(rbacService.listPermissions(user.getId())));
        return info;
    }
}
