package cn.edu.xmu.oomall.aftersale.Dao.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ExchangeProduct;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExchangeProductBuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 4;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        ExchangeProduct bo=new ExchangeProduct(dao);
        BeanUtils.copyProperties(po,bo);
        bo.setAftersalePo(po);
        log.info("【ExchangeProduct Builder】已成功创建换货子类 - aftersaleId={}", po.getAftersaleId());
        return bo;
    }
}