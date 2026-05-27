package com.ydzz.controller;

import com.ydzz.common.Result;
import com.ydzz.service.VisitInfoService;
import com.ydzz.util.ClientIpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 通用接口控制器 — 页面访问统计等
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/game")
@Tag(name = "通用业务接口", description = "页面访问记录等通用业务")
public class GameController {

    @Autowired
    private VisitInfoService visitInfoService;

    @Operation(summary = "记录页面访问", description = "默认从请求中自动解析客户端外网 IP，也可手动传入 IP")
    @PostMapping("/visit_page")
    public Result<String> visitPage(
            @Parameter(description = "访问者 IP（可选，不传则自动从请求头解析）", example = "114.114.114.114")
            @RequestParam(value = "ip", required = false) String ip,
            HttpServletRequest request) {

        ip = StringUtils.hasText(ip) ? ip : ClientIpUtil.getClientIp(request);

        try {
            log.info("记录页面访问 — IP: {}", ip);
            visitInfoService.recordVisit(ip);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录页面访问失败 — IP: {}, 异常: {}", ip, e.getMessage(), e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "记录下载信息", description = "根据客户端 IP 和下载平台记录下载行为，IP 默认从请求头自动解析")
    @PostMapping("/down_info")
    public Result<String> downInfo(
            @Parameter(description = "访问者 IP（可选，不传则自动从请求头解析）", example = "114.114.114.114")
            @RequestParam(value = "ip", required = false) String ip,
            @Parameter(description = "下载平台标识", example = "iOS", required = true)
            @RequestParam("downPlatform") String downPlatform,
            HttpServletRequest request) {

        ip = StringUtils.hasText(ip) ? ip : ClientIpUtil.getClientIp(request);

        try {
            log.info("记录下载信息 — IP: {}, 平台: {}", ip, downPlatform);
            visitInfoService.recordDownload(ip, downPlatform);
            return Result.success("记录成功");
        } catch (Exception e) {
            log.error("记录下载信息失败 — IP: {}, 平台: {}, 异常: {}", ip, downPlatform, e.getMessage(), e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
}
