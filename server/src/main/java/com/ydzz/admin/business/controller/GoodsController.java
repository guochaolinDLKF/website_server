package com.ydzz.admin.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.business.entity.Item;
import com.ydzz.admin.business.service.BusinessQueryService;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.admin.log.OperationLog;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理接口（zhouyi.products）。
 *
 * <p>数据表为 zhouyi.items（含价格 item_price、折扣价 item_discount）。<br/>
 * 查询：拥有 {@code goods:list} 权限即可（运营等角色可见）。<br/>
 * 增删改：写 zhouyi，需要 {@code goods:edit} 权限，
 * 通过 {@link SaCheckPermission} 在后端强制校验（不依赖前端隐藏按钮）。ID 由数据库自增、不可修改。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/goods")
@Tag(name = "后台-商品管理")
public class GoodsController {

    private final BusinessQueryService businessQueryService;

    public GoodsController(BusinessQueryService businessQueryService) {
        this.businessQueryService = businessQueryService;
    }

    @Operation(summary = "商品分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "goods:list")
    @GetMapping("/page")
    public Result<PageResult<Item>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer itemType,
                                         @RequestParam(required = false) String sortOrder) {
        return Result.success(PageResult.of(businessQueryService.pageGoods(current, size, keyword, itemType, sortOrder)));
    }

    @Operation(summary = "新增/编辑商品（需 goods:edit 权限）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "goods:edit")
    @OperationLog(module = "商品管理", operation = "保存商品")
    @PostMapping
    public Result<Long> save(@RequestBody Item item) {
        return Result.success("保存成功", businessQueryService.saveGoods(item));
    }

    @Operation(summary = "删除商品（需 goods:edit 权限）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "goods:edit")
    @OperationLog(module = "商品管理", operation = "删除商品")
    @DeleteMapping("/{id}")
    public Result<String> remove(@PathVariable Long id) {
        businessQueryService.deleteGoods(id);
        return Result.success("删除成功", "ok");
    }
}
