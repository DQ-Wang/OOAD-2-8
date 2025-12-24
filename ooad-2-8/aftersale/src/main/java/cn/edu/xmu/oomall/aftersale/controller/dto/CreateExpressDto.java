package cn.edu.xmu.oomall.aftersale.controller.dto;

import lombok.Data;

import java.util.List;


@Data
public class CreateExpressDto {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 支付方式（1:寄方付 2:收方付）
     */
    private Integer payMethod;

    /**
     * 收货地址信息
     */
    private AddressDto address;

    /**
     * 货物详情列表
     */
    private List<CargoDetailDto> cargoDetails;
}
