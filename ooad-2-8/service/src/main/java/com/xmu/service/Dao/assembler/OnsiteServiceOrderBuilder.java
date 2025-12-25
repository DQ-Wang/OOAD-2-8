package com.xmu.service.Dao.assembler;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.OnSiteServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 上门服务单构建器
 */
@Component
public class OnsiteServiceOrderBuilder implements ServiceOrderBuilder {

    @Override
    public Byte getType() {
        return ServiceOrder.TYPE_ON_SITE;
    }

    @Override
    public ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao) {
        OnSiteServiceOrder bo = new OnSiteServiceOrder();
        BeanUtils.copyProperties(po, bo);
        return bo;
    }

    @Override
    public ServiceOrder createFromDto(ServiceOrderDto dto, Long shopId, Long afterSaleId) {
        OnSiteServiceOrder bo = new OnSiteServiceOrder();
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

