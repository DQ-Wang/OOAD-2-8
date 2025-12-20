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

public class AftersaleProductVo {
    @Setter
    private Long aftersale_id;

    @Setter
    private Byte state = NULL;
    /**
     * 共三种状态
     */
    //已验收
    @ToString.Exclude
    @JsonIgnore
    public static final Byte VALID = 0;
    //已拒绝
    @ToString.Exclude
    @JsonIgnore
    public static final Byte REJECTED = 1;

    @ToString.Exclude
    @JsonIgnore
    public static final Byte NULL = 2;

}
