package com.ydzz.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownInfoDTO {

    private Long id;

    @NotBlank(message = "游戏名称不能为空")
    private String gameName;

    @NotBlank(message = "版本号不能为空")
    private String version;

    @NotBlank(message = "下载地址不能为空")
    private String downloadUrl;

    private String description;

    private Integer sort;

    private Integer status;
}
