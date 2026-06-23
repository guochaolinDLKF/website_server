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

package com.xiaomayi.system.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.*;
import com.xiaomayi.pdf.utils.PdfUtils;
import com.xiaomayi.system.dto.user.*;
import com.xiaomayi.system.entity.*;
import com.xiaomayi.system.mapper.*;
import com.xiaomayi.system.service.*;
import com.xiaomayi.system.utils.DictResolver;
import com.xiaomayi.system.vo.user.UserExcelVO;
import com.xiaomayi.system.vo.user.UserInfoVO;
import com.xiaomayi.system.vo.user.UserListVO;
import com.xiaomayi.system.vo.user.UserProfileVO;
import com.xiaomayi.tenant.annotation.TenantIgnore;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;
    private final LevelService levelService;
    private final PositionService positionService;
    private final DeptService deptService;
    private final UserRoleService userRoleService;
    private final CityService cityService;
    private final TenantMapper tenantMapper;

    /**
     * 查询分页列表
     *
     * @param userPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<User> page(UserPageDTO userPageDTO) {
        // 分页设置
        Page<User> page = new Page<>(userPageDTO.getPageNo(), userPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                // 部门ID
                .eq(StringUtils.isNotNull(userPageDTO.getDeptId()) && userPageDTO.getDeptId() > 0, User::getDeptId, userPageDTO.getDeptId())
                // 用户名称
                .like(StringUtils.isNotEmpty(userPageDTO.getRealname()), User::getRealname, userPageDTO.getRealname())
                // 用户状态：1-正常 2-禁用
                .eq(StringUtils.isNotNull(userPageDTO.getStatus()) && userPageDTO.getStatus() > 0, User::getStatus, userPageDTO.getStatus())
                // 是否主账号：1-是 0-否,默认主账号不显示
                .ne(User::getMaster, 1)
                .eq(User::getDelFlag, 0)
                .orderByDesc(User::getId);
        // 查询分页数据
        Page<User> pageData = page(page, wrapper);
        // 数据处理
        pageData.convert(x -> {
            UserListVO userListVO = Convert.convert(UserListVO.class, x);
            // 用户头像
            if (!StringUtils.isEmpty(x.getAvatar())) {
                userListVO.setAvatar(CommonUtils.getFileURL(x.getAvatar()));
            }
            // 用户性别
            if (StringUtils.isNotNull(x.getGender()) && x.getGender() > 0) {
                userListVO.setGenderName(DictResolver.getDictItemName("sys_gender", x.getGender().toString()));
            }
            // 获取职级信息
            if (StringUtils.isNotNull(x.getLevelId())) {
                Level levelInfo = levelService.getInfo(x.getLevelId());
                if (StringUtils.isNotNull(levelInfo)) {
                    userListVO.setLevelName(levelInfo.getName());
                }
            }
            // 获取岗位信息
            if (StringUtils.isNotNull(x.getPositionId())) {
                Position positionInfo = positionService.getInfo(x.getPositionId());
                if (StringUtils.isNotNull(positionInfo)) {
                    userListVO.setPositionName(positionInfo.getName());
                }
            }
            // 获取部门信息
            if (StringUtils.isNotNull(x.getDeptId())) {
                Dept dept = deptService.getInfo(x.getDeptId());
                if (StringUtils.isNotNull(dept)) {
                    userListVO.setDeptName(dept.getName());
                }
            }
            // 获取行政区划
            if (StringUtils.isNotNull(x.getProvinceCode()) &&
                    StringUtils.isNotNull(x.getCityCode()) &&
                    StringUtils.isNotNull(x.getDistrictCode())) {
                // 初始化数组
                String[] strings = new String[3];
                strings[0] = x.getProvinceCode();
                strings[1] = x.getCityCode();
                strings[2] = x.getDistrictCode();
                userListVO.setCity(strings);
            }
            // 获取角色列表
            List<Role> roleList = roleMapper.getRolesByUserId(x.getId());
            userListVO.setRoles(roleList);
            // 返回VO对象
            return userListVO;
        });
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param userListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<UserListVO> getList(UserListDTO userListDTO) {
        // 查询数据源
        List<User> userList = list(new LambdaQueryWrapper<User>()
                // 用户名称
                .like(StringUtils.isNotEmpty(userListDTO.getRealname()), User::getRealname, userListDTO.getRealname())
                // 用户状态：1-正常 2-禁用
                .eq(StringUtils.isNotNull(userListDTO.getStatus()) && userListDTO.getStatus() > 0, User::getStatus, userListDTO.getStatus())
                .eq(User::getDelFlag, 0)
                .orderByAsc(User::getId));
        // 实例化VO列表
        List<UserListVO> userListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userList)) {
            // 遍历数据源
            for (User user : userList) {
                // 实例化VO对象
                UserListVO userListVO = new UserListVO();
                BeanUtils.copyProperties(user, userListVO);
                userListVOList.add(userListVO);
            }
        }
        return userListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 用户ID
     * @return 返回结果
     */
    @Override
    public User getInfo(Integer id) {
        User user = getById(id);
        if (StringUtils.isNull(user) || !user.getDelFlag().equals(0)) {
            return null;
        }
        return user;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 用户ID
     * @return 返回结果
     */
    @Override
    public UserInfoVO getDetail(Integer id) {
        User user = getInfo(id);
        if (StringUtils.isNull(user)) {
            return null;
        }
        // 实例化VO
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        // 用户头像
        if (!StringUtils.isEmpty(user.getAvatar())) {
            userInfoVO.setAvatar(CommonUtils.getFileURL(user.getAvatar()));
        }
        // 行政区划处理
        if (StringUtils.isNotNull(user.getProvinceCode()) &&
                StringUtils.isNotNull(user.getCityCode()) &&
                StringUtils.isNotNull(user.getDistrictCode())) {
            // 初始化数组
            String[] strings = new String[4];
            strings[0] = user.getProvinceCode();
            strings[1] = user.getCityCode();
            strings[2] = user.getDistrictCode();
            strings[3] = user.getStreetCode();
            userInfoVO.setCity(strings);
        }

        // 获取角色信息
        List<Role> roleList = roleMapper.getRolesByUserId(user.getId());
        // 获取角色ID
        List<Integer> roleIdList = roleList.stream().map(Role::getId).toList();
        userInfoVO.setRoles(roleIdList.toArray(new Integer[0]));

        // 返回结果
        return userInfoVO;
    }

    /**
     * 添加用户
     *
     * @param userAddDTO 参数
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R add(UserAddDTO userAddDTO) {
        // 检查用户是否已存在
        if (checkExist(userAddDTO.getUsername(), 0)) {
            return R.failed("用户已存在");
        }

        // 租户ID,租户ID=1是平台默认租户
        Integer tenantId = userAddDTO.getTenantId();
        // 查询当前租户信息
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (StringUtils.isNull(tenant)) {
            return R.failed("租户不存在");
        }
        // 租户ID!=1时进行验证
        if (!tenantId.equals(1)) {
            // 租户子账号格式案例：T00001@0001，租户总账号@子账号
            String accountFormat = String.format("%s@", tenant.getCode());
            if (!userAddDTO.getUsername().startsWith(accountFormat)) {
                userAddDTO.setUsername(accountFormat + userAddDTO.getUsername());
            }
            // 判断此租户是否不限用户数
            if (tenant.getNumber() != -1) {
                // 验证租户开通人数是否已超限
                if (tenant.getUserNum() >= tenant.getNumber()) {
                    return R.failed("人数超限，请联系管理员");
                }
            }
        }

        // 实例化对象
        User user = new User();
        // 属性拷贝
        BeanUtils.copyProperties(userAddDTO, user);
        // 用户类型：0-系统用户 1-租户用户
        if (tenantId != 1) {
            user.setType(1);
        }
        // 头像处理
        if (StringUtils.isNotEmpty(userAddDTO.getAvatar()) && userAddDTO.getAvatar().contains(AppConfig.getDomain())) {
            user.setAvatar(userAddDTO.getAvatar().replaceAll(AppConfig.getDomain(), ""));
        }

//        // 密码基于SpringSecurity加密处理
//        if (StringUtils.isNotEmpty(userAddDTO.getPassword())) {
//            // 加密盐
//            String salt = RandomUtil.randomString(10);
//            user.setSalt(salt);
//            user.setPassword(SecurityUtils.encryptPassword(salt + userAddDTO.getPassword()));
//        } else {
//            user.setPassword(null);
//        }
        // 行政区划处理
        if (userAddDTO.getCity().length < 4) {
            return R.failed("请选择行政区划");
        }
        // 省份编码
        user.setProvinceCode(userAddDTO.getCity()[0]);
        // 城市编码
        user.setCityCode(userAddDTO.getCity()[1]);
        // 县区编码
        user.setDistrictCode(userAddDTO.getCity()[2]);
        // 街道编码
        user.setStreetCode(userAddDTO.getCity()[3]);
        // 获取行政区划详细信息
        String cityInfo = cityService.getCityNames(user.getStreetCode(), ",");
        if (StringUtils.isNotEmpty(cityInfo)) {
            user.setCityInfo(cityInfo);
        }
        boolean result = save(user);
        if (!result) {
            return R.failed();
        }

        // 保存用户角色数据
        userRoleService.saveUserRole(user.getId(), userAddDTO.getRoles());

        // 查询当前租户用户数量
        long userNum = count(new LambdaQueryWrapper<User>()
                .ne(User::getMaster, 1)
                .eq(User::getDelFlag, 0));
        tenant.setUserNum(userNum);
        int count = tenantMapper.updateById(tenant);
        if (count == 0) {
            return R.failed("租户人数更新失败");
        }

        // 返回结果
        return R.ok();
    }

    /**
     * 更新用户
     *
     * @param userUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(UserUpdateDTO userUpdateDTO) {
        // 根据ID查询信息
        User user = getInfo(userUpdateDTO.getId());
        if (StringUtils.isNull(user)) {
            return R.failed("记录不存在");
        }
        // 检查用户是否已存在
        if (checkExist(userUpdateDTO.getUsername(), user.getId())) {
            return R.failed("用户已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(userUpdateDTO, user);
        // 头像处理
        if (StringUtils.isNotEmpty(userUpdateDTO.getAvatar()) && userUpdateDTO.getAvatar().contains(AppConfig.getDomain())) {
            user.setAvatar(userUpdateDTO.getAvatar().replaceAll(AppConfig.getDomain(), ""));
        }
//        // 密码基于SpringSecurity加密处理
//        if (StringUtils.isNotEmpty(userUpdateDTO.getPassword())) {
//            // 加密盐
//            String salt = RandomUtil.randomString(10);
//            user.setSalt(salt);
//            user.setPassword(SecurityUtils.encryptPassword(salt + userUpdateDTO.getPassword()));
//        } else {
//            user.setPassword(null);
//        }
        // 行政区划处理
        if (userUpdateDTO.getCity().length < 4) {
            return R.failed("请选择行政区划");
        }
        // 省份编码
        user.setProvinceCode(userUpdateDTO.getCity()[0]);
        // 城市编码
        user.setCityCode(userUpdateDTO.getCity()[1]);
        // 县区编码
        user.setDistrictCode(userUpdateDTO.getCity()[2]);
        // 街道编码
        user.setStreetCode(userUpdateDTO.getCity()[3]);
        // 获取行政区划详细信息
        String cityInfo = cityService.getCityNames(user.getStreetCode(), ",");
        if (StringUtils.isNotEmpty(cityInfo)) {
            user.setCityInfo(cityInfo);
        }
        boolean result = updateById(user);
        if (!result) {
            return R.failed();
        }

        // 保存用户角色数据
        userRoleService.saveUserRole(user.getId(), userUpdateDTO.getRoles());

        return R.ok();
    }

    /**
     * 删除用户
     *
     * @param idList 用户ID
     * @return 返回结果
     */
    @Override
    public R delete(List<Integer> idList) {
        // 删除ID判空
        if (StringUtils.isEmpty(idList)) {
            return R.failed("删除记录ID不存在");
        }
        // 批量删除
        boolean result = removeBatchByIds(idList);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 重置用户密码
     *
     * @param userResetPwdDTO 参数
     * @return 返回结果
     */
    @Override
    public R resetPwd(UserResetPwdDTO userResetPwdDTO) {
        // 根据用户ID查询用户
        User user = getInfo(userResetPwdDTO.getUserId());
        if (StringUtils.isNull(user)) {
            return R.failed("用户不存在");
        }
//        // 加密盐
//        String salt = RandomUtil.randomString(10);
//        user.setSalt(salt);
//        // 获取租户默认密码参数
//        String userPassword = ParamResolver.getParamValue("USER_DEFAULT_PASSWORD", "123456");
//        // 设置密码
//        user.setPassword(SecurityUtils.encryptPassword(salt + userPassword));
        boolean result = updateById(user);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回结果
     */
    @TenantIgnore
    @Override
    public UserInfoVO selectUserByUserName(String username) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username), false);
        if (StringUtils.isNull(user)) {
            return null;
        }

        // 实例化VO
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        // 获取部门信息
        if (StringUtils.isNotNull(user.getDeptId()) && user.getDeptId() > 0) {
            Dept dept = deptService.getInfo(user.getDeptId());
            if (StringUtils.isNotNull(dept)) {
                userInfoVO.setDeptName(dept.getName());
            }
        }

        // 返回结果
        return userInfoVO;
    }

    /**
     * 根据用户ID获取信息
     *
     * @param userId 用户ID
     * @return 返回结果
     */
    @Override
    public UserProfileVO getProfile(Integer userId) {
        // 根据ID查询用户
        User user = getInfo(userId);
        if (StringUtils.isNull(user)) {
            return null;
        }
        // 实例化用户VO
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, userProfileVO);

        // 用户头像
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            userProfileVO.setAvatar(CommonUtils.getFileURL(user.getAvatar()));
        }

        // 行政区划处理
        if (StringUtils.isNotNull(user.getProvinceCode()) &&
                StringUtils.isNotNull(user.getCityCode()) &&
                StringUtils.isNotNull(user.getDistrictCode())) {
            // 初始化数组
            String[] strings = new String[4];
            strings[0] = user.getProvinceCode();
            strings[1] = user.getCityCode();
            strings[2] = user.getDistrictCode();
            strings[3] = user.getStreetCode();
            userProfileVO.setCity(strings);
        }

        // 获取用户角色
        List<Role> roles = userRoleMapper.getRolesByUserId(userId);
        userProfileVO.setRoles(roles);

        // 获取用户权限
        if (userId.equals(1)) {
            // 超级管理员
            userProfileVO.setPermissions(List.of("*:*:*"));
        } else {
            // 非管理员
            List<String> permissions = menuMapper.getPermissions(userId);
            userProfileVO.setPermissions(permissions);
        }

        // 返回结果
        return userProfileVO;
    }

    /**
     * 更新用户信息
     *
     * @param userProfileUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R updateProfile(UserProfileUpdateDTO userProfileUpdateDTO) {
        // 实例化用户
        User user = new User();
        BeanUtils.copyProperties(userProfileUpdateDTO, user);
        // 用户ID
        user.setId(userProfileUpdateDTO.getUserId());
        // 用户头像
        if (!StringUtils.isEmpty(userProfileUpdateDTO.getAvatar()) && userProfileUpdateDTO.getAvatar().contains(AppConfig.getDomain())) {
            user.setAvatar(userProfileUpdateDTO.getAvatar().replaceAll(AppConfig.getDomain(), ""));
        }
        // 行政区划处理
        if (userProfileUpdateDTO.getCity().length < 4) {
            return R.failed("请选择行政区划");
        }
        // 省份编码
        user.setProvinceCode(userProfileUpdateDTO.getCity()[0]);
        // 城市编码
        user.setCityCode(userProfileUpdateDTO.getCity()[1]);
        // 县区编码
        user.setDistrictCode(userProfileUpdateDTO.getCity()[2]);
        // 街道编码
        user.setStreetCode(userProfileUpdateDTO.getCity()[3]);
        // 获取行政区划详细信息
        String cityInfo = cityService.getCityNames(user.getStreetCode(), ",");
        if (StringUtils.isNotEmpty(cityInfo)) {
            user.setCityInfo(cityInfo);
        }
        boolean result = updateById(user);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 检查用户是否已存在
     *
     * @param username 用户名
     * @param userId   用户ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String username, Integer userId) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .ne(userId > 0, User::getId, userId)
                .eq(User::getDelFlag, 0), false);
        return StringUtils.isNotNull(user);
    }

    /**
     * 导入用户
     *
     * @param userExcelVOList 用户对象
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(List<UserExcelVO> userExcelVOList) {
        // 导入的数据集合判空
        if (StringUtils.isEmpty(userExcelVOList)) {
            return R.failed("导入的数据不能为空");
        }
        try {
            // 实例化用户列表
            List<User> userList = new ArrayList<>();
            // 遍历数据源
            for (UserExcelVO userExcelVO : userExcelVOList) {
                User user = new User();
                BeanUtils.copyProperties(userExcelVO, user);
                // 检查用户名是否已存在，存在则跳过
                if (checkExist(user.getUsername(), 0)) {
                    continue;
                }
                // 置空ID
                user.setId(null);
//                // 设置密码
//                if (StringUtils.isNotEmpty(userExcelVO.getPassword())) {
//                    user.setPassword(SecurityUtils.encryptPassword(userExcelVO.getPassword()));
//                }
                userList.add(user);
            }
            // 数据集合判空
            if (StringUtils.isEmpty(userList)) {
                return R.failed();
            }
            // 批量写入
            boolean result = saveBatch(userList);
            if (!result) {
                return R.failed();
            }
        } catch (Exception e) {
            log.error("数据处理异常：{}", e);
        }
        return R.ok();
    }

    /**
     * 导出用户
     *
     * @return 返回结果
     */
    @Override
    public List<UserExcelVO> exportExcel() {
        // 实例化用户Excel导出列表
        List<UserExcelVO> userExcelVOList = new ArrayList<>();

        // 查询用户列表
        List<User> userList = list(new LambdaQueryWrapper<User>()
                .eq(User::getDelFlag, 0)
                .orderByAsc(User::getId));
        // 用户判空
        if (StringUtils.isNotEmpty(userList)) {
            // 遍历数据源
            for (User user : userList) {
                UserExcelVO userExcelVO = new UserExcelVO();
                BeanUtils.copyProperties(user, userExcelVO);
                userExcelVOList.add(userExcelVO);
            }
        }
        return userExcelVOList;
    }

    /**
     * 更新用户登录次数
     *
     * @param userId 用户ID
     * @return 返回结果
     */
    @Override
    public boolean updateLoginNum(Integer userId) {
        // 获取当前代理对象，通过代理对象调用
//        UserService service = (UserService) AopContext.currentProxy();
        // 第二种方式
//        MybatisTenantContext.set(true);
//        MybatisTenantContext.clear();
        // 获取用户信息
        User userInfo = getInfo(userId);
        if (StringUtils.isNull(userInfo)) {
            return false;
        }
        // 查询用户信息
        User user = new User();
        user.setId(userId);
        // 登录次数+1
        user.setLoginNum(userInfo.getLoginNum() + 1);
        // 登录IP
        user.setLoginIp(IpUtils.getIpAddr());
        // 登录时间
        user.setLoginTime(LocalDateTime.now());
        // 更新并返回结果
        return updateById(user);
    }

    /**
     * 生成文档
     *
     * @param userId 用户ID
     * @return 返回结果
     * @throws IOException       异常处理
     * @throws DocumentException 异常处理
     */
    @Override
    public R generateDocument(Integer userId) throws IOException, DocumentException {
        try {
            // 根据用户ID查询信息
            User user = getInfo(userId);
            if (StringUtils.isNull(user)) {
                return R.failed("用户不存在");
            }

            // 存储目录
            String appPath = AppConfig.getProfile();
            // 创建文件存放目录
            String savePath = "/user/" + DateUtils.dateTime();
            // 创建上传文件目录
            File file = new File(appPath + savePath);
            if (!file.mkdir()) {
                file.mkdirs();
            }

            // 自定义上传文件名
            String fileName = System.currentTimeMillis() + new Random().nextInt(1000) + ".pdf";
            // 目标存储路径
            String filePath = appPath + savePath + "/" + fileName;

            // 创建文本
            Document document = new Document();
            File tempFile = new File(filePath);
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));
            document.open();

            // 设置中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChinese = new Font(bfChinese, 12, Font.BOLD);

            PdfPTable table = new PdfPTable(8); //  设置4列

            table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2, 3});
            table.setTotalWidth(540);// 设置绝对宽度
            table.setLockedWidth(true);// 使绝对宽度模式生效

            // 标题
            Font fontTitle = new Font(bfChinese, 22, Font.BOLD);
            PdfPCell cell = new PdfPCell(new Phrase("个人简历", fontTitle));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setMinimumHeight(50);//设置表格行高
            cell.setBorderWidth(0f);//去除表格的边框
            cell.setColspan(8);
            table.addCell(cell);

            // ========================================== 第1行 =============================================

            // 第1列
            cell = new PdfPCell(new Paragraph("个\r\n人\r\n信\r\n息", fontChinese));
            cell.setRowspan(5);
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 第2行
            cell = new PdfPCell(new Paragraph("姓名：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(user.getRealname(), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            // 第2行
            cell = new PdfPCell(new Paragraph("性别：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(DictResolver.getDictItemName("sys_gender", user.getGender().toString()), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 第2行
            cell = new PdfPCell(new Paragraph("出生年月：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("1998-12-28", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 设置个人头像
            String imagePath = AppConfig.getProfile() + user.getAvatar();      // 图片的绝对路径
            File gifFile = new File(imagePath);
            Image image = null;
            if (gifFile.exists()) {
                // 取得图片对象
                image = Image.getInstance(imagePath);
                image.scaleAbsolute(80, 100);
            }

            // 第7列
            if (StringUtils.isNotNull(image)) {
                cell = new PdfPCell(image);
            } else {
                cell = new PdfPCell(new Paragraph("", fontChinese));
            }
            cell.setRowspan(4);
            cell.setMinimumHeight(30);//设置表格行高
            cell.setUseAscender(true); // 设置可以居中
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // ========================================== 第2行 =============================================

            // 第2行
            cell = new PdfPCell(new Paragraph("民族：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("汉族", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            // 第2行
            cell = new PdfPCell(new Paragraph("籍贯：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(user.getCityInfo(), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 第2行
            cell = new PdfPCell(new Paragraph("政治面貌：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("党员", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // ========================================== 第3行 =============================================

            // 第3行
            cell = new PdfPCell(new Paragraph("身高：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("180cm", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("体重：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("75KG", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("身体状况：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("良好", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // ========================================== 第4行 =============================================

            // 第3行
            cell = new PdfPCell(new Paragraph("联系电话：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(user.getMobile(), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("邮箱：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(user.getEmail(), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("现所在地：", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(user.getAddress(), fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            // ========================================== 第5行 =============================================

            // 第一行
            cell = new PdfPCell(new Paragraph("求职意向：", fontChinese));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setMinimumHeight(30);//设置表格行高
            table.addCell(cell);
            //第一行填写值
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setColspan(6);//合并3列
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setMinimumHeight(30);//设置表格行高
            table.addCell(cell);


            // ========================================== 第6行 =============================================
            PdfPTable table2 = new PdfPTable(4); //  设置4列

            table2.setWidths(new int[]{1, 5, 5, 5});
            table2.setTotalWidth(540);// 设置绝对宽度
            table2.setLockedWidth(true);// 使绝对宽度模式生效

            // 第1列
            cell = new PdfPCell(new Paragraph("教\r\n育\r\n背\r\n景", fontChinese));
            cell.setRowspan(3);
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("时间", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("院校", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("专业", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);

            // ========================================== 第7行 =============================================

            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);

            // ========================================== 第8行 =============================================

            // 第3行
            cell = new PdfPCell(new Paragraph("教育背景:", fontChinese));
            cell.setColspan(3);
            cell.setMinimumHeight(60);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(cell);

            // ========================================== 第8行 =============================================

            PdfPTable table3 = new PdfPTable(4); //  设置4列

            table3.setWidths(new int[]{1, 5, 5, 5});
            table3.setTotalWidth(540);// 设置绝对宽度
            table3.setLockedWidth(true);// 使绝对宽度模式生效

            // 第1列
            cell = new PdfPCell(new Paragraph("工\r\n作\r\n经\r\n历", fontChinese));
            cell.setRowspan(7);
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("时间", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("单位", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("职位", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // ========================================== 第7行 =============================================

            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("工作经历:", fontChinese));
            cell.setColspan(3);
            cell.setMinimumHeight(60);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("工作经历:", fontChinese));
            cell.setColspan(3);
            cell.setMinimumHeight(60);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("工作经历:", fontChinese));
            cell.setColspan(3);
            cell.setMinimumHeight(60);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(cell);

            // ========================================== 能力情况 =============================================

            PdfPTable table4 = new PdfPTable(3); //  设置4列

            table4.setWidths(new int[]{1, 3, 12});
            table4.setTotalWidth(540);// 设置绝对宽度
            table4.setLockedWidth(true);// 使绝对宽度模式生效

            // 第1列
            cell = new PdfPCell(new Paragraph("能\r\n力\r\n情\r\n况", fontChinese));
            cell.setRowspan(3);
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);

            // 第3行
            cell = new PdfPCell(new Paragraph("个人荣誉", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("兴趣特长", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("外语水平", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(30);//设置表格行高
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);

            // ========================================== 能力情况 =============================================

            PdfPTable table5 = new PdfPTable(2); //  设置4列

            table5.setWidths(new int[]{1, 15});
            table5.setTotalWidth(540);// 设置绝对宽度
            table5.setLockedWidth(true);// 使绝对宽度模式生效

            // 第1列
            cell = new PdfPCell(new Paragraph("自\r\n我\r\n评\r\n价", fontChinese));
            cell.setMinimumHeight(100);//设置表格行高
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table5.addCell(cell);
            // 第3行
            cell = new PdfPCell(new Paragraph("", fontChinese));
            cell.setMinimumHeight(100);//设置表格行高
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table5.addCell(cell);

            document.add(table);
            document.add(table2);
            document.add(table3);
            document.add(table4);
            document.add(table5);
            document.close();
            writer.close();

            // 文件加水印
            String waterUrl = PdfUtils.setWaterMark(filePath, AppConfig.getName());
            // 文件URL
            String fileUrl = CommonUtils.getFileURL(waterUrl.replace(AppConfig.getProfile(), ""));

            // 返回结果装配
            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileUrl", fileUrl);

            // 返回结果
            return R.ok(result);
        } catch (Exception e) {
            log.error("文件生成异常：{}", e);
        }
        return R.failed();
    }

}
