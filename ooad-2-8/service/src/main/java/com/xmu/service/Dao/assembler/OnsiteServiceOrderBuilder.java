package com.xmu.service.Dao.assembler;

import cn.edu.xmu.javaee.core.clonefactory.CloneFactory;
import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.OnSiteServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.stereotype.Component;

/**
 * 上门服务单构建器
 */

@Component
public class OnsiteServiceOrderBuilder implements ServiceOrderBuilder {

    @Override
    public String getType() {
        return ServiceOrder.TYPE_ON_SITE;
    }

    @Override
    public ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao) {
        OnSiteServiceOrder bo = CloneFactory.copy(new OnSiteServiceOrder(), po);
        bo.setServiceOrderDao(dao);
        return bo;
    }
}

