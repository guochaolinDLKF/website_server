package com.ydzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydzz.entity.VisitInfo;

/**
 * 访问信息 Service 接口
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
public interface VisitInfoService extends IService<VisitInfo> {

    /**
     * 处理一次页面访问
     * 根据 IP 查询地理位置信息，记录或更新访问数据
     *
     * @param ip 访问者 IP 地址
     */
    void recordVisit(String ip);
}
