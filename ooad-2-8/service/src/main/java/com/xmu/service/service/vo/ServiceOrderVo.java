package com.xmu.service.service.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/23 20:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ServiceOrderVo {
    private Long id;
    private Byte type;
    private String typeName;
    private Byte status;
    private String statusName;
    private String serviceConsignee;
    private String serviceMobile;
    private String address;
    private Long expressId;
    private Long workerId;
    private Long serviceProviderId;
    private Long shopId;
    private Long aftersalesId;
    private String description;
    private LocalDateTime appointmentTime;
    private LocalDateTime createTime;
}