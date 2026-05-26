package com.ydzz.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitPageDTO {

    private String id;

    private String pageKey;

    private String country;

    private String city;

    private String province;

    private Integer visitCount;

    private String downInfo;

    private String lastVisitTime;
}
