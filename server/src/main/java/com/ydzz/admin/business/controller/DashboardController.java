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

    @Operation(summary = "付费概况（付费金额/付费人数/付费率/ARPU，含日环比、周同比）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/pay-overview")
    public Result<Map<String, Object>> payOverview() {
        return Result.success(dashboardService.payOverview());
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

    @Operation(summary = "新增用户次日留存（按注册日区间，dim=day/week/month；或最近 N 个注册日）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/retention")
    public Result<Map<String, Object>> retention(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(defaultValue = "30") int days) {
        if (start != null && end != null) {
            return Result.success(dashboardService.nextDayRetention(start, end, dim));
        }
        return Result.success(dashboardService.nextDayRetention(days));
    }

    @Operation(summary = "人均在线时长/启动次数（dim=day/week/month，含日环比同比、均值总和）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/online-stats")
    public Result<Map<String, Object>> onlineStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(defaultValue = "30") int days) {
        if (start != null && end != null) {
            return Result.success(dashboardService.onlineStats(start, end, dim));
        }
        return Result.success(dashboardService.onlineStats(days, dim));
    }

    @Operation(summary = "实时在线人数（单日今日+昨日VS；传 start/end 跨多日则区间连续+前一周期VS）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/realtime-online")
    public Result<Map<String, Object>> realtimeOnline(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "5") int bucket) {
        // 选了真正的多日区间：返回区间连续序列（保持粒度）+ 前一等长周期 VS
        if (start != null && end != null && !start.isEqual(end)) {
            return Result.success(dashboardService.realtimeOnlineRange(start, end, bucket));
        }
        // 单日：优先 date，其次 end（面板选单日时回传的是 end）
        LocalDate day = date != null ? date : end;
        return Result.success(dashboardService.realtimeOnline(day, bucket));
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

    @Operation(summary = "实时新增用户（单日今日+昨日VS；传 start/end 跨多日则区间连续+前一周期VS；cumulative=1 累计）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/realtime-new-users")
    public Result<Map<String, Object>> realtimeNewUsers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "5") int bucket,
            @RequestParam(defaultValue = "0") int cumulative) {
        boolean cum = cumulative == 1;
        if (start != null && end != null && !start.isEqual(end)) {
            return Result.success(dashboardService.realtimeNewUsersRange(start, end, bucket, cum));
        }
        LocalDate day = date != null ? date : end;
        return Result.success(dashboardService.realtimeNewUsers(day, bucket, cum));
    }

    @Operation(summary = "新增用户趋势（dim=day/week/month；cumulative=1 运行累计）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/new-users-trend")
    public Result<Map<String, Object>> newUsersTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int cumulative) {
        return Result.success(dashboardService.newUsersTrend(dim, start, end, cumulative == 1));
    }

    @Operation(summary = "实时付费金额（单日今日+昨日VS；传 start/end 跨多日则区间连续+前一周期VS；cumulative=1 累计）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/realtime-revenue")
    public Result<Map<String, Object>> realtimeRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "5") int bucket,
            @RequestParam(defaultValue = "0") int cumulative) {
        boolean cum = cumulative == 1;
        if (start != null && end != null && !start.isEqual(end)) {
            return Result.success(dashboardService.realtimeRevenueRange(start, end, bucket, cum));
        }
        LocalDate day = date != null ? date : end;
        return Result.success(dashboardService.realtimeRevenue(day, bucket, cum));
    }

    @Operation(summary = "付费金额趋势（dim=day/week/month；cumulative=1 运行累计）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/revenue-trend")
    public Result<Map<String, Object>> revenueTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int cumulative) {
        return Result.success(dashboardService.revenueTrend(dim, start, end, cumulative == 1));
    }

    @Operation(summary = "新增玩家数量及占比（dim=day/week/month；柱=新增玩家，线=新增/活跃占比%）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/new-players-trend")
    public Result<Map<String, Object>> newPlayersTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.newPlayersTrend(dim, start, end));
    }

    @Operation(summary = "新增设备数量及占比（dim=day/week/month；柱=激活设备，线=新增/累计设备占比%）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/new-devices-trend")
    public Result<Map<String, Object>> newDevicesTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.newDevicesTrend(dim, start, end));
    }

    @Operation(summary = "各渠道新增玩家（dim=day/week/month；返回各渠道每期新增数，前端渲染堆积/占比）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/channel-new-players")
    public Result<Map<String, Object>> channelNewPlayers(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.channelNewPlayers(dim, start, end));
    }

    @Operation(summary = "付费总体趋势（dim=day/week/month；柱=付费金额，线=付费率%）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/pay-trend")
    public Result<Map<String, Object>> payTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.payTrend(dim, start, end));
    }

    @Operation(summary = "ARPU 与 ARPPU 趋势（dim=day/week/month；双数值线）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/arpu-trend")
    public Result<Map<String, Object>> arpuTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.arpuTrend(dim, start, end));
    }

    @Operation(summary = "付费人数新老用户分层（dim=day/week/month；双数值线：新/老用户付费人数）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/pay-user-segment-trend")
    public Result<Map<String, Object>> payUserSegmentTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.payUserSegmentTrend(dim, start, end));
    }

    @Operation(summary = "充值成功率与失败率（dim=day/week/month；双百分比线）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/payment-success-rate-trend")
    public Result<Map<String, Object>> paymentSuccessRateTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.paymentSuccessRateTrend(dim, start, end));
    }

    @Operation(summary = "注册首日付费转化率（dim=day/week/month；单百分比线）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/first-day-pay-trend")
    public Result<Map<String, Object>> firstDayPayTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.firstDayPayTrend(dim, start, end));
    }

    @Operation(summary = "注册后阶段累计付费人数（同期群表格；dim=day/week/month，maxStage=注册后最大阶段数）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/reg-stage-pay-cohort")
    public Result<Map<String, Object>> regStagePayCohort(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "7") int maxStage) {
        return Result.success(dashboardService.regStagePayCohort(dim, start, end, maxStage));
    }

    @Operation(summary = "付费流水构成（按权益）：所选区间成功支付金额按权益拆分，用于环形图")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/pay-composition-benefit")
    public Result<Map<String, Object>> payCompositionByBenefit(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.payCompositionByBenefit(start, end));
    }

    @Operation(summary = "付费流水构成（按权益）趋势（dim=day/week/month；多折线，每条线一个权益）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/pay-benefit-trend")
    public Result<Map<String, Object>> payAmountByBenefitTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.payAmountByBenefitTrend(dim, start, end));
    }

    @Operation(summary = "商品复购率（按权益）趋势（dim=day/week/month；多折线，含总体线）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/product-repurchase-trend")
    public Result<Map<String, Object>> productRepurchaseTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.productRepurchaseTrend(dim, start, end));
    }

    @Operation(summary = "每日活跃数据（dim=day/week/month；DAU/WAU/MAU 三柱 + DAU/MAU 粘性折线）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/active-trend")
    public Result<Map<String, Object>> activeTrend(
            @RequestParam(defaultValue = "day") String dim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.activeTrend(dim, start, end));
    }

    @Operation(summary = "活跃用户生命周期天数构成（环形图）")
    @SaCheckPermission(type = AdminStpUtil.TYPE, value = "dashboard:view")
    @GetMapping("/active-lifecycle-dist")
    public Result<Map<String, Object>> activeLifecycleDist(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(dashboardService.activeLifecycleDist(start, end));
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
