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

package com.xiaomayi.admin.controller;

import cn.hutool.core.util.RandomUtil;
import com.itextpdf.text.DocumentException;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.excel.annotation.RequestExcel;
import com.xiaomayi.excel.annotation.ResponseExcel;
import com.xiaomayi.logger.annotation.RequestLog;
import com.xiaomayi.logger.enums.RequestType;
import com.xiaomayi.security.utils.SecurityUtils;
import com.xiaomayi.system.dto.user.*;
import com.xiaomayi.system.service.UserService;
import com.xiaomayi.system.utils.ParamResolver;
import com.xiaomayi.system.vo.user.UserExcelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 查询分页列表
     *
     * @param userPageDTO 查询条件
     * @return 返回结果
     */
    @Operation(summary = "查询分页列表", description = "查询分页列表")
    @PreAuthorize("@pms.hasAuthority('sys:user:page')")
    @GetMapping("/page")
    public R page(UserPageDTO userPageDTO) {
        return R.ok(userService.page(userPageDTO));
    }

    /**
     * 查询数据列表
     *
     * @param userListDTO 查询条件
     * @return 返回结果
     */
    @Operation(summary = "查询数据列表", description = "查询数据列表")
    @PreAuthorize("@pms.hasAuthority('sys:user:list')")
    @GetMapping("/list")
    public R getList(UserListDTO userListDTO) {
        return R.ok(userService.getList(userListDTO));
    }

    /**
     * 根据ID查询详情
     *
     * @param id 用户ID
     * @return 返回结果
     */
    @Operation(summary = "根据ID查询详情", description = "根据ID查询详情")
    @PreAuthorize("@pms.hasAuthority('sys:user:detail')")
    @GetMapping("/detail/{id}")
    public R getDetail(@PathVariable("id") Integer id) {
        return R.ok(userService.getDetail(id));
    }

    /**
     * 添加用户
     *
     * @param userAddDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "添加用户", description = "添加用户")
    @RequestLog(title = "添加用户", type = RequestType.INSERT, exclude = {"password"})
    @PreAuthorize("@pms.hasAuthority('sys:user:add')")
    @PostMapping("/add")
    public R add(@RequestBody @Validated UserAddDTO userAddDTO) {
        // 登录密码
        String password = userAddDTO.getPassword();
        if (StringUtils.isNotEmpty(password)) {
            // 加密盐
            String salt = RandomUtil.randomString(10);
            userAddDTO.setSalt(salt);
            // 密码基于SpringSecurity加密处理
            userAddDTO.setPassword(SecurityUtils.encryptPassword(salt + password));
        } else {
            userAddDTO.setPassword(null);
        }
        // 租户ID
        userAddDTO.setTenantId(SecurityUtils.getTenantId());
        return userService.add(userAddDTO);
    }

    /**
     * 更新用户
     *
     * @param userUpdateDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "更新用户", description = "更新用户")
    @RequestLog(title = "更新用户", type = RequestType.UPDATE, exclude = {"password"})
    @PreAuthorize("@pms.hasAuthority('sys:user:update')")
    @PutMapping("/update")
    public R update(@RequestBody @Validated UserUpdateDTO userUpdateDTO) {
        // 登录密码
        String password = userUpdateDTO.getPassword();
        if (StringUtils.isNotEmpty(password)) {
            // 加密盐
            String salt = RandomUtil.randomString(10);
            userUpdateDTO.setSalt(salt);
            // 密码基于SpringSecurity加密处理
            userUpdateDTO.setPassword(SecurityUtils.encryptPassword(salt + password));
        } else {
            userUpdateDTO.setPassword(null);
        }
        return userService.update(userUpdateDTO);
    }

    /**
     * 删除用户
     *
     * @param id 记录ID
     * @return 返回结果
     */
    @Operation(summary = "删除用户", description = "删除用户")
    @RequestLog(title = "删除用户", type = RequestType.DELETE)
    @PreAuthorize("@pms.hasAuthority('sys:user:delete')")
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        List<Integer> idList = Collections.singletonList(id);
        return userService.delete(idList);
    }

    /**
     * 批量删除用户
     *
     * @param idList 记录ID
     * @return 返回结果
     */
    @Operation(summary = "批量删除用户", description = "批量删除用户")
    @RequestLog(title = "批量删除用户", type = RequestType.BATCH_DELETE)
    @PreAuthorize("@pms.hasAuthority('sys:user:batchDelete')")
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestBody @Validated List<Integer> idList) {
        return userService.delete(idList);
    }

    /**
     * 重置用户密码
     *
     * @param userResetPwdDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "重置用户密码", description = "重置用户密码")
    @RequestLog(title = "重置用户密码", type = RequestType.RESET_PWD)
    @PreAuthorize("@pms.hasAuthority('sys:user:resetPwd')")
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody @Validated UserResetPwdDTO userResetPwdDTO) {
        // 加密盐
        String salt = RandomUtil.randomString(10);
        userResetPwdDTO.setSalt(salt);
        // 获取租户默认密码参数
        String userPassword = ParamResolver.getParamValue("USER_DEFAULT_PASSWORD", "123456");
        // 设置密码
        userResetPwdDTO.setPassword(SecurityUtils.encryptPassword(salt + userPassword));
        return userService.resetPwd(userResetPwdDTO);
    }

    /**
     * 导入用户
     *
     * @param userExcelVOList 导入Excel
     * @return 返回结果
     */
    @Operation(summary = "导入用户", description = "导入用户")
    @RequestLog(title = "导入用户", type = RequestType.IMPORT)
    @PreAuthorize("@pms.hasAuthority('sys:user:import')")
    @PostMapping("/import")
    public R importExcel(@RequestExcel List<UserExcelVO> userExcelVOList) {
        if (AppConfig.isDemo()) {
            return R.failed("演示环境，禁止操作");
        }
        return userService.importExcel(userExcelVOList);
    }

    /**
     * 导出用户
     *
     * @return 返回结果
     */
    @Operation(summary = "导出用户", description = "导出用户")
    @RequestLog(title = "导出用户", type = RequestType.EXPORT)
    @PreAuthorize("@pms.hasAuthority('sys:user:export')")
    @ResponseExcel(name = "用户信息", sheetName = "用户信息")
    @GetMapping("/export")
    public List<UserExcelVO> exportExcel() {
        return userService.exportExcel();
    }

    /**
     * 生成文档
     * 特别备注：此处文档生成仅为提供以编码的方式生成PDF文件案例参考，不涉及任何业务；
     * 以此举一反三，理解逻辑后可以根据实际业务生成任何你所需要的文档，包括动态追加写入数据、图片等等；
     *
     * @param userId 用户ID
     * @return 返回结果
     * @throws IOException       异常处理
     * @throws DocumentException 异常处理
     */
    @Operation(summary = "生成文档", description = "生成文档")
    @RequestLog(title = "生成文档", type = RequestType.OTHER)
    @PreAuthorize("@pms.hasAuthority('sys:user:document')")
    @GetMapping("/document/{userId}")
    public R generateDocument(@PathVariable("userId") Integer userId) throws IOException, DocumentException {
        return userService.generateDocument(userId);
    }

}
