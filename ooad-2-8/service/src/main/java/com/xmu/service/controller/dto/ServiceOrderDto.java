
package com.xmu.service.controller.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * 创建服务单请求DTO
 */
@Getter
@Setter
public class ServiceOrderDto {
    private String afterSaleId;
    private String consignee;
    private String mobile;
    private String address;
    private Byte type;


}