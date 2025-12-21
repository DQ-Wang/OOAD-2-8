package cn.edu.xmu.oomall.aftersale.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ExchangeProduct;
import cn.edu.xmu.oomall.aftersale.Dao.bo.Maintenance;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ExchangeProductBuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 4;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        ExchangeProduct bo=new ExchangeProduct(dao);
        BeanUtils.copyProperties(po,bo);
        return bo;
    }
}