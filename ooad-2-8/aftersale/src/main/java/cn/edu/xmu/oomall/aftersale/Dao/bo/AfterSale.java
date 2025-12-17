package cn.edu.xmu.oomall.aftersale.Dao.bo;


import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.AfterSaleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * 对应类图中的`aftersale`：售后业务抽象父类
 * 包含所有售后类型的通用属性与方法，由子类（仅退款/维修/退货退款/换货）继承
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public abstract class AfterSale {
    // 类图中定义的通用属性
    private Long aftersaleId;    // 售后单ID（主键）
    private Long shopId;         // 店铺ID
    private long productId;      // 商品ID
    private Long regionId;       // 地区ID（关联region模块的RegionPo）
    private long orderId;        // 订单ID
    protected Long serviceOrderId;      // 服务单ID
    private Long customerId;     // 顾客ID
    private Byte type;           // 售后类型：1=维修 2=仅退款 3=退货退款 4=换货
    private Byte status;         // 售后状态：0=待审核 1=已同意 2=已拒绝 3=已完成
    private String reason;       // 售后原因



    protected AfterSaleDao afterSaleDao;


    // 父类的抽象方法：子类必须进行重写
    // 审核售后
    public abstract boolean HandleAftersale(boolean confirm, String reason);


    // 类图中定义的通用方法：设置售后状态
    public void SetStatus(boolean confirm, String reason) {
        log.debug("SetStatus:aftersaleId={},confirm={}",
                aftersaleId, confirm);
        // 通用逻辑：更新售后状态（子类可重写扩展）
        this.setReason(reason);
        this.setStatus(confirm ? (byte) 1 : (byte) 2);
        AfterSalePo afterSalePo = new AfterSalePo();
        BeanUtils.copyProperties(this, afterSalePo); // 拷贝同名属性（驼峰命名需一致）
        this.afterSaleDao.saveAftersale(afterSalePo);
        log.debug("saveAftersale:aftersaleId={}",aftersaleId);
    }
}