package cn.edu.xmu.oomall.aftersale.service.vo;


//import cn.edu.xmu.javaee.core.clonefactory.CopyFrom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
// TODO:import Aftersalse

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
//@CopyFrom({AfterSale.class})
@Getter
@AllArgsConstructor

public class AftersaleVo {
    @Setter
    private Long aftersale_id;

    @Setter
    private String serviceOrder_id;
    /**
     * 共三种状态
     */
    //有效
    @ToString.Exclude
    @JsonIgnore
    public static final Byte VALID = 0;
    //停用
    @ToString.Exclude
    @JsonIgnore
    public static final Byte SUSPENDED = 1;
    //废弃
    @ToString.Exclude
    @JsonIgnore
    public static final Byte ABANDONED = 2;
}
