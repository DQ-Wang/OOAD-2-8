package cn.edu.xmu.oomall.aftersale.controller.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 创建服务单请求DTO
 */
public class CreateServiceOrderDto {

    @Setter
    @Getter
    private String consignee;


    @Setter
    @Getter
    private String mobile;


    @Setter
    @Getter
    private String address;
    @Setter
    @Getter
    private Byte type;
    @Setter
    @Getter
    private String problemImageUrl;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private Long productId;
    @Setter
    @Getter
    private Long customerId;
    @Setter
    @Getter
    private Long expressId;
    @Setter
    @Getter
    private Long afterSaleId;
    @Setter
    @Getter
    private Byte serviceType;


}