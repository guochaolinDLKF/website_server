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
