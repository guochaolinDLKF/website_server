package com.ydzz.admin.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.business.entity.Orders;
import com.ydzz.admin.business.entity.PaymentCallbackFailure;
import com.ydzz.admin.business.entity.UserBenefit;
import com.ydzz.admin.business.service.BusinessQueryService;
import com.ydzz.admin.common.PageResult;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 业务数据查询接口（只读 zhouyi）：订单、商品、权益、支付异常监控。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "后台-业务数据")
public class BusinessQueryController {

    private final BusinessQueryService businessQueryService;

    public BusinessQueryController(BusinessQueryService businessQueryService) {
        this.businessQueryService = businessQueryService;
    }

    // ---------------- 订单 ----------------

    @Operation(summary = "订单分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "order:list")
    @GetMapping("/order/page")
    public Result<PageResult<Orders>> orderPage(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String orderStatus,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                                @RequestParam(required = false) String sortOrder) {
        return Result.success(PageResult.of(
                businessQueryService.pageOrders(current, size, keyword, orderStatus, startTime, endTime, sortOrder)));
    }

    @Operation(summary = "订单详情（含支付/退款）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "order:detail")
    @GetMapping("/order/{id}")
    public Result<Map<String, Object>> orderDetail(@PathVariable Long id) {
        return Result.success(businessQueryService.orderDetail(id));
    }

    // 商品（含增删改查）已独立到 GoodsController。

    // ---------------- 权益 ----------------

    @Operation(summary = "权益分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "benefit:list")
    @GetMapping("/benefit/page")
    public Result<PageResult<UserBenefit>> benefitPage(@RequestParam(defaultValue = "1") long current,
                                                       @RequestParam(defaultValue = "10") long size,
                                                       @RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) Integer isValid) {
        return Result.success(PageResult.of(businessQueryService.pageBenefits(current, size, userId, isValid)));
    }

    // ---------------- 支付异常 ----------------

    @Operation(summary = "支付异常分页")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "payfail:list")
    @GetMapping("/payment-failure/page")
    public Result<PageResult<PaymentCallbackFailure>> paymentFailurePage(@RequestParam(defaultValue = "1") long current,
                                                                         @RequestParam(defaultValue = "10") long size,
                                                                         @RequestParam(required = false) String status,
                                                                         @RequestParam(required = false) String failureType) {
        return Result.success(PageResult.of(
                businessQueryService.pagePaymentFailures(current, size, status, failureType)));
    }
}
