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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    /**
     * 新增用户次日留存（指定注册日区间 [start,end]）。
     *
     * <p>口径同 {@link #nextDayRetention(int)}；区间结束日会被收敛到「次日已完整」的最大注册日(today-2)，
     * 若收敛后起始日晚于结束日则返回空。</p>
     */
    public Map<String, Object> nextDayRetention(LocalDate start, LocalDate end) {
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

        List<Map<String, Object>> points = new java.util.ArrayList<>();
        BigDecimal latest = null;
        String latestLabel = "";
        BigDecimal sum = BigDecimal.ZERO;
        int validCount = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            long[] rr = m.get(d.toString());
            BigDecimal rate = null;
            if (rr != null && rr[0] > 0) {
                rate = BigDecimal.valueOf(rr[1] * 100.0 / rr[0]).setScale(2, RoundingMode.HALF_UP);
                latest = rate;
                latestLabel = fmtDateCn(d);
                sum = sum.add(rate);
                validCount++;
            }
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("label", d.toString().substring(5).replace('-', '/'));
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
        LocalDate end = LocalDate.now().minusDays(1);     // 昨日：最后一个完整自然日
        LocalDate start = end.minusDays((days <= 0 ? 30 : days) - 1L);
        return onlineStats(start, end);
    }

    /** 人均在线时长 / 人均启动次数（指定自然日区间 [start,end]）。 */
    public Map<String, Object> onlineStats(LocalDate start, LocalDate end) {
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
        data.put("duration", buildDailySeries(durMap, start, end));
        data.put("launch", buildDailySeries(lauMap, start, end));
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

    /** 一天 5 分钟槽位数 */
    private static final double SLOTS_OF_DAY = 1440.0 / 5;

    /**
     * 在线人数趋势（日均在线人数，按 天/周/月 聚合）。
     *
     * <p>日均在线人数 = 当日各 5 分钟时间点在线人数的平均值；周/月取其所含各日日均的平均。
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

        Map<LocalDate, Long> daySum = computeDailyOnline(start, end);

        // 聚合到周期：key -> [日均之和, 天数]
        Map<String, double[]> agg = new LinkedHashMap<>();
        Map<String, String> keyLabel = new LinkedHashMap<>();
        Map<String, LocalDate> keyDate = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            double dayAvg = daySum.getOrDefault(d, 0L) / SLOTS_OF_DAY;
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
            double[] a = agg.computeIfAbsent(key, k -> new double[2]);
            a[0] += dayAvg;
            a[1] += 1;
            keyLabel.putIfAbsent(key, label);
            keyDate.putIfAbsent(key, kd);
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal latest = null;
        String latestLabel = "";
        for (Map.Entry<String, double[]> e : agg.entrySet()) {
            double[] a = e.getValue();
            BigDecimal v = BigDecimal.valueOf(a[1] == 0 ? 0 : a[0] / a[1]).setScale(2, RoundingMode.HALF_UP);
            labels.add(keyLabel.get(e.getKey()));
            values.add(v);
            sum = sum.add(v);
            latest = v;
            latestLabel = periodLabel(dim, keyDate.get(e.getKey()));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("labels", labels);
        data.put("values", values);
        data.put("latest", latest);
        data.put("latestLabel", latestLabel);
        data.put("mean", labels.isEmpty() ? null : sum.divide(BigDecimal.valueOf(labels.size()), 2, RoundingMode.HALF_UP));
        data.put("sum", sum.setScale(2, RoundingMode.HALF_UP));
        return data;
    }

    /** 计算区间内每天的「在线人数总和」（= 当日各 5 分钟点在线人数之和，用于求日均） */
    private Map<LocalDate, Long> computeDailyOnline(LocalDate start, LocalDate end) {
        List<Map<String, Object>> rows = dashboardMapper.eventStream(
                start.atStartOfDay().minusMinutes(SESSION_GAP_MINUTES), end.plusDays(1).atStartOfDay());

        Map<String, List<LocalDateTime>> grp = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            Object uid = r.get("uid");
            if (uid == null) {
                continue;
            }
            grp.computeIfAbsent(String.valueOf(uid), k -> new ArrayList<>()).add(toLdt(r.get("t")));
        }

        Map<LocalDate, Long> daySum = new HashMap<>();
        for (List<LocalDateTime> ts : grp.values()) {
            // 该用户在线覆盖的「绝对 5 分钟刻度」(分钟数)，去重
            HashSet<Long> marks = new HashSet<>();
            for (LocalDateTime t : ts) {
                long base = t.toEpochSecond(ZoneOffset.UTC) / 60;
                long first = (base % 5 == 0) ? base : (base / 5 + 1) * 5;
                for (long mk = first; mk < base + SESSION_GAP_MINUTES; mk += 5) {
                    marks.add(mk);
                }
            }
            for (long mk : marks) {
                LocalDate day = LocalDateTime.ofEpochSecond(mk * 60, 0, ZoneOffset.UTC).toLocalDate();
                if (!day.isBefore(start) && !day.isAfter(end)) {
                    daySum.merge(day, 1L, Long::sum);
                }
            }
        }
        return daySum;
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
