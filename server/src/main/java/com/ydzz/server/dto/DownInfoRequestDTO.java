package com.ydzz.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownInfoRequestDTO {

    @NotBlank(message = "IP地址不能为空")
    private String ip;

    @NotBlank(message = "下载平台不能为空")
    private String downPlatform;
}