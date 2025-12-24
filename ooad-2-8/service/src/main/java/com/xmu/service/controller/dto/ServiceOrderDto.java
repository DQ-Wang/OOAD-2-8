
package com.xmu.service.controller.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * 创建服务单请求DTO
 */
@Getter
@Setter
public class ServiceOrderDto {
    private String serviceConsignee;
    private String serviceMobile;
    private String address;
    private String description;
    private Byte type;
}