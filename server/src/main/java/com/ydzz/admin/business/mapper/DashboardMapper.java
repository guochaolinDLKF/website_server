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
}
