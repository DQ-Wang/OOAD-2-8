package com.xmu.service.service;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.controller.dto.AppointmentDto;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.service.vo.ServiceOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


/**
 * 服务单领域服务（统筹业务流程）
 */
@Service
public class ServiceOrderService {
    @Autowired
    private ServiceOrderDao serviceOrderDao;


/**
     * 创建服务单
     * @param shopId 店铺ID
     * @param afterSaleId 售后单ID
     * @param dto 创建服务单请求体参数
     * @return 创建后的服务单对象
     */
    public ServiceOrderVo createServiceOrder(Long shopId, Long afterSaleId, ServiceOrderDto dto) {
        ServiceOrder serviceOrder = ServiceOrder.create(shopId, afterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        return toVo(serviceOrder);
    }


    /**
     * 接收服务单
     * @param providerId 服务提供商ID
     * @param serviceOrderId 服务单ID
     * @return 接受后的服务单对象
     */
    public ServiceOrderVo acceptServiceOrder(Long providerId, Long serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.acceptByProvider(providerId);
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;
    }


    /**
     * 指派服务单给维修师傅
     */
    public ServiceOrderVo assignToWorker(Long providerId, Long workerId, Long serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.assign(providerId, workerId);
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;
    }

    /**
     * 完成服务单
     */
    public ServiceOrderVo finishServiceOrder(Long workerId, Long serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.finish(workerId);
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;
    }
    /**
     * 取消服务单
     */
    public ServiceOrderVo cancelServiceOrder(Long serviceOrderId) {
        ServiceOrder serviceOrder =serviceOrderDao.findById(serviceOrderId);
        serviceOrder.cancel();
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;

    }
    /**
     * 服务商收到顾客寄件
     */
    public ServiceOrderVo receiveDelivery(Long providerId, Long serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.doReceive(providerId);
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;
    }

    /**
     * 维修师傅预约上门
     */
    public ServiceOrderVo appointment(Long workerId, Long serviceOrderId, AppointmentDto appointmentDto) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        // 直接使用请求体中的预约时间
        LocalDateTime time = appointmentDto.getAppointmentTime();
        serviceOrder.doAppoint(workerId, time);
        serviceOrderDao.update(serviceOrder);
        return toVo(serviceOrder) ;
    }

    private ServiceOrderVo toVo(ServiceOrder bo) {
        ServiceOrderVo vo = new ServiceOrderVo();
        BeanUtils.copyProperties(bo, vo);
        vo.setTypeName(bo.getTypeName());
        vo.setStatusName(bo.getStatusName());
        return vo;
    }


}