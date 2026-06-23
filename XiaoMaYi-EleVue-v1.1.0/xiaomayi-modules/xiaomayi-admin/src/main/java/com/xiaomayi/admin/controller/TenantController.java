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
import com.xiaomayi.core.utils.R;
import com.xiaomayi.logger.annotation.RequestLog;
import com.xiaomayi.logger.enums.RequestType;
import com.xiaomayi.security.utils.SecurityUtils;
import com.xiaomayi.system.dto.tenant.TenantAccountDTO;
import com.xiaomayi.system.dto.tenant.TenantAddDTO;
import com.xiaomayi.system.dto.tenant.TenantPageDTO;
import com.xiaomayi.system.dto.tenant.TenantUpdateDTO;
import com.xiaomayi.system.service.TenantService;
import com.xiaomayi.system.utils.ParamResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 租户 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-07-05
 */
@RestController
@RequestMapping("/tenant")
@Tag(name = "租户管理", description = "租户管理")
@AllArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 查询分页列表
     *
     * @param tenantPageDTO 查询条件
     * @return 返回结果
     */
    @Operation(summary = "查询分页列表", description = "查询分页列表")
    @GetMapping("/page")
    public R page(TenantPageDTO tenantPageDTO) {
        return R.ok(tenantService.page(tenantPageDTO));
    }

    /**
     * 根据ID查询详情
     *
     * @param id 租户ID
     * @return 返回结果
     */
    @Operation(summary = "根据ID查询详情", description = "根据ID查询详情")
    @GetMapping("/detail/{id}")
    public R getDetail(@PathVariable("id") Integer id) {
        return R.ok(tenantService.getDetail(id));
    }

    /**
     * 添加租户
     *
     * @param tenantAddDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "添加租户", description = "添加租户")
    @RequestLog(title = "添加租户", type = RequestType.INSERT)
    @PreAuthorize("@pms.hasAuthority('sys:tenant:add')")
    @PostMapping("/add")
    public R add(@RequestBody @Validated TenantAddDTO tenantAddDTO) {
        return tenantService.add(tenantAddDTO);
    }

    /**
     * 更新租户
     *
     * @param tenantUpdateDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "更新租户", description = "更新租户")
    @RequestLog(title = "更新租户", type = RequestType.UPDATE)
    @PreAuthorize("@pms.hasAuthority('sys:tenant:update')")
    @PutMapping("/update")
    public R update(@RequestBody @Validated TenantUpdateDTO tenantUpdateDTO) {
        return tenantService.update(tenantUpdateDTO);
    }

    /**
     * 删除租户
     *
     * @param id 记录ID
     * @return 返回结果
     */
    @Operation(summary = "删除租户", description = "删除租户")
    @RequestLog(title = "删除租户", type = RequestType.DELETE)
    @PreAuthorize("@pms.hasAuthority('sys:tenant:delete')")
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        return tenantService.delete(id);
    }

    /**
     * 批量删除租户
     *
     * @param idList 记录ID
     * @return 返回结果
     */
    @Operation(summary = "批量删除租户", description = "批量删除租户")
    @RequestLog(title = "批量删除租户", type = RequestType.BATCH_DELETE)
    @PreAuthorize("@pms.hasAuthority('sys:tenant:batchDelete')")
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestBody @Validated List<Integer> idList) {
        return tenantService.batchDelete(idList);
    }

    /**
     * 创建租户账号
     *
     * @param tenantAccountDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "创建租户账号", description = "创建租户账号")
    @RequestLog(title = "创建租户账号", type = RequestType.ACCOUNT)
    @PreAuthorize("@pms.hasAuthority('sys:tenant:account')")
    @PostMapping("/account")
    public R addAccount(@RequestBody @Validated TenantAccountDTO tenantAccountDTO) {
        // 加密盐
        String salt = RandomUtil.randomString(10);
        tenantAccountDTO.setSalt(salt);
        // 获取租户默认密码参数
        String tenantPassword = ParamResolver.getParamValue("TENANT_DEFAULT_PASSWORD", "123456");
        // 设置密码
        tenantAccountDTO.setPassword(SecurityUtils.encryptPassword(salt + tenantPassword));
        return tenantService.addAccount(tenantAccountDTO);
    }

}
