package cn.edu.xmu.oomall.aftersale.controller.dto;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
//import cn.edu.xmu.javaee.core.clonefactory.CopyTo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 售后单审核的请求体DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data // Lombok注解，自动生成getter/setter等
//@CopyTo(AfterSale.class)
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