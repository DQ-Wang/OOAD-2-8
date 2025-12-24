package cn.edu.xmu.oomall.aftersale.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ReturnAndRefund;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReturnAndRefundbuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 3;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        ReturnAndRefund bo=new ReturnAndRefund(dao);
        BeanUtils.copyProperties(po,bo);
        bo.setAftersalePo(po);
        log.info("【ReturnAndRefund Builder】已成功创建退货退款子类 - aftersaleId={}", po.getAftersaleId());
        return bo;
    }
}
