// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2020~2025 小蚂蚁云团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁云团队 团队荣誉出品
// +----------------------------------------------------------------------
// | 版权和免责声明:
// | 本团队对该软件框架产品拥有知识产权（包括但不限于商标权、专利权、著作权、商业秘密等）
// | 均受到相关法律法规的保护，任何个人、组织和单位不得在未经本团队书面授权的情况下对所授权
// | 软件框架产品本身申请相关的知识产权，被授权主体务必妥善保管官方所授权的软件产品源码，禁
// | 止以任何形式对外泄露(包括但不限于分享、开源、网络平台),禁止用于任何违法、侵害他人合法
// | 权益等恶意的行为，禁止用于任何违反我国法律法规的一切项目研发，任何个人、组织和单位用于
// | 项目研发而产生的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其造成的损失 (包括
// | 但不限于直接、间接、附带或衍生的损失等)，本团队不承担任何法律责任，本软件框架禁止任何
// | 单位、组织、个人用于任何违法、侵害他人合法利益等恶意的行为，如有发现违规、违法的犯罪行
// | 为，本团队将无条件配合公安机关调查取证同时保留一切以法律手段起诉的权利，本软件框架只能
// | 用于公司和个人内部的法律所允许的合法合规的软件产品研发，详细声明内容请阅读《框架免责声
// | 明》附件；
// +----------------------------------------------------------------------

package com.xiaomayi.admin.service.impl;

import com.xiaomayi.admin.service.OnlineService;
import com.xiaomayi.core.constant.CacheConstant;
import com.xiaomayi.redis.core.RedisCache;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.online.OnlineListDTO;
import com.xiaomayi.system.vo.online.OnlineUserVO;
import com.xiaomayi.security.security.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 * 在线用户 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-11
 */
@Service
@AllArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    private final RedisCache redisCache;

    /**
     * 查询在线用户列表
     *
     * @param onlineListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public R list(OnlineListDTO onlineListDTO) {
        // 登录IP
        String ipAddr = onlineListDTO.getIpAddr();
        // 登录账号
        String username = onlineListDTO.getUsername();
        // 查询指定缓存前缀正则用户
        Collection<String> keys = redisCache.keys(CacheConstant.LOGIN_TOKEN_KEY + "*");
        // 实例化登录用户对象列表
        List<OnlineUserVO> onlineUserVOList = new ArrayList<>();
        // 缓存集合判空
        if (!CollectionUtils.isEmpty(keys)) {
            // 遍历缓存KEY值
            for (String key : keys) {
                // 根据缓存KEY获取登录用户信息
                LoginUser user = redisCache.getCacheObject(key);
                // 用户为空直接跳过
                if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser())) {
                    continue;
                }

                // 登录IP、登录账号
                if (StringUtils.isNotEmpty(ipAddr) && StringUtils.isNotEmpty(username)) {
                    if (!StringUtils.equals(ipAddr, user.getIpAddr()) || !StringUtils.equals(username, user.getUsername())) {
                        continue;
                    }
                }
                // 登录IP
                else if (StringUtils.isNotEmpty(ipAddr) && !StringUtils.equals(ipAddr, user.getIpAddr())) {
                    continue;
                }
                // 登录账号
                else if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, user.getUsername())) {
                    continue;
                }

                // 装配登录用户信息
                OnlineUserVO onlineUserVO = getOnlineUserVO(user);
                // 加入列表
                onlineUserVOList.add(onlineUserVO);
            }
        }
        // 结果数据集按照登录时间倒序排列
        onlineUserVOList.sort(Comparator.comparing(OnlineUserVO::getLoginTime).reversed());
        // 返回结果
        return R.ok(onlineUserVOList);
    }

    /**
     * 获取在线用户信息
     *
     * @param user 用户
     * @return 返回结果
     */
    private static OnlineUserVO getOnlineUserVO(LoginUser user) {
        OnlineUserVO onlineUserVO = new OnlineUserVO();
        onlineUserVO.setTokenId(user.getToken());
        onlineUserVO.setUsername(user.getUsername());
        onlineUserVO.setDeptId(user.getDeptId());
        onlineUserVO.setDeptName(user.getDeptName());
        onlineUserVO.setIpAddr(user.getIpAddr());
        onlineUserVO.setLoginLocation(user.getLoginLocation());
        onlineUserVO.setBrowser(user.getBrowser());
        onlineUserVO.setOs(user.getOs());
        // 登录时间
        LocalDateTime loginTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getLoginTime()), ZoneId.systemDefault());
        onlineUserVO.setLoginTime(loginTime);
        return onlineUserVO;
    }

    /**
     * 强制退出
     *
     * @param tokenId 令牌ID
     * @return 返回结果
     */
    @Override
    public R logout(String tokenId) {
        redisCache.deleteObject(CacheConstant.LOGIN_TOKEN_KEY + tokenId);
        return R.ok();
    }
}
