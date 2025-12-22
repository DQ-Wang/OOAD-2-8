package cn.edu.xmu.oomall.aftersale.Dao.bo;


import cn.edu.xmu.javaee.core.clonefactory.CopyFrom;
import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 对应类图中的`aftersale`：售后业务抽象父类
 * 包含所有售后类型的通用属性与方法，由子类（仅退款/维修/退货退款/换货）继承
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
@CopyFrom(AfterSalePo.class)
public abstract class AfterSale {
    // 类图中定义的通用属性
    private Long aftersaleId;    // 售后单ID（主键）
    private Long shopId;         // 店铺ID
    private long productId;      // 商品ID
    private Long regionId;       // 地区ID（关联region模块的RegionPo）
    private long orderId;        // 订单ID
    private Long customerId;     // 顾客ID
    private Byte type;           // 售后类型：1=维修 2=仅退款 3=退货退款 4=换货
    private Byte status;         // 售后状态：0=待审核 1=已同意 2=已拒绝 3=商家待收货 4=待分配服务商 5=服务待完成 6=已完成 7=已取消
    protected String reason;     // 审核原因
    protected String beanName;
    protected String mobile;
    protected String address;
    protected int quantity;
    protected String serviceOrderId;      //服务单ID
    protected String returnExpress;       //退货运单
    protected String deliverExpress;      //发货运单



    protected AfterSalePo aftersalePo;


    protected AfterSaleDao afterSaleDao;

    public AfterSale(AfterSaleDao afterSaleDao) {
        this.afterSaleDao = afterSaleDao;
    }


    public void setAftersalePo(AfterSalePo aftersalePo) {
        this.aftersalePo = aftersalePo;
        // 可选：加日志，验证setter是否被调用
        log.debug("AfterSale.aftersalePo已赋值：{}", aftersalePo);
    }


    // 父类的抽象方法：子类必须进行重写
    // 审核售后
    public abstract String HandleAftersale(boolean confirm, String reason);

    // 取消售后
    public abstract boolean CancleAftersale(String reason);



    // 类图中定义的通用方法：设置售后状态
    public void ConfirmAftersale(boolean confirm, String reason) {
        log.debug("SetStatus:aftersaleId={},confirm={}",
                aftersaleId, confirm);
        // 通用逻辑：更新售后状态（子类可重写扩展）
        this.setReason(reason);
        this.setStatus(confirm ? (byte) 1 : (byte) 2);
        log.debug("saveAftersale:aftersaleId={}",aftersaleId);
    }




}