package com.ydzz.admin.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ydzz.admin.business.service.DashboardService;
import com.ydzz.admin.config.AdminStpUtil;
import com.ydzz.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据驾驶舱接口（读 zhouyi）。
 *
 * <p>对外暴露 {@link DashboardService} 的聚合统计能力，统一 {@code dashboard:view} 权限。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "后台-数据驾驶舱")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "驾驶舱核心卡片")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(dashboardService.overview());
    }

    @Operation(summary = "玩家概览卡片（今日含环比同比 + 当月累计）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/player-stats")
    public Result<Map<String, Object>> playerStats() {
        return Result.success(dashboardService.playerStats());
    }

    @Operation(summary = "新增用户趋势")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/user-trend")
    public Result<List<Map<String, Object>>> userTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(dashboardService.userTrend(days));
    }

    @Operation(summary = "活跃数据（hour=今日每小时/week=过去7天/month=本月）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/active")
    public Result<Map<String, Object>> active(@RequestParam(defaultValue = "week") String type) {
        return Result.success(dashboardService.activeData(type));
    }

    @Operation(summary = "新增用户次日留存（按注册日区间，或最近 N 个注册日）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/retention")
    public Result<Map<String, Object>> retention(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "30") int days) {
        if (start != null && end != null) {
            return Result.success(dashboardService.nextDayRetention(start, end));
        }
        return Result.success(dashboardService.nextDayRetention(days));
    }

    @Operation(summary = "人均在线时长/启动次数（按天，含日环比同比、均值总和）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/online-stats")
    public Result<Map<String, Object>> onlineStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "30") int days) {
        if (start != null && end != null) {
            return Result.success(dashboardService.onlineStats(start, end));
        }
        return Result.success(dashboardService.onlineStats(days));
    }

    @Operation(summary = "实时在线人数（按5分钟，今日+昨日VS）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/realtime-online")
    public Result<Map<String, Object>> realtimeOnline(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "5") int bucket) {
        return Result.success(dashboardService.realtimeOnline(date, bucket));
    }

    @Operation(summary = "日均在线人数趋势（dim=day/week/month）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/online-trend")
    public Result<Map<String, Object>> onlineTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.onlineTrend(dim, start, end));
    }

    @Operation(summary = "收入趋势")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/income-trend")
    public Result<List<Map<String, Object>>> incomeTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(dashboardService.incomeTrend(days));
    }

    @Operation(summary = "订单趋势")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> orderTrend(@RequestParam(defaultValue = "30") int days) {
        return Result.success(dashboardService.orderTrend(days));
    }

    @Operation(summary = "商品销售排行")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/goods-rank")
    public Result<List<Map<String, Object>>> goodsRank() {
        return Result.success(dashboardService.goodsRank());
    }

    @Operation(summary = "渠道分布")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/channel-dist")
    public Result<List<Map<String, Object>>> channelDist() {
        return Result.success(dashboardService.channelDist());
    }

    @Operation(summary = "权益类型分布")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/benefit-dist")
    public Result<List<Map<String, Object>>> benefitTypeDist() {
        return Result.success(dashboardService.benefitTypeDist());
    }
}
