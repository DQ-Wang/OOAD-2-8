package cn.edu.xmu.oomall.aftersale.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ReturnAndRefund;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ReturnAndRefundbuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 3;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        ReturnAndRefund bo=new ReturnAndRefund(dao);
        BeanUtils.copyProperties(po,bo);
        return bo;
    }
}
