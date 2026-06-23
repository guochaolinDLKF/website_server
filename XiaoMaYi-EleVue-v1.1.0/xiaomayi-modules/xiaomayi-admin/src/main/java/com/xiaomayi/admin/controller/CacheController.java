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

import com.xiaomayi.core.utils.R;
import com.xiaomayi.system.dto.cache.CacheValueDTO;
import com.xiaomayi.system.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 系统缓存 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-11
 */
@RestController
@RequestMapping("/cache")
@Tag(name = "系统监控", description = "系统监控")
@AllArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    /**
     * 获取缓存信息
     *
     * @return 返回结果
     */
    @Operation(summary = "获取缓存信息", description = "获取缓存信息")
    @PreAuthorize("@pms.hasAuthority('sys:cache:info')")
    @GetMapping("/info")
    public R getCacheInfo() {
        return cacheService.getCacheInfo();
    }

    /**
     * 获取缓存名称列表
     *
     * @return 返回结果
     */
    @Operation(summary = "获取缓存名称列表", description = "获取缓存名称列表")
    @GetMapping("/getNames")
    public R getCacheNames() {
        return R.ok(cacheService.getCacheNames());
    }

    /**
     * 获取缓存KEY列表
     *
     * @param cacheName 缓存米高程
     * @return 返回结果
     */
    @Operation(summary = "获取缓存KEY列表", description = "获取缓存KEY列表")
    @GetMapping("/getKeys/{cacheName}")
    public R getCacheKeys(@PathVariable String cacheName) {
        return cacheService.getCacheKeys(cacheName);
    }

    /**
     * 获取缓存值
     *
     * @param cacheValueDTO 查询条件
     * @return 返回结果
     */
    @Operation(summary = "获取缓存值", description = "获取缓存值")
    @GetMapping("/getValue")
    public R getCacheValue(@Validated CacheValueDTO cacheValueDTO) {
        return cacheService.getCacheValue(cacheValueDTO);
    }

    /**
     * 根据名称删除缓存
     *
     * @param cacheName 缓存名称
     * @return 返回结果
     */
    @Operation(summary = "根据名称删除缓存", description = "根据名称删除缓存")
    @DeleteMapping("/deleteCacheName/{cacheName}")
    public R deleteCacheName(@PathVariable String cacheName) {
        return cacheService.deleteCacheName(cacheName);
    }

    /**
     * 根据缓存KEY删除缓存
     *
     * @param cacheKey 缓存KEY
     * @return 返回结果
     */
    @Operation(summary = "根据缓存KEY删除缓存", description = "根据缓存KEY删除缓存")
    @DeleteMapping("/deleteCacheKey/{cacheKey}")
    public R deleteCacheKey(@PathVariable String cacheKey) {
        return cacheService.deleteCacheKey(cacheKey);
    }

    /**
     * 删除全部缓存
     *
     * @return 返回结果
     */
    @Operation(summary = "删除全部缓存", description = "删除全部缓存")
    @DeleteMapping("/deleteCacheAll")
    public R deleteCacheAll() {
        return cacheService.deleteCacheAll();
    }

}
