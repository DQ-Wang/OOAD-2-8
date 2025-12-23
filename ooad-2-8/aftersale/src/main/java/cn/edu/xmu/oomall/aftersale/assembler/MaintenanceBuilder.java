package cn.edu.xmu.oomall.aftersale.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.Maintenance;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MaintenanceBuilder implements AfterSaleBuilder {
    @Override
    public Byte getType() {
        return 1;
    }

    @Override
    public AfterSale build(AfterSalePo po, AfterSaleDao dao) {
        Maintenance bo=new Maintenance(dao);
        BeanUtils.copyProperties(po,bo);
        bo.setAftersalePo(po);
        log.info("【Maintenace Builder】已成功创建维修子类 - aftersaleId={}", po.getAftersaleId());
        return bo;
    }
}
