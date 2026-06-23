package com.xiaomayi.admin.controller;

import com.xiaomayi.core.utils.R;
import com.xiaomayi.logger.annotation.RequestLog;
import com.xiaomayi.logger.enums.RequestType;
import com.xiaomayi.system.dto.websocket.SocketMsgDTO;
import com.xiaomayi.websocket.server.WebSocketServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * Socket通讯 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Slf4j
@RestController
@RequestMapping("/websocket")
@Tag(name = "Socket通讯", description = "Socket通讯")
@AllArgsConstructor
public class WebSocketController {

    /**
     * 渲染模板
     * 特别说明：此处仅是测试案例，实际使用可直接删除
     *
     * @param cid 客户端ID
     * @return 返回结果
     */
    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid) {
        ModelAndView view = new ModelAndView("/websocket");
        view.addObject("cid", cid);
        return view;
    }

    /**
     * 发送消息
     *
     * @param socketMsgDTO 参数
     * @return 返回结果
     */
    @Operation(summary = "发送消息", description = "发送消息")
    @RequestLog(title = "发送消息", type = RequestType.OTHER)
    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody @Validated SocketMsgDTO socketMsgDTO) {
        try {
            WebSocketServer.sendInfo(socketMsgDTO.getMessage(), socketMsgDTO.getReceiveId());
        } catch (Exception e) {
            log.error("Socket消息发送失败：{}", e.getMessage());
        }
        return R.ok();
    }

}
