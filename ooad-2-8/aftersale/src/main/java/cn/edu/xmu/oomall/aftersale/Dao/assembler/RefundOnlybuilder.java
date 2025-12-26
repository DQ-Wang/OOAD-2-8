package cn.edu.xmu.oomall.aftersale.Dao.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.RefundOnly;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RefundOnlybuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 2;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        RefundOnly bo=new RefundOnly(dao);
        BeanUtils.copyProperties(po,bo);
        bo.setAftersalePo(po);
        log.info("【RefundOnly Builder】已成功创建仅退款子类 - aftersaleId={}", po.getAftersaleId());
        return bo;
    }
}