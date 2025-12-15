package cn.edu.xmu.oomall.aftersale.controller.dto;

import lombok.Data;

/**
 * 售后单审核的请求体DTO
 */
@Data // Lombok注解，自动生成getter/setter等
public class AftersaleConfirmDto {
    /**
     * 是否同意：true=同意，false=不同意
     */
    private Boolean confirm;

    /**
     * 审核结论（如拒绝理由）
     */
    private String conclusion;

    /**
     * 售后类型：0换货，1退货，2维修
     */
   // private Integer type;
}