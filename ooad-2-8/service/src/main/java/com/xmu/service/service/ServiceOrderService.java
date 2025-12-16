package com.xmu.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.controller.dto.CreateServiceOrderDto;
import com.xmu.service.Dao.bo.ServiceOrderBo ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 服务单领域服务（统筹业务流程）
 */
@Service
public class ServiceOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOrderService.class);
    @Autowired
    private ServiceOrderDao serviceOrderDao;

    /**
     * 创建服务单核心流程
     */
    public ServiceOrderBo createServiceOrder(Long shopId, Long aftersalesId, CreateServiceOrderDto dto){
        LOGGER.info("开始创建服务单，商铺ID={}, 售后单ID={}, 请求参数={}",
                shopId, aftersalesId, dto);
        // 1. 初始化BO
        ServiceOrderBo bo = new ServiceOrderBo(dto, shopId, aftersalesId);
        // 2. 校验业务规则
        bo.validate();
        LOGGER.info("服务单参数校验通过，预生成服务单号={}", bo.getServiceSn());
        // 3. 持久化数据
        serviceOrderDao.save(bo);
        LOGGER.info("服务单持久化完成，服务单ID={}, 服务单号={}", bo.getId(), bo.getServiceSn());
        return bo;
    }
}