package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据驾驶舱聚合查询（zhouyi，只读）。
 *
 * <p>说明：表/列名与状态枚举值（如 payments.payment_status='SUCCESS'）依据
 * 《运营后台系统策划案》，以真实 zhouyi 表为准；联调时如不符仅需调整本类 SQL。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface DashboardMapper {

    /** 成功支付累计收入 */
    @Select("SELECT IFNULL(SUM(payment_amount), 0) FROM payments WHERE payment_status = 'SUCCESS'")
    BigDecimal sumIncomeTotal();

    /** 指定时间起的成功支付收入 */
    @Select("SELECT IFNULL(SUM(payment_amount), 0) FROM payments WHERE payment_status = 'SUCCESS' AND payment_time >= #{start}")
    BigDecimal sumIncomeSince(@Param("start") LocalDateTime start);

    /** 付费用户数（去重） */
    @Select("SELECT COUNT(DISTINCT user_id) FROM payments WHERE payment_status = 'SUCCESS'")
    Long countPaidUsers();

    // ===================== 玩家概览卡片（区间统计：新增 / 付费玩家 / 付费金额） =====================

    /** 指定区间 [start,end) 新增用户数 */
    @Select("SELECT COUNT(*) FROM user WHERE createTime >= #{start} AND createTime < #{end}")
    Long countNewUsers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 指定区间 [start,end) 「充值成功」触发用户数 = 成功支付的去重 user_id（即付费人数） */
    @Select("SELECT COUNT(DISTINCT user_id) FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND payment_time >= #{start} AND payment_time < #{end}")
    Long countPayUsers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 指定区间 [start,end) 成功支付金额合计 */
    @Select("SELECT IFNULL(SUM(payment_amount), 0) FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND payment_time >= #{start} AND payment_time < #{end}")
    BigDecimal sumIncomeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 近段时间新增用户趋势（按天） */
    @Select("SELECT DATE(createTime) AS `date`, COUNT(*) AS `count` FROM user WHERE createTime >= #{start} GROUP BY DATE(createTime) ORDER BY `date`")
    List<Map<String, Object>> userTrend(@Param("start") LocalDateTime start);

    /** 近段时间收入趋势（按天） */
    @Select("SELECT DATE(payment_time) AS `date`, IFNULL(SUM(payment_amount), 0) AS `amount` FROM payments WHERE payment_status = 'SUCCESS' AND payment_time >= #{start} GROUP BY DATE(payment_time) ORDER BY `date`")
    List<Map<String, Object>> incomeTrend(@Param("start") LocalDateTime start);

    /** 近段时间订单量趋势（按天） */
    @Select("SELECT DATE(create_time) AS `date`, COUNT(*) AS `count` FROM orders WHERE create_time >= #{start} GROUP BY DATE(create_time) ORDER BY `date`")
    List<Map<String, Object>> orderTrend(@Param("start") LocalDateTime start);

    /** 热门商品排行（订单数 Top10） */
    @Select("SELECT item_id AS itemId, item_name AS itemName, COUNT(*) AS cnt FROM orders GROUP BY item_id, item_name ORDER BY cnt DESC LIMIT 10")
    List<Map<String, Object>> goodsRank();

    /** 支付渠道分布 */
    @Select("SELECT pay_source AS channel, COUNT(*) AS cnt FROM orders GROUP BY pay_source")
    List<Map<String, Object>> channelDist();

    /** 有效权益类型分布 */
    @Select("SELECT item_type AS type, COUNT(*) AS cnt FROM user_benefits WHERE is_valid = 1 GROUP BY item_type")
    List<Map<String, Object>> benefitTypeDist();

    // ===================== 活跃数据（用户行为活跃：custom_event.create_time 去重 creator(=用户ID)） =====================
    // 说明：custom_event 为用户行为事件流水（user_pai_pan 排盘 / user_login 登录等），
    //       creator 即用户ID，create_time 为事件发生时间，deleted_flag='N' 为有效记录。

    /** 指定区间内按小时去重活跃用户数（k=小时0-23, v=活跃数） */
    @Select("SELECT HOUR(create_time) AS k, COUNT(DISTINCT creator) AS v FROM custom_event "
            + "WHERE deleted_flag = 'N' AND create_time >= #{start} AND create_time < #{end} "
            + "GROUP BY HOUR(create_time) ORDER BY k")
    List<Map<String, Object>> activeByHour(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 指定区间内按天去重活跃用户数（k=日期, v=活跃数） */
    @Select("SELECT DATE(create_time) AS k, COUNT(DISTINCT creator) AS v FROM custom_event "
            + "WHERE deleted_flag = 'N' AND create_time >= #{start} AND create_time < #{end} "
            + "GROUP BY DATE(create_time) ORDER BY k")
    List<Map<String, Object>> activeByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 指定区间内去重活跃用户总数 */
    @Select("SELECT COUNT(DISTINCT creator) FROM custom_event "
            + "WHERE deleted_flag = 'N' AND create_time >= #{start} AND create_time < #{end}")
    Long activeCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 指定区间 [start,end) 内触发「账号登录」(user_login) 事件的去重用户数（ARPU 分母） */
    @Select("SELECT COUNT(DISTINCT creator) FROM custom_event "
            + "WHERE deleted_flag = 'N' AND event_name = 'user_login' "
            + "AND create_time >= #{start} AND create_time < #{end}")
    Long loginUserCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内活跃用户及其注册时间（uid=用户ID, reg=注册时间）。
     *
     * <p>用于「活跃用户生命周期天数构成」：活跃=custom_event 去重 creator，经 CAST 关联 user 取注册时间。</p>
     */
    @Select("SELECT DISTINCT e.creator AS uid, u.createTime AS reg "
            + "FROM custom_event e JOIN `user` u ON CAST(e.creator AS UNSIGNED) = u.id "
            + "WHERE e.deleted_flag = 'N' AND e.creator IS NOT NULL "
            + "AND e.create_time >= #{start} AND e.create_time < #{end}")
    List<Map<String, Object>> activeUserReg(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // ===================== 新增用户次日留存 =====================
    // 口径：按注册日 D 分组，reg=当日注册用户数；retained=其中在次日(D+1)有行为事件(custom_event)的去重用户数。
    //       creator(varchar) 存的是用户ID，与 user.id 关联。

    /** 区间 [start,end) 内的行为事件流水（uid=用户ID, t=事件时间），按用户、时间升序，用于会话化统计在线时长/启动次数 */
    @Select("SELECT creator AS uid, create_time AS t FROM custom_event "
            + "WHERE deleted_flag = 'N' AND creator IS NOT NULL "
            + "AND create_time >= #{start} AND create_time < #{end} "
            + "ORDER BY creator, create_time")
    List<Map<String, Object>> eventStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内「账号登录」(user_login) 事件流水（uid=用户ID, t=事件时间），用于按周期去重统计「账号登录」触发用户数 */
    @Select("SELECT creator AS uid, create_time AS t FROM custom_event "
            + "WHERE deleted_flag = 'N' AND event_name = 'user_login' AND creator IS NOT NULL "
            + "AND create_time >= #{start} AND create_time < #{end} "
            + "ORDER BY creator, create_time")
    List<Map<String, Object>> loginEventStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内按注册日统计：d=注册日, reg=注册数, retained=次日留存数 */
    @Select("SELECT DATE(u.createTime) AS d, "
            + "COUNT(DISTINCT u.id) AS reg, "
            + "COUNT(DISTINCT e.creator) AS retained "
            + "FROM user u "
            + "LEFT JOIN custom_event e "
            + "  ON e.creator = u.id AND e.deleted_flag = 'N' "
            + "  AND e.create_time >= DATE_ADD(DATE(u.createTime), INTERVAL 1 DAY) "
            + "  AND e.create_time <  DATE_ADD(DATE(u.createTime), INTERVAL 2 DAY) "
            + "WHERE u.createTime >= #{start} AND u.createTime < #{end} "
            + "GROUP BY DATE(u.createTime) ORDER BY d")
    List<Map<String, Object>> nextDayRetention(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内新增用户的注册时间流水（t=createTime），用于按 bucket 分桶统计实时新增 */
    @Select("SELECT createTime AS t FROM user WHERE createTime >= #{start} AND createTime < #{end} ORDER BY createTime")
    List<Map<String, Object>> newUserStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内按天新增用户数：d=注册日, c=新增数，用于趋势(天/周/月)聚合 */
    @Select("SELECT DATE(createTime) AS d, COUNT(*) AS c FROM user "
            + "WHERE createTime >= #{start} AND createTime < #{end} GROUP BY DATE(createTime) ORDER BY d")
    List<Map<String, Object>> newUserDailyCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内成功支付的流水（t=支付时间, amt=支付金额），用于按 bucket 分桶统计实时付费 */
    @Select("SELECT payment_time AS t, payment_amount AS amt FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND payment_time >= #{start} AND payment_time < #{end} ORDER BY payment_time")
    List<Map<String, Object>> paymentStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内按天成功支付金额：d=支付日, amt=金额合计，用于趋势(天/周/月)聚合 */
    @Select("SELECT DATE(payment_time) AS d, IFNULL(SUM(payment_amount), 0) AS amt FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND payment_time >= #{start} AND payment_time < #{end} "
            + "GROUP BY DATE(payment_time) ORDER BY d")
    List<Map<String, Object>> paymentDailySum(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内成功支付的去重用户流水（uid=付费用户ID, t=支付时间）。
     *
     * <p>用于按周期(天/周/月)对付费用户去重取并集，得到「付费人数=充值成功触发用户数」，
     * 作为付费率/ARPPU 的分母。</p>
     */
    @Select("SELECT user_id AS uid, payment_time AS t FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND user_id IS NOT NULL "
            + "AND payment_time >= #{start} AND payment_time < #{end} "
            + "ORDER BY user_id, payment_time")
    List<Map<String, Object>> paymentUserStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内成功支付的用户流水 + 其注册时间（uid=付费用户ID, pt=支付时间, rt=注册时间）。
     *
     * <p>用于「付费人数新老用户分层」：按周期对付费用户去重后，依据注册时间(rt)是否落在该周期内分为新/老用户。</p>
     */
    @Select("SELECT p.user_id AS uid, p.payment_time AS pt, u.createTime AS rt "
            + "FROM payments p JOIN `user` u ON u.id = p.user_id "
            + "WHERE p.payment_status = 'SUCCESS' AND p.payment_time >= #{start} AND p.payment_time < #{end} "
            + "ORDER BY p.payment_time")
    List<Map<String, Object>> paymentUserWithReg(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内按天统计充值订单数：d=下单日, total=发起充值总数, success=成功(PAID)数。
     *
     * <p>数据源为 zhouyi 的 orders 表，按下单时间(create_time)分桶：发起充值=全部订单，成功=order_status='PAID'。
     * 用于「充值成功率/失败率」：成功率=success/total，失败率=(total-success)/total（只要不是 PAID 都算失败，含支付中、已取消）。</p>
     */
    @Select("SELECT DATE(create_time) AS d, COUNT(*) AS total, "
            + "SUM(CASE WHEN order_status = 'PAID' THEN 1 ELSE 0 END) AS success "
            + "FROM orders WHERE create_time >= #{start} AND create_time < #{end} "
            + "GROUP BY DATE(create_time) ORDER BY d")
    List<Map<String, Object>> paymentStatusDailyCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内按注册日统计：d=注册日, reg=注册用户数, pay=注册当天即成功付费的去重用户数。
     *
     * <p>用于「注册首日付费转化率」= pay ÷ reg ×100。</p>
     */
    @Select("SELECT DATE(u.createTime) AS d, COUNT(DISTINCT u.id) AS reg, "
            + "COUNT(DISTINCT CASE WHEN p.user_id IS NOT NULL THEN u.id END) AS pay "
            + "FROM `user` u "
            + "LEFT JOIN payments p ON p.user_id = u.id AND p.payment_status = 'SUCCESS' "
            + "  AND DATE(p.payment_time) = DATE(u.createTime) "
            + "WHERE u.createTime >= #{start} AND u.createTime < #{end} "
            + "GROUP BY DATE(u.createTime) ORDER BY d")
    List<Map<String, Object>> firstDayPayDaily(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内注册用户（uid, d=注册日），用于「注册后阶段累计付费」同期群 */
    @Select("SELECT id AS uid, DATE(createTime) AS d FROM `user` "
            + "WHERE createTime >= #{start} AND createTime < #{end}")
    List<Map<String, Object>> regUserDays(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** 区间 [start,end) 内成功支付的（uid, d=支付日）去重，用于「注册后阶段累计付费」同期群 */
    @Select("SELECT DISTINCT user_id AS uid, DATE(payment_time) AS d FROM payments "
            + "WHERE payment_status = 'SUCCESS' AND user_id IS NOT NULL "
            + "AND payment_time >= #{start} AND payment_time < #{end}")
    List<Map<String, Object>> payUserDays(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内按「权益」(item_name) 聚合「充值成功的支付金额总和」：name=权益名称, amt=金额合计。
     *
     * <p>用于「付费流水权益占比」环形图。口径=充值成功的支付金额总和：
     * payments 取 payment_status='SUCCESS' 的 payment_amount，按 payment_time 落区间，经 order_no 关联 orders 取权益名称。</p>
     */
    @Select("SELECT o.item_name AS name, IFNULL(SUM(p.payment_amount), 0) AS amt "
            + "FROM payments p JOIN orders o ON o.order_no = p.order_no "
            + "WHERE p.payment_status = 'SUCCESS' AND p.payment_time >= #{start} AND p.payment_time < #{end} "
            + "GROUP BY o.item_name ORDER BY amt DESC")
    List<Map<String, Object>> payAmountByBenefit(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内按天 × 权益(item_name) 聚合「充值成功的支付金额总和」：d=支付日, name=权益名称, amt=金额合计。
     *
     * <p>用于「付费流水构成（按权益）」多折线趋势图。口径=充值成功的支付金额总和：
     * payments 取 payment_status='SUCCESS' 的 payment_amount，按 payment_time 落桶，经 order_no 关联 orders 取权益名称。</p>
     */
    @Select("SELECT DATE(p.payment_time) AS d, o.item_name AS name, IFNULL(SUM(p.payment_amount), 0) AS amt "
            + "FROM payments p JOIN orders o ON o.order_no = p.order_no "
            + "WHERE p.payment_status = 'SUCCESS' AND p.payment_time >= #{start} AND p.payment_time < #{end} "
            + "GROUP BY DATE(p.payment_time), o.item_name ORDER BY d")
    List<Map<String, Object>> payAmountByBenefitDaily(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 区间 [start,end) 内已支付订单的购买流水（uid=用户ID, name=权益名, t=下单时间，精确到时刻）。
     *
     * <p>用于「商品复购率」：在所选区间内，按购买时间(create_time)判定首购与复购
     * （首次购买后，再次购买的时间与首购不同即为复购，含同日不同时刻）。
     * 数据源为 zhouyi 的 orders 表，仅 order_status='PAID'。</p>
     */
    @Select("SELECT user_id AS uid, item_name AS name, create_time AS t "
            + "FROM orders "
            + "WHERE order_status = 'PAID' AND user_id IS NOT NULL "
            + "AND create_time >= #{start} AND create_time < #{end} "
            + "ORDER BY create_time")
    List<Map<String, Object>> paidOrderStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 全量「设备」首次出现时间（firstTime=该设备最早事件时间）。
     *
     * <p>设备指纹 = custom_event_property 中同一事件(custom_event_id)的 device_model 与 device_os
     * 两个属性值组合，按指纹去重后取 MIN(create_time) 作为该设备首次出现时间。
     * 上层据此计算「每日新增设备」与「累计设备数」。</p>
     */
    @Select("SELECT MIN(ev.t) AS firstTime FROM ( "
            + "  SELECT m.custom_event_id AS eid, "
            + "         CONCAT(CAST(m.property_value AS CHAR), '||', CAST(o.property_value AS CHAR)) AS fp, "
            + "         m.create_time AS t "
            + "  FROM custom_event_property m "
            + "  JOIN custom_event_property o "
            + "    ON o.custom_event_id = m.custom_event_id "
            + "   AND o.property_name = 'device_os' AND o.deleted_flag = 'N' "
            + "  WHERE m.property_name = 'device_model' AND m.deleted_flag = 'N' "
            + ") ev GROUP BY ev.fp")
    List<Map<String, Object>> deviceFirstSeenTimes();

    /**
     * 区间 [start,end) 内「账号登录」(user_login) 事件携带的设备指纹流水（fp=型号||系统, t=事件时间）。
     *
     * <p>用于「新增设备占比」的分母——按周期对登录设备指纹去重得到「账号登录的触发设备号数」。
     * 设备指纹 = 同一登录事件(custom_event_id)的 device_model 与 device_os 属性值组合。</p>
     */
    @Select("SELECT CONCAT(CAST(m.property_value AS CHAR), '||', CAST(o.property_value AS CHAR)) AS fp, "
            + "       e.create_time AS t "
            + "FROM custom_event e "
            + "JOIN custom_event_property m ON m.custom_event_id = e.id "
            + "  AND m.property_name = 'device_model' AND m.deleted_flag = 'N' "
            + "JOIN custom_event_property o ON o.custom_event_id = e.id "
            + "  AND o.property_name = 'device_os' AND o.deleted_flag = 'N' "
            + "WHERE e.deleted_flag = 'N' AND e.event_name = 'user_login' "
            + "  AND e.create_time >= #{start} AND e.create_time < #{end}")
    List<Map<String, Object>> loginDeviceStream(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 各渠道新增玩家：按注册日 × 渠道统计「账号注册」触发用户数（d=注册日, channel=渠道码, c=新增数）。
     *
     * <p>口径=「账号注册」的触发用户数按渠道拆分：user 表一人一行，COUNT(*) 即该渠道去重注册用户数。
     * 渠道取该用户最早一条带 channel 属性事件的 property_value（JSON 去引号）；
     * custom_event.creator(varchar) 经 CAST 为数值与 user.id 关联，规避排序规则冲突。
     * 仅统计有渠道记录的新增用户。channel 原始码由前端映射为展示名。</p>
     */
    @Select("SELECT DATE(u.createTime) AS d, ch.channel AS channel, COUNT(*) AS c "
            + "FROM `user` u "
            + "JOIN ( "
            + "  SELECT e.creator AS uid, "
            + "         SUBSTRING_INDEX(GROUP_CONCAT(JSON_UNQUOTE(p.property_value) ORDER BY e.create_time ASC SEPARATOR 0x1F), 0x1F, 1) AS channel "
            + "  FROM custom_event e "
            + "  JOIN custom_event_property p ON p.custom_event_id = e.id AND p.property_name = 'channel' AND p.deleted_flag = 'N' "
            + "  WHERE e.deleted_flag = 'N' AND e.creator IS NOT NULL "
            + "  GROUP BY e.creator "
            + ") ch ON CAST(ch.uid AS UNSIGNED) = u.id "
            + "WHERE u.createTime >= #{start} AND u.createTime < #{end} "
            + "GROUP BY DATE(u.createTime), ch.channel ORDER BY d")
    List<Map<String, Object>> channelNewPlayerCounts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
