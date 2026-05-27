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

    /**
     * 记录一次下载行为
     * 根据 IP 和下载平台，更新该 IP 对应记录的下载信息
     *
     * @param ip       访问者 IP 地址
     * @param platform 下载平台标识（如 iOS、Android、Web 等）
     */
    void recordDownload(String ip, String platform);
}
