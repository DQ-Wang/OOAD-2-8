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
        LOGGER.info("【ServiceOrder Service】开始创建服务单流程 - shopId={}, aftersalesId={}, customerId={}, productId={}",
                shopId, aftersalesId, dto.getCustomerId(), dto.getProductId());
        LOGGER.debug("【ServiceOrder Service】完整请求参数={}", dto);
        
        // 1. 初始化BO
        LOGGER.debug("【ServiceOrder Service】开始初始化ServiceOrderBo");
        ServiceOrderBo bo = new ServiceOrderBo(dto, shopId, aftersalesId);
        LOGGER.info("【ServiceOrder Service】BO初始化完成，预生成服务单号={}", bo.getServiceSn());
        
        // 2. 校验业务规则
        LOGGER.debug("【ServiceOrder Service】开始校验业务规则");
        bo.validate();
        LOGGER.info("【ServiceOrder Service】业务规则校验通过 - shopId={}, aftersalesId={}, 服务单号={}", 
                shopId, aftersalesId, bo.getServiceSn());
        
        // 3. 持久化数据
        LOGGER.info("【ServiceOrder Service】开始持久化服务单到数据库 - serviceSn={}", bo.getServiceSn());
        serviceOrderDao.save(bo);
        LOGGER.info("【ServiceOrder Service】服务单持久化完成 - shopId={}, aftersalesId={}, serviceOrderId={}, serviceSn={}", 
                shopId, aftersalesId, bo.getId(), bo.getServiceSn());
        
        return bo;
    }
}