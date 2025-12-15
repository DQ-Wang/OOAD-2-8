package cn.edu.xmu.oomall.aftersale.Dao.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 对应类图中的`aftersale`：售后业务抽象父类
 * 包含所有售后类型的通用属性与方法，由子类（仅退款/维修/退货退款/换货）继承
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class Aftersale {
    // 类图中定义的通用属性
    private Long aftersaleId;    // 售后单ID（主键）
    private Long shopId;         // 店铺ID
    private long productId;      // 商品ID
    private Long regionId;       // 地区ID（关联region模块的RegionPo）
    private long orderId;        // 订单ID
    private long serviceSn;      // 服务单ID
    private Long customerId;     // 顾客ID
    private Byte type;           // 售后类型：1=维修 2=仅退款 3=退货退款 4=换货
    private Byte status;         // 售后状态：0=待审核 1=已同意 2=已拒绝 3=已完成
    private String reason;       // 售后原因





    // 父类的抽象方法：子类必须进行重写
    // 审核售后
    public abstract boolean HandleAftersale(boolean confirm, String reason);

//    // 类图中定义的通用方法：确认商品
//    public void confirmProduct(boolean confirm, String reason) {
//        // 通用逻辑：记录商品确认状态（子类可重写扩展）
//        this.setReason(reason);
//        // 示例：若确认则更新状态（具体逻辑可根据业务调整）
//        if (confirm) {
//            this.setStatus((byte) 1);
//        }
//    }

    // 类图中定义的通用方法：设置售后状态
    public void SetStatus(boolean confirm, String reason) {
        // 通用逻辑：更新售后状态（子类可重写扩展）
        this.setReason(reason);
        this.setStatus(confirm ? (byte) 1 : (byte) 2);
    }
}