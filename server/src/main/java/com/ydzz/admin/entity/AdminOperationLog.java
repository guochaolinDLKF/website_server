package com.ydzz.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台操作日志
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@TableName("admin_operation_log")
@Schema(description = "后台操作日志")
public class AdminOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private String username;

    private String module;

    private String operation;

    @Schema(description = "类.方法")
    private String method;

    private String requestUri;

    @Schema(description = "HTTP 方法")
    private String requestMethod;

    private String requestParam;

    private String responseResult;

    private String ip;

    @Schema(description = "耗时(毫秒)")
    private Long costMs;

    @Schema(description = "1成功 0异常")
    private Integer status;

    private String errorMsg;

    private LocalDateTime createTime;
}
