package com.xmu.service.Dao.assembler;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.DeliveryServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 配送服务单构建器
 */
@Component
public class DeliveryServiceOrderBuilder implements ServiceOrderBuilder {

    @Override
    public String getType() {
        return ServiceOrder.TYPE_DELIVERY;
    }

    @Override
    public ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao) {
        DeliveryServiceOrder bo = new DeliveryServiceOrder();
        BeanUtils.copyProperties(po, bo);
        bo.setServiceOrderDao(dao);
        return bo;
    }
}
