package com.ydzz.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "visitinfo")
public class VisitPage {

    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @Column(name = "ip")
    private String pageKey;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "count")
    private Integer visitCount;

    @Column(name = "downInfo", length = 500)
    private String downInfo;

    @Column(name = "lastVisitTime")
    private String lastVisitTime;

    @Transient
    private LocalDateTime createTime;

    @Transient
    private LocalDateTime updateTime;
}
