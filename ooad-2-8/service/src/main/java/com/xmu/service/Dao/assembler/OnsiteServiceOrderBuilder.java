package com.xmu.service.Dao.assembler;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.OnSiteServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.BeanUtils;
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
        OnSiteServiceOrder bo = new OnSiteServiceOrder();
        BeanUtils.copyProperties(po, bo);
        bo.setServiceOrderDao(dao);
        return bo;
    }
}

