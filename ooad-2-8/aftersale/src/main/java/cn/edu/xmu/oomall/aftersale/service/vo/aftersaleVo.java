package cn.edu.xmu.oomall.aftersale.service.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import cn.edu.xmu.javaee.core.clonefactory.CopyFrom;
import lombok.Setter;
import lombok.ToString;
// TODO:import Aftersalse

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CopyFrom({Aftersale.class})
@Getter


public class aftersaleVo {
    @Setter
    private Long id;

    @Setter
    private Long shop_id;

    @Setter
    private Long product_id;

    @Setter
    private Long region_id;

    @Setter
    private Long order_id;

    @Setter
    private Long custom_id;

    @Setter
    private Byte status;

    @Setter
    private Long aftersale_sn;

    @Setter
    private String apply_reason;
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
