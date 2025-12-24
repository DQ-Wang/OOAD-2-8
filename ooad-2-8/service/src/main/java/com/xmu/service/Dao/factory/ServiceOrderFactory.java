package com.xmu.service.Dao.factory;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.bo.DeliveryServiceOrder;
import com.xmu.service.Dao.bo.OnSiteServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 服务单工厂，按类型创建具体 ServiceOrder 子类
 * 新增类型仅需在 REGISTRY 注册，不改工厂逻辑，符合开闭原则
 */
public class ServiceOrderFactory {

    private static final Map<Byte, Supplier<ServiceOrder>> REGISTRY = new HashMap<>() {{
        put(ServiceOrder.TYPE_ON_SITE, OnSiteServiceOrder::new);
        put(ServiceOrder.TYPE_DELIVERY, DeliveryServiceOrder::new);
    }};

    public static ServiceOrder create(Long shopId, Long afterSaleId, ServiceOrderDto dto) {
        if (dto == null || dto.getType() == null) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "服务单类型不能为空");
        }
        Supplier<ServiceOrder> supplier = REGISTRY.get(dto.getType());
        if (supplier == null) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "未知的服务单类型");
        }
        ServiceOrder bo = supplier.get();
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

