package com.ydzz.server.controller;

import com.ydzz.server.common.Result;
import com.ydzz.server.dto.DownInfoRequestDTO;
import com.ydzz.server.dto.VisitPageDTO;
import com.ydzz.server.dto.VisitPageRequestDTO;
import com.ydzz.server.service.DownInfoService;
import com.ydzz.server.service.VisitPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "游戏管理", description = "游戏相关接口")
public class GameController {

    private final DownInfoService downInfoService;
    private final VisitPageService visitPageService;

    @PostMapping("/down_info")
    @Operation(summary = "记录下载操作", description = "根据 IP 和下载平台记录用户下载操作")
    public Result<Void> recordDownload(@Valid @RequestBody DownInfoRequestDTO request) {
        downInfoService.recordDownload(request);
        return Result.success("记录成功", null);
    }

    @PostMapping("/visit_page")
    @Operation(summary = "记录页面访问", description = "根据 IP 地址记录页面访问，自动获取地理位置信息")
    public Result<VisitPageDTO> recordVisit(@Valid @RequestBody VisitPageRequestDTO request) {
        VisitPageDTO result = visitPageService.recordVisit(request.getIp());
        if (result != null) {
            return Result.success("访问记录成功", result);
        }
        return Result.error("获取 IP 信息失败");
    }
}
