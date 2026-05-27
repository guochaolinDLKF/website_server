package com.ydzz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 访问信息实体 — 记录每次网页访问的 IP 归属地和次数
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("visitinfo")
public class VisitInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId
    private String id;

    /**
     * 访问者 IP 地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 省份（国内 IP 通过高德地图查询）
     */
    @TableField("province")
    private String province;

    /**
     * 该 IP 的累计访问次数
     */
    @TableField("count")
    private Integer count;

    /**
     * 下载信息
     */
    @TableField("downInfo")
    private String downInfo;

    /**
     * 最近一次访问时间（毫秒时间戳字符串）
     */
    @TableField("lastVisitTime")
    private String lastVisitTime;
}
