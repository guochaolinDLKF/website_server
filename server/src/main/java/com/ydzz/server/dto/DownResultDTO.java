package com.ydzz.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownResultDTO {
    private int downCount;
    private String platform;
    private String downTime;
}