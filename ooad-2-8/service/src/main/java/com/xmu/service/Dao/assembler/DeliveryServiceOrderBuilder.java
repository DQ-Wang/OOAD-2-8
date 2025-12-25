package com.xmu.service.Dao.assembler;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.DeliveryServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 配送服务单构建器
 */
@Component
public class DeliveryServiceOrderBuilder implements ServiceOrderBuilder {

    @Override
    public Byte getType() {
        return ServiceOrder.TYPE_DELIVERY;
    }

    @Override
    public ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao) {
        DeliveryServiceOrder bo = new DeliveryServiceOrder();
        BeanUtils.copyProperties(po, bo);
        return bo;
    }

    @Override
    public ServiceOrder createFromDto(ServiceOrderDto dto, Long shopId, Long afterSaleId) {
        DeliveryServiceOrder bo = new DeliveryServiceOrder();
        // 先拷贝 DTO 中同名字段，忽略主键和系统字段
        BeanUtils.copyProperties(dto, bo, "id", "status", "shopId", "aftersalesId", "createTime");
        // 填充系统字段
        bo.setStatus(ServiceOrder.STATUS_NEW);
        bo.setShopId(shopId);
        bo.setAftersalesId(afterSaleId);
        bo.setCreateTime(LocalDate.now().atStartOfDay());
        return bo;
    }
}
