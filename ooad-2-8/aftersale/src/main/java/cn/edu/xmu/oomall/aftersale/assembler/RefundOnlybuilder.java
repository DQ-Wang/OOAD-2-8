package cn.edu.xmu.oomall.aftersale.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.Maintenance;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
public class RefundOnlybuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 2;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        Maintenance bo=new Maintenance(dao);
        BeanUtils.copyProperties(po,bo);
        bo.setAftersalePo(po);
        return bo;
    }
}