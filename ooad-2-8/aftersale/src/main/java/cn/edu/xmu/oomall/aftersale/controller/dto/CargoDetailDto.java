package cn.edu.xmu.oomall.aftersale.controller.dto;


import lombok.Data;

@Data
public class CargoDetailDto {
    /**
     * 货物ID
     */
    private Long id;

    /**
     * 货物名称
     */
    private String name;

    /**
     * 货物数量
     */
    private Integer count;

    /**
     * 单位（如件/箱/吨）
     */
    private String unit;

    /**
     * 重量（可根据业务定义单位，如千克）
     */
    private Double weight;

    /**
     * 金额（分/元，根据业务精度定义，这里用Long避免浮点精度问题）
     */
    private Long amount;
}
