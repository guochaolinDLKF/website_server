package com.ydzz.admin.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydzz.admin.business.mapper.DashboardMapper;
import com.ydzz.admin.business.mapper.OrdersMapper;
import com.ydzz.admin.business.mapper.UserBenefitMapper;
import com.ydzz.admin.business.mapper.ZhouyiUserMapper;
import com.ydzz.admin.business.entity.Orders;
import com.ydzz.admin.business.entity.UserBenefit;
import com.ydzz.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据驾驶舱服务（读 zhouyi）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class DashboardService {

    private final ZhouyiUserMapper userMapper;
    private final OrdersMapper ordersMapper;
    private final UserBenefitMapper userBenefitMapper;
    private final DashboardMapper dashboardMapper;

    public DashboardService(ZhouyiUserMapper userMapper, OrdersMapper ordersMapper,
                            UserBenefitMapper userBenefitMapper, DashboardMapper dashboardMapper) {
        this.userMapper = userMapper;
        this.ordersMapper = ordersMapper;
        this.userBenefitMapper = userBenefitMapper;
        this.dashboardMapper = dashboardMapper;
    }

    /** 驾驶舱核心卡片 */
    public Map<String, Object> overview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        long totalUsers = userMapper.selectCount(null);
        LambdaQueryWrapper<User> todayUserQw = new LambdaQueryWrapper<>();
        todayUserQw.ge(User::getCreateTime, todayStart);
        long todayNewUsers = userMapper.selectCount(todayUserQw);

        LambdaQueryWrapper<User> vipQw = new LambdaQueryWrapper<>();
        vipQw.eq(User::getIsVip, 1);
        long vipUsers = userMapper.selectCount(vipQw);

        BigDecimal totalIncome = nullSafe(dashboardMapper.sumIncomeTotal());
        BigDecimal todayIncome = nullSafe(dashboardMapper.sumIncomeSince(todayStart));

        LambdaQueryWrapper<Orders> todayOrderQw = new LambdaQueryWrapper<>();
        todayOrderQw.ge(Orders::getCreateTime, todayStart);
        long todayOrders = ordersMapper.selectCount(todayOrderQw);

        LambdaQueryWrapper<UserBenefit> validBenefitQw = new LambdaQueryWrapper<>();
        validBenefitQw.eq(UserBenefit::getIsValid, 1);
        long validBenefits = userBenefitMapper.selectCount(validBenefitQw);

        long paidUsers = nullSafe(dashboardMapper.countPaidUsers());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("todayNewUsers", todayNewUsers);
        data.put("vipUsers", vipUsers);
        data.put("totalIncome", totalIncome);
        data.put("todayIncome", todayIncome);
        data.put("todayOrders", todayOrders);
        data.put("validBenefits", validBenefits);
        data.put("paidUsers", paidUsers);
        data.put("paidPenetration", ratio(paidUsers, totalUsers));
        data.put("vipRatio", ratio(vipUsers, totalUsers));
        return data;
    }

    public List<Map<String, Object>> userTrend(int days) {
        return dashboardMapper.userTrend(startOf(days));
    }

    public List<Map<String, Object>> incomeTrend(int days) {
        return dashboardMapper.incomeTrend(startOf(days));
    }

    public List<Map<String, Object>> orderTrend(int days) {
        return dashboardMapper.orderTrend(startOf(days));
    }

    public List<Map<String, Object>> goodsRank() {
        return dashboardMapper.goodsRank();
    }

    public List<Map<String, Object>> channelDist() {
        return dashboardMapper.channelDist();
    }

    public List<Map<String, Object>> benefitTypeDist() {
        return dashboardMapper.benefitTypeDist();
    }

    /**
     * 活跃数据（短信登录活跃）。
     *
     * @param type hour=今日每小时；week=过去7天（按天）；month=本月（按天）
     * @return points[{label,value}] + 汇总（latest/mean/sum/dod 日环比/wow 周同比）
     */
    public Map<String, Object> activeData(String type) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> points = new java.util.ArrayList<>();
        boolean daily = !"hour".equalsIgnoreCase(type);

        if ("hour".equalsIgnoreCase(type)) {
            // 今日每小时（0 点至当前小时）
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.plusDays(1).atStartOfDay();
            Map<Integer, Long> m = new LinkedHashMap<>();
            for (Map<String, Object> row : dashboardMapper.activeByHour(start, end)) {
                m.put(((Number) row.get("k")).intValue(), ((Number) row.get("v")).longValue());
            }
            int curHour = java.time.LocalTime.now().getHour();
            for (int h = 0; h <= curHour; h++) {
                points.add(point(String.format("%02d:00", h), m.getOrDefault(h, 0L)));
            }
        } else {
            // week=近7天；month=本月1号至今
            LocalDate startDate = "month".equalsIgnoreCase(type) ? today.withDayOfMonth(1) : today.minusDays(6);
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = today.plusDays(1).atStartOfDay();
            Map<String, Long> m = new LinkedHashMap<>();
            for (Map<String, Object> row : dashboardMapper.activeByDay(start, end)) {
                m.put(String.valueOf(row.get("k")).substring(0, 10), ((Number) row.get("v")).longValue());
            }
            for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
                String key = d.toString();
                points.add(point(key.substring(5).replace('-', '/'), m.getOrDefault(key, 0L)));
            }
        }

        long latest = points.isEmpty() ? 0L : ((Number) points.get(points.size() - 1).get("value")).longValue();
        long prev = points.size() < 2 ? 0L : ((Number) points.get(points.size() - 2).get("value")).longValue();
        long total = points.stream().mapToLong(p -> ((Number) p.get("value")).longValue()).sum();
        double mean = points.isEmpty() ? 0d : (double) total / points.size();

        // 周同比：仅日维度可比——最新当天 vs 7 天前同日
        BigDecimal wow = null;
        if (daily) {
            LocalDate lastSevenStart = today.minusDays(7);
            long base7 = nullSafe(dashboardMapper.activeCount(lastSevenStart.atStartOfDay(), lastSevenStart.plusDays(1).atStartOfDay()));
            wow = pct(latest, base7);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("points", points);
        data.put("latest", latest);
        data.put("latestLabel", points.isEmpty() ? "" : points.get(points.size() - 1).get("label"));
        data.put("mean", BigDecimal.valueOf(mean).setScale(2, RoundingMode.HALF_UP));
        data.put("sum", total);
        data.put("dod", pct(latest, prev));   // 环比（相邻点）
        data.put("wow", wow);                  // 周同比（仅 week/month）
        return data;
    }

    private Map<String, Object> point(String label, long value) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("label", label);
        p.put("value", value);
        return p;
    }

    /** 变化率百分比（(cur-base)/base*100，保留2位）；base<=0 返回 null */
    private BigDecimal pct(long cur, long base) {
        if (base <= 0) {
            return null;
        }
        return BigDecimal.valueOf((cur - base) * 100.0 / base).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDateTime startOf(int days) {
        int d = days <= 0 ? 30 : days;
        return LocalDate.now().minusDays(d - 1L).atStartOfDay();
    }

    private BigDecimal nullSafe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private long nullSafe(Long v) {
        return v == null ? 0L : v;
    }

    /** 比率，保留 4 位小数（0~1） */
    private BigDecimal ratio(long part, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(part).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }
}
