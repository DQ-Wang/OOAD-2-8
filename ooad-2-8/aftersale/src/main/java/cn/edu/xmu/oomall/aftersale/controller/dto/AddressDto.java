package cn.edu.xmu.oomall.aftersale.controller.dto;


import lombok.Data;

@Data
public class AddressDto {
    /**
     * 收件人姓名
     */
    private String name;

    /**
     * 收件人手机号
     */
    private String mobile;

    /**
     * 地区ID（省/市/区三级编码，如行政区域ID）
     */
    private Long regionId;

    /**
     * 详细地址
     */
    private String address;
}
