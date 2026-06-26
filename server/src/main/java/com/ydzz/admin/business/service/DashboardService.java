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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 玩家概览卡片：今日（新增/活跃/付费玩家/付费金额，含日环比、周同比）+ 当月（同四项，仅当月累计值）。
     *
     * <p>日环比=今日 vs 昨日；周同比=今日 vs 上周同一天。当月为本月 1 号至今的累计。</p>
     */
    public Map<String, Object> playerStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        // 今日与对比日均采用「当日 00:00 ~ 当前时刻」的等长时间窗，保证环比/同比口径一致、可比
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime yestStart = today.minusDays(1).atStartOfDay();
        LocalDateTime yestNow = now.minusDays(1);          // 昨日同一时刻
        LocalDateTime weekAgoStart = today.minusDays(7).atStartOfDay();
        LocalDateTime weekAgoNow = now.minusDays(7);        // 上周同日同一时刻
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        Map<String, Object> data = new LinkedHashMap<>();

        // ---- 今日（日环比 vs 昨日同时段；周同比 vs 上周同日同时段）----
        data.put("todayNewUsers", metric(
                nullSafe(dashboardMapper.countNewUsers(todayStart, now)),
                nullSafe(dashboardMapper.countNewUsers(yestStart, yestNow)),
                nullSafe(dashboardMapper.countNewUsers(weekAgoStart, weekAgoNow))));
        data.put("todayActive", metric(
                nullSafe(dashboardMapper.activeCount(todayStart, now)),
                nullSafe(dashboardMapper.activeCount(yestStart, yestNow)),
                nullSafe(dashboardMapper.activeCount(weekAgoStart, weekAgoNow))));
        data.put("todayPayUsers", metric(
                nullSafe(dashboardMapper.countPayUsers(todayStart, now)),
                nullSafe(dashboardMapper.countPayUsers(yestStart, yestNow)),
                nullSafe(dashboardMapper.countPayUsers(weekAgoStart, weekAgoNow))));
        data.put("todayPayAmount", metricMoney(
                nullSafe(dashboardMapper.sumIncomeRange(todayStart, now)),
                nullSafe(dashboardMapper.sumIncomeRange(yestStart, yestNow)),
                nullSafe(dashboardMapper.sumIncomeRange(weekAgoStart, weekAgoNow))));

        // ---- 当月（本月 1 号至今累计）----
        data.put("monthNewUsers", nullSafe(dashboardMapper.countNewUsers(monthStart, now)));
        data.put("monthActive", nullSafe(dashboardMapper.activeCount(monthStart, now)));
        data.put("monthPayUsers", nullSafe(dashboardMapper.countPayUsers(monthStart, now)));
        data.put("monthPayAmount", nullSafe(dashboardMapper.sumIncomeRange(monthStart, now)));

        return data;
    }

    /** 数量型指标卡：当前值 + 日环比(vs 昨日) + 周同比(vs 上周同日) */
    private Map<String, Object> metric(long cur, long prevDay, long prevWeek) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("value", cur);
        m.put("dod", pct(cur, prevDay));
        m.put("wow", pct(cur, prevWeek));
        return m;
    }

    /** 金额型指标卡：当前值 + 日环比 + 周同比 */
    private Map<String, Object> metricMoney(BigDecimal cur, BigDecimal prevDay, BigDecimal prevWeek) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("value", cur);
        m.put("dod", pctBd(cur, prevDay));
        m.put("wow", pctBd(cur, prevWeek));
        return m;
    }

    /** 变化率百分比；cur 为空或 base<=0 返回 null */
    private BigDecimal pctBd(BigDecimal cur, BigDecimal base) {
        if (cur == null || base == null || base.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return cur.subtract(base).multiply(BigDecimal.valueOf(100))
                .divide(base, 2, RoundingMode.HALF_UP);
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

    /**
     * 新增用户次日留存（按注册日）。
     *
     * <p>留存率 = 当日注册用户中、次日有行为事件的去重用户数 / 当日注册数 ×100。
     * 仅统计「次日已完整过去」的注册日（即截至 today-2），保证每个点的次日数据完整。</p>
     *
     * @param days 取最近多少个注册日（默认 30）
     * @return points[{label,value}](value=留存率%，无注册的日期为 null) + latest/latestLabel/mean
     */
    public Map<String, Object> nextDayRetention(int days) {
        int n = days <= 0 ? 30 : days;
        LocalDate end = LocalDate.now().minusDays(2);     // 最后一个次日已完整的注册日
        LocalDate start = end.minusDays(n - 1L);
        return nextDayRetention(start, end);
    }

    /** 新增用户次日留存（指定注册日区间 [start,end]，按天）。 */
    public Map<String, Object> nextDayRetention(LocalDate start, LocalDate end) {
        return nextDayRetention(start, end, "day");
    }

    /**
     * 新增用户次日留存（指定注册日区间 [start,end]，dim=day/week/month）。
     *
     * <p>口径同 {@link #nextDayRetention(int)}；区间结束日会被收敛到「次日已完整」的最大注册日(today-2)，
     * 若收敛后起始日晚于结束日则返回空。周/月维度按加权留存率聚合：周期留存率 = 周期内 Σ次留 ÷ Σ新增 ×100。</p>
     */
    public Map<String, Object> nextDayRetention(LocalDate start, LocalDate end, String dim) {
        LocalDate maxCohort = LocalDate.now().minusDays(2);     // 次日已完整的最大注册日
        if (end == null || end.isAfter(maxCohort)) {
            end = maxCohort;
        }
        Map<String, Object> empty = new LinkedHashMap<>();
        if (start == null || start.isAfter(end)) {
            empty.put("points", new java.util.ArrayList<>());
            empty.put("latest", null);
            empty.put("latestLabel", "");
            empty.put("mean", null);
            return empty;
        }

        Map<String, long[]> m = new LinkedHashMap<>();    // dateStr -> [reg, retained]
        for (Map<String, Object> row : dashboardMapper.nextDayRetention(start.atStartOfDay(), end.plusDays(1).atStartOfDay())) {
            String d = String.valueOf(row.get("d")).substring(0, 10);
            long reg = ((Number) row.get("reg")).longValue();
            long retained = row.get("retained") == null ? 0L : ((Number) row.get("retained")).longValue();
            m.put(d, new long[]{reg, retained});
        }

        // 按 dim 聚合到周期：key -> [Σreg, Σretained]
        boolean byDay = !"week".equalsIgnoreCase(dim) && !"month".equalsIgnoreCase(dim);
        Map<String, long[]> agg = new LinkedHashMap<>();
        Map<String, String> keyLabel = new LinkedHashMap<>();
        Map<String, LocalDate> keyDate = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            long[] rr = m.get(d.toString());
            long reg = rr != null ? rr[0] : 0L;
            long ret = rr != null ? rr[1] : 0L;
            String key;
            String label;
            LocalDate kd;
            if ("week".equalsIgnoreCase(dim)) {
                kd = mondayOf(d);
                key = kd.toString();
                label = mmdd(kd);
            } else if ("month".equalsIgnoreCase(dim)) {
                kd = d.withDayOfMonth(1);
                key = kd.toString().substring(0, 7);
                label = key;
            } else {
                kd = d;
                key = d.toString();
                label = d.toString().substring(5).replace('-', '/');
            }
            long[] a = agg.computeIfAbsent(key, k -> new long[2]);
            a[0] += reg;
            a[1] += ret;
            keyLabel.putIfAbsent(key, label);
            keyDate.putIfAbsent(key, kd);
        }

        List<Map<String, Object>> points = new java.util.ArrayList<>();
        BigDecimal latest = null;
        String latestLabel = "";
        BigDecimal sum = BigDecimal.ZERO;
        int validCount = 0;
        for (Map.Entry<String, long[]> e : agg.entrySet()) {
            long[] a = e.getValue();
            BigDecimal rate = null;
            if (a[0] > 0) {
                rate = BigDecimal.valueOf(a[1] * 100.0 / a[0]).setScale(2, RoundingMode.HALF_UP);
                latest = rate;
                latestLabel = byDay ? fmtDateCn(keyDate.get(e.getKey())) : periodLabel(dim, keyDate.get(e.getKey()));
                sum = sum.add(rate);
                validCount++;
            }
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("label", keyLabel.get(e.getKey()));
            p.put("value", rate);
            points.add(p);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("points", points);
        data.put("latest", latest);
        data.put("latestLabel", latestLabel);
        data.put("mean", validCount == 0 ? null : sum.divide(BigDecimal.valueOf(validCount), 2, RoundingMode.HALF_UP));
        return data;
    }

    /** 会话间隔阈值：相邻事件间隔 ≤ 20 分钟视为同一次在线会话 */
    private static final long SESSION_GAP_MINUTES = 20;

    /**
     * 人均在线时长 / 人均启动次数（按天，实时）。
     *
     * <p>口径：以 custom_event 行为事件做会话化——从登录开始，相邻操作间隔 ≤20 分钟算同一次在线；
     * 会话时长=会话内首末事件之差，「启动次数」=会话数。每日人均 = 当日各用户合计 ÷ 当日活跃用户数。
     * 仅统计已完整结束的自然日（截至 today-1）。</p>
     *
     * @param days 最近多少个完整自然日（默认 30）
     */
    public Map<String, Object> onlineStats(int days) {
        return onlineStats(days, "day");
    }

    /**
     * 人均在线时长 / 人均启动次数（最近 N 天窗口，按 dim 聚合）。
     *
     * <p>未指定区间时按 dim 取默认窗口：day=最近 days 天、week=近 12 周、month=近 12 个月，
     * 与「活跃用户趋势」保持一致。</p>
     */
    public Map<String, Object> onlineStats(int days, String dim) {
        LocalDate end = LocalDate.now().minusDays(1);     // 昨日：最后一个完整自然日
        LocalDate start;
        if ("week".equalsIgnoreCase(dim)) {
            start = mondayOf(end).minusWeeks(11);
        } else if ("month".equalsIgnoreCase(dim)) {
            start = end.withDayOfMonth(1).minusMonths(11);
        } else {
            start = end.minusDays((days <= 0 ? 30 : days) - 1L);
        }
        return onlineStats(start, end, dim);
    }

    /** 人均在线时长 / 人均启动次数（指定自然日区间 [start,end]，按天聚合）。 */
    public Map<String, Object> onlineStats(LocalDate start, LocalDate end) {
        return onlineStats(start, end, "day");
    }

    /**
     * 人均在线时长 / 人均启动次数（指定区间 [start,end]，dim=day/week/month）。
     *
     * <p>始终先算每日人均值，再按 dim 聚合到周期（周期值=周期内各日人均值的平均）。
     * 仅 day 维度提供「日环比/周同比」（按天对比口径），week/month 维度该两项为 null。</p>
     */
    public Map<String, Object> onlineStats(LocalDate start, LocalDate end, String dim) {
        LocalDate maxDay = LocalDate.now().minusDays(1);
        if (end == null || end.isAfter(maxDay)) {
            end = maxDay;
        }
        if (start == null || start.isAfter(end)) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("duration", emptySeries());
            empty.put("launch", emptySeries());
            return empty;
        }
        // 向前多取 7 天，保证最新日的日环比/周同比有对比基数
        LocalDate computeStart = start.minusDays(7);
        List<Map<String, Object>> rows = dashboardMapper.eventStream(
                computeStart.atStartOfDay(), end.plusDays(1).atStartOfDay());

        // 按「日期|用户」分组（rows 已按 creator,create_time 升序，同一用户同一天的事件天然有序）
        Map<String, List<LocalDateTime>> grp = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            Object uid = r.get("uid");
            if (uid == null) {
                continue;
            }
            LocalDateTime t = toLdt(r.get("t"));
            grp.computeIfAbsent(t.toLocalDate() + "|" + uid, k -> new ArrayList<>()).add(t);
        }

        // 每日聚合：[在线分钟合计, 启动次数合计, 活跃用户数]
        Map<LocalDate, double[]> agg = new LinkedHashMap<>();
        for (Map.Entry<String, List<LocalDateTime>> e : grp.entrySet()) {
            LocalDate day = LocalDate.parse(e.getKey().substring(0, 10));
            List<LocalDateTime> ts = e.getValue();
            double online = 0d;
            int sessions = 1;
            LocalDateTime sStart = ts.get(0);
            LocalDateTime last = ts.get(0);
            for (int i = 1; i < ts.size(); i++) {
                LocalDateTime cur = ts.get(i);
                if (Duration.between(last, cur).toMinutes() <= SESSION_GAP_MINUTES) {
                    last = cur;
                } else {
                    online += Duration.between(sStart, last).toMillis() / 60000.0;
                    sStart = cur;
                    last = cur;
                    sessions++;
                }
            }
            online += Duration.between(sStart, last).toMillis() / 60000.0;
            double[] a = agg.computeIfAbsent(day, k -> new double[3]);
            a[0] += online;
            a[1] += sessions;
            a[2] += 1;
        }

        // 每日人均值
        Map<LocalDate, BigDecimal> durMap = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> lauMap = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, double[]> e : agg.entrySet()) {
            double[] a = e.getValue();
            if (a[2] > 0) {
                durMap.put(e.getKey(), BigDecimal.valueOf(a[0] / a[2]).setScale(2, RoundingMode.HALF_UP));
                lauMap.put(e.getKey(), BigDecimal.valueOf(a[1] / a[2]).setScale(2, RoundingMode.HALF_UP));
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("duration", buildSeries(durMap, start, end, dim));
        data.put("launch", buildSeries(lauMap, start, end, dim));
        return data;
    }

    /**
     * 实时在线人数（按 bucket 分钟分桶，今日 + 昨日 VS）。
     *
     * <p>口径：某时刻 T 的在线人数 = 在 (T-20分钟, T] 内有行为事件的去重用户数（即「最近一次操作 20 分钟内」算在线）。
     * today 序列截至当前时刻，prev(昨日) 为全天，用于对比。</p>
     *
     * @param day          统计日（默认今日）
     * @param bucketMinutes 分桶粒度：1/5/10 分钟（其它值按 5 处理）
     */
    public Map<String, Object> realtimeOnline(LocalDate day, int bucketMinutes) {
        if (day == null) {
            day = LocalDate.now();
        }
        int bucket = (bucketMinutes == 1 || bucketMinutes == 10 || bucketMinutes == 60) ? bucketMinutes : 5;
        int slots = 1440 / bucket;
        boolean isToday = day.equals(LocalDate.now());
        int todayLast = isToday
                ? Math.min(slots - 1, java.time.LocalTime.now().toSecondOfDay() / 60 / bucket)
                : slots - 1;

        long[] todayCounts = dayOnlineCounts(day, bucket, slots);
        long[] prevCounts = dayOnlineCounts(day.minusDays(1), bucket, slots);

        List<String> labels = new ArrayList<>();
        List<Long> today = new ArrayList<>();
        List<Long> prev = new ArrayList<>();
        long sum = 0;
        int cnt = 0;
        for (int k = 0; k < slots; k++) {
            labels.add(slotLabel(k, bucket));
            prev.add(prevCounts[k]);
            if (k <= todayLast) {
                today.add(todayCounts[k]);
                sum += todayCounts[k];
                cnt++;
            } else {
                today.add(null);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("bucket", bucket);
        data.put("labels", labels);
        data.put("today", today);
        data.put("prev", prev);
        data.put("latest", todayLast >= 0 ? todayCounts[todayLast] : 0L);
        data.put("latestLabel", day + " " + slotLabel(todayLast, bucket));
        data.put("mean", cnt == 0 ? null : BigDecimal.valueOf((double) sum / cnt).setScale(2, RoundingMode.HALF_UP));
        data.put("sum", sum);
        return data;
    }

    /**
     * 实时在线人数（区间连续序列，保持 bucket 粒度，与前一等长周期 VS）。
     *
     * <p>实线 = [start,end] 闭区间内每个 bucket 时间点的在线人数（连续多天，不做日聚合）；
     * 虚线 = 往前平移一个等长周期 [start-N, end-N] 的同位置点，用于同比对比。
     * 若区间含今日，今日当前时刻之后的点置 null。</p>
     *
     * @param start         区间起始日
     * @param end           区间结束日
     * @param bucketMinutes 分桶粒度：1/5/10/60 分钟（其它值按 5 处理）
     */
    public Map<String, Object> realtimeOnlineRange(LocalDate start, LocalDate end, int bucketMinutes) {
        int bucket = (bucketMinutes == 1 || bucketMinutes == 10 || bucketMinutes == 60) ? bucketMinutes : 5;
        int slotsPerDay = 1440 / bucket;
        long days = end.toEpochDay() - start.toEpochDay() + 1;
        // 防御：区间过大时收敛到最近 92 天，避免点数与计算量爆炸
        if (days > 92) {
            start = end.minusDays(91);
            days = 92;
        }
        int slots = (int) (days * slotsPerDay);

        long[] curCounts = rangeOnlineCounts(start, end, bucket, slots);
        LocalDate prevStart = start.minusDays(days);
        LocalDate prevEnd = end.minusDays(days);
        long[] prevCounts = rangeOnlineCounts(prevStart, prevEnd, bucket, slots);

        LocalDateTime rangeStart = start.atStartOfDay();
        LocalDateTime prevRangeStart = prevStart.atStartOfDay();
        // 截断未来：区间含今日时，超过当前时刻的点不展示
        int curLast = slots - 1;
        if (!end.isBefore(LocalDate.now())) {
            long nowMin = Duration.between(rangeStart, LocalDateTime.now()).toMinutes();
            curLast = (int) Math.max(0, Math.min(slots - 1, nowMin / bucket));
        }

        List<String> labels = new ArrayList<>();
        List<String> prevLabels = new ArrayList<>();
        List<Long> today = new ArrayList<>();
        List<Long> prev = new ArrayList<>();
        long sum = 0;
        int cnt = 0;
        long latestVal = 0;
        String latestLabel = "";
        for (int k = 0; k < slots; k++) {
            labels.add(slotLabelAt(rangeStart, k, bucket));
            prevLabels.add(slotLabelAt(prevRangeStart, k, bucket));
            prev.add(prevCounts[k]);
            if (k <= curLast) {
                today.add(curCounts[k]);
                sum += curCounts[k];
                cnt++;
                latestVal = curCounts[k];
                latestLabel = slotLabelAt(rangeStart, k, bucket);
            } else {
                today.add(null);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("bucket", bucket);
        data.put("range", true);
        data.put("labels", labels);
        data.put("prevLabels", prevLabels);
        data.put("today", today);
        data.put("prev", prev);
        data.put("latest", latestVal);
        data.put("latestLabel", latestLabel);
        data.put("mean", cnt == 0 ? null : BigDecimal.valueOf((double) sum / cnt).setScale(2, RoundingMode.HALF_UP));
        data.put("sum", sum);
        return data;
    }

    /** 计算 [start,end] 闭区间内，每个 bucket 分钟时间点上的在线人数（连续多天，dayOnlineCounts 的区间推广） */
    private long[] rangeOnlineCounts(LocalDate start, LocalDate end, int bucket, int slots) {
        LocalDateTime rangeStart = start.atStartOfDay();
        // 向前多取 20 分钟：跨区间起点零点的活跃用户在起点附近仍算在线
        List<Map<String, Object>> rows = dashboardMapper.eventStream(
                rangeStart.minusMinutes(SESSION_GAP_MINUTES), end.plusDays(1).atStartOfDay());

        Map<String, List<LocalDateTime>> grp = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            Object uid = r.get("uid");
            if (uid == null) {
                continue;
            }
            grp.computeIfAbsent(String.valueOf(uid), k -> new ArrayList<>()).add(toLdt(r.get("t")));
        }

        long[] counts = new long[slots];
        java.util.BitSet present = new java.util.BitSet(slots);
        for (List<LocalDateTime> ts : grp.values()) {
            present.clear();
            for (LocalDateTime t : ts) {
                long eMin = Duration.between(rangeStart, t).toMinutes();   // 距区间起点分钟数，可为负
                int kStart = (int) Math.ceil((double) eMin / bucket);
                if (kStart < 0) {
                    kStart = 0;
                }
                // 在线区间 [e, e+20min)：覆盖的时间点
                for (int k = kStart; k < slots && (long) k * bucket < eMin + SESSION_GAP_MINUTES; k++) {
                    present.set(k);
                }
            }
            for (int k = present.nextSetBit(0); k >= 0; k = present.nextSetBit(k + 1)) {
                counts[k]++;
            }
        }
        return counts;
    }

    /** 绝对时刻标签：base + k*bucket 分钟 → "yyyy-MM-dd HH:mm" */
    private String slotLabelAt(LocalDateTime base, int k, int bucket) {
        LocalDateTime t = base.plusMinutes((long) Math.max(0, k) * bucket);
        return String.format("%04d-%02d-%02d %02d:%02d",
                t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour(), t.getMinute());
    }

    /** 计算某天各 bucket 分钟时间点上的在线人数 */
    private long[] dayOnlineCounts(LocalDate day, int bucket, int slots) {
        LocalDateTime dayStart = day.atStartOfDay();
        // 向前多取 20 分钟：跨零点的活跃用户在 00:00 附近仍算在线
        List<Map<String, Object>> rows = dashboardMapper.eventStream(
                dayStart.minusMinutes(SESSION_GAP_MINUTES), day.plusDays(1).atStartOfDay());

        Map<String, List<LocalDateTime>> grp = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            Object uid = r.get("uid");
            if (uid == null) {
                continue;
            }
            grp.computeIfAbsent(String.valueOf(uid), k -> new ArrayList<>()).add(toLdt(r.get("t")));
        }

        long[] counts = new long[slots];
        java.util.BitSet present = new java.util.BitSet(slots);
        for (List<LocalDateTime> ts : grp.values()) {
            present.clear();
            for (LocalDateTime t : ts) {
                long eMin = Duration.between(dayStart, t).toMinutes();   // 距零点分钟数，可为负
                int kStart = (int) Math.ceil((double) eMin / bucket);
                if (kStart < 0) {
                    kStart = 0;
                }
                // 在线区间 [e, e+20min)：覆盖的时间点
                for (int k = kStart; k < slots && (long) k * bucket < eMin + SESSION_GAP_MINUTES; k++) {
                    present.set(k);
                }
            }
            for (int k = present.nextSetBit(0); k >= 0; k = present.nextSetBit(k + 1)) {
                counts[k]++;
            }
        }
        return counts;
    }

    /** 槽位序号 → HH:mm */
    private String slotLabel(int k, int bucket) {
        int m = Math.max(0, k) * bucket;
        return String.format("%02d:%02d", m / 60, m % 60);
    }

    /**
     * 活跃用户趋势（按 天/周/月 去重活跃用户数：DAU/WAU/MAU）。
     *
     * <p>口径：周期内有任意行为事件的去重用户数。按天=当日 DAU、按周=当周 WAU、按月=当月 MAU，均为整数。
     * 截至昨日（today-1）的完整周期。day=近30天、week=近12周、month=近12个月。</p>
     *
     * @param dim day | week | month
     */
    public Map<String, Object> onlineTrend(String dim) {
        return onlineTrend(dim, null, null);
    }

    /**
     * 在线人数趋势（指定区间 [start,end]）。
     *
     * <p>结束日收敛到昨日（today-1，完整周期）；未传 start 时按 dim 用默认窗口
     * （day=近30天、week=近12周、month=近12个月）。</p>
     */
    public Map<String, Object> onlineTrend(String dim, LocalDate start, LocalDate end) {
        LocalDate maxDay = LocalDate.now().minusDays(1);
        if (end == null || end.isAfter(maxDay)) {
            end = maxDay;
        }
        if (start == null) {
            if ("week".equalsIgnoreCase(dim)) {
                start = mondayOf(end).minusWeeks(11);
            } else if ("month".equalsIgnoreCase(dim)) {
                start = end.withDayOfMonth(1).minusMonths(11);
            } else {
                start = end.minusDays(29);
            }
        }
        if (start.isAfter(end)) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("labels", new ArrayList<>());
            empty.put("values", new ArrayList<>());
            empty.put("latest", null);
            empty.put("latestLabel", "");
            empty.put("mean", null);
            empty.put("sum", null);
            return empty;
        }

        Map<LocalDate, Set<String>> dayUsers = computeDailyActiveUsers(start, end);

        // 聚合到周期：key -> 周期内去重活跃用户集合（按天=DAU、按周=WAU、按月=MAU）
        Map<String, Set<String>> agg = new LinkedHashMap<>();
        Map<String, String> keyLabel = new LinkedHashMap<>();
        Map<String, LocalDate> keyDate = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            String key;
            String label;
            LocalDate kd;
            if ("week".equalsIgnoreCase(dim)) {
                kd = mondayOf(d);
                key = kd.toString();
                label = mmdd(kd);
            } else if ("month".equalsIgnoreCase(dim)) {
                kd = d.withDayOfMonth(1);
                key = kd.toString().substring(0, 7);
                label = key;
            } else {
                kd = d;
                key = d.toString();
                label = mmdd(d);
            }
            agg.computeIfAbsent(key, k -> new HashSet<>()).addAll(dayUsers.getOrDefault(d, Collections.emptySet()));
            keyLabel.putIfAbsent(key, label);
            keyDate.putIfAbsent(key, kd);
        }

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        long sum = 0;
        Long latest = null;
        String latestLabel = "";
        for (Map.Entry<String, Set<String>> e : agg.entrySet()) {
            long v = e.getValue().size();
            labels.add(keyLabel.get(e.getKey()));
            values.add(v);
            sum += v;
            latest = v;
            latestLabel = periodLabel(dim, keyDate.get(e.getKey()));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("labels", labels);
        data.put("values", values);
        data.put("latest", latest);
        data.put("latestLabel", latestLabel);
        data.put("mean", labels.isEmpty() ? null : BigDecimal.valueOf((double) sum / labels.size()).setScale(2, RoundingMode.HALF_UP));
        data.put("sum", sum);
        return data;
    }

    /** 计算区间内每天的「活跃用户集合」（当天有任意行为事件的去重用户，用于 DAU/WAU/MAU） */
    private Map<LocalDate, Set<String>> computeDailyActiveUsers(LocalDate start, LocalDate end) {
        List<Map<String, Object>> rows = dashboardMapper.eventStream(
                start.atStartOfDay(), end.plusDays(1).atStartOfDay());

        Map<LocalDate, Set<String>> dayUsers = new HashMap<>();
        for (Map<String, Object> r : rows) {
            Object uid = r.get("uid");
            if (uid == null) {
                continue;
            }
            LocalDate d = toLdt(r.get("t")).toLocalDate();
            if (d.isBefore(start) || d.isAfter(end)) {
                continue;
            }
            dayUsers.computeIfAbsent(d, k -> new HashSet<>()).add(String.valueOf(uid));
        }
        return dayUsers;
    }

    private LocalDate mondayOf(LocalDate d) {
        return d.minusDays(d.getDayOfWeek().getValue() - 1L);
    }

    private String mmdd(LocalDate d) {
        return String.format("%02d-%02d", d.getMonthValue(), d.getDayOfMonth());
    }

    private String periodLabel(String dim, LocalDate d) {
        if ("week".equalsIgnoreCase(dim)) {
            return d + " 当周";
        }
        if ("month".equalsIgnoreCase(dim)) {
            return d.toString().substring(0, 7);
        }
        return fmtDateCn(d);
    }

    /** 把每日值整理为前端折线序列 + 汇总（latest/dod/wow/mean/sum） */
    private Map<String, Object> buildDailySeries(Map<LocalDate, BigDecimal> map, LocalDate start, LocalDate end) {
        List<Map<String, Object>> points = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        int cnt = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            BigDecimal v = map.get(d);
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("label", d.toString().substring(5).replace('-', '/'));
            p.put("value", v);
            points.add(p);
            if (v != null) {
                sum = sum.add(v);
                cnt++;
            }
        }
        BigDecimal latest = map.get(end);
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("points", points);
        s.put("latest", latest);
        s.put("latestLabel", fmtDateCn(end));
        s.put("dod", pctBd(latest, map.get(end.minusDays(1))));
        s.put("wow", pctBd(latest, map.get(end.minusDays(7))));
        s.put("mean", cnt == 0 ? null : sum.divide(BigDecimal.valueOf(cnt), 2, RoundingMode.HALF_UP));
        s.put("sum", sum.setScale(2, RoundingMode.HALF_UP));
        return s;
    }

    /** 把每日值按 dim 聚合为前端折线序列 + 汇总；day 维度额外提供 dod/wow，week/month 该两项为 null */
    private Map<String, Object> buildSeries(Map<LocalDate, BigDecimal> map, LocalDate start, LocalDate end, String dim) {
        if ("day".equalsIgnoreCase(dim)) {
            return buildDailySeries(map, start, end);
        }

        Map<String, BigDecimal[]> agg = new LinkedHashMap<>();   // key -> [sum, count]
        Map<String, String> keyLabel = new LinkedHashMap<>();
        Map<String, LocalDate> keyDate = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            BigDecimal v = map.get(d);
            if (v == null) {
                continue;
            }
            String key;
            String label;
            LocalDate kd;
            if ("week".equalsIgnoreCase(dim)) {
                kd = mondayOf(d);
                key = kd.toString();
                label = mmdd(kd);
            } else {
                kd = d.withDayOfMonth(1);
                key = kd.toString().substring(0, 7);
                label = key;
            }
            BigDecimal[] a = agg.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            a[0] = a[0].add(v);
            a[1] = a[1].add(BigDecimal.ONE);
            keyLabel.putIfAbsent(key, label);
            keyDate.putIfAbsent(key, kd);
        }

        List<Map<String, Object>> points = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal latest = null;
        String latestLabel = "";
        int cnt = 0;
        for (Map.Entry<String, BigDecimal[]> e : agg.entrySet()) {
            BigDecimal[] a = e.getValue();
            BigDecimal v = a[1].compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : a[0].divide(a[1], 2, RoundingMode.HALF_UP);
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("label", keyLabel.get(e.getKey()));
            p.put("value", v);
            points.add(p);
            sum = sum.add(v);
            latest = v;
            latestLabel = periodLabel(dim, keyDate.get(e.getKey()));
            cnt++;
        }

        Map<String, Object> s = new LinkedHashMap<>();
        s.put("points", points);
        s.put("latest", latest);
        s.put("latestLabel", latestLabel);
        s.put("dod", null);
        s.put("wow", null);
        s.put("mean", cnt == 0 ? null : sum.divide(BigDecimal.valueOf(cnt), 2, RoundingMode.HALF_UP));
        s.put("sum", sum.setScale(2, RoundingMode.HALF_UP));
        return s;
    }

    private Map<String, Object> emptySeries() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("points", new ArrayList<>());
        s.put("latest", null);
        s.put("latestLabel", "");
        s.put("dod", null);
        s.put("wow", null);
        s.put("mean", null);
        s.put("sum", null);
        return s;
    }

    private LocalDateTime toLdt(Object o) {
        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }
        if (o instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) o).toLocalDateTime();
        }
        return LocalDateTime.parse(String.valueOf(o).replace(' ', 'T'));
    }

    private static final String[] WEEK_CN = {"日", "一", "二", "三", "四", "五", "六"};

    /** 日期格式化为 yyyy-MM-dd(周X) */
    private String fmtDateCn(LocalDate d) {
        return d + "(" + WEEK_CN[d.getDayOfWeek().getValue() % 7] + ")";
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
