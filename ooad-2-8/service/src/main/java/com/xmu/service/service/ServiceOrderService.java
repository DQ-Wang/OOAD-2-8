package com.xmu.service.service;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.Worker;
import com.xmu.service.controller.dto.AppointmentDto;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.Dao.WorkerDao;
import com.xmu.service.controller.dto.ServiceOrderDto;
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

    @Autowired
    private WorkerDao workerDao;

/**
     * 创建服务单
     * @param shopId 店铺ID
     * @param afterSaleId 售后单ID
     * @param dto 创建服务单请求体参数
     * @return 创建后的服务单对象
     */
    public ServiceOrder createServiceOrder(String shopId, String afterSaleId, ServiceOrderDto dto) {
        ServiceOrder serviceOrder = ServiceOrder.create(shopId, afterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        return serviceOrder;
    }


    /**
     * 接收服务单
     * @param providerId 服务提供商ID
     * @param serviceOrderId 服务单ID
     * @return 接受后的服务单对象
     */
    public ServiceOrder acceptServiceOrder(String providerId, String serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.acceptByProvider(providerId);
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;
    }


    /**
     * 指派服务单给维修师傅
     */
    public ServiceOrder assignToWorker(String providerId, String workerId, String serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.assign(providerId, workerId);
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;
    }

    /**
     * 完成服务单
     */
    public ServiceOrder finishServiceOrder(String workerId, String serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.finish(workerId);
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;
    }
    /**
     * 取消服务单
     */
    public ServiceOrder cancelServiceOrder(String serviceOrderId) {
        ServiceOrder serviceOrder =serviceOrderDao.findById(serviceOrderId);
        serviceOrder.cancel();
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;

    }
    /**
     * 服务商收到顾客寄件
     */
    public ServiceOrder receiveDelivery(String providerId, String serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.doReceive(providerId);
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;
    }

    /**
     * 维修师傅预约上门
     */
    public ServiceOrder appointment(String workerId, String serviceOrderId, AppointmentDto appointmentDto) {
        ServiceOrder serviceOrder = serviceOrderDao.findById(serviceOrderId);
        // 解析预约日期（yyyy-MM-dd），存为 LocalDateTime 00:00:00
        LocalDateTime time = java.time.LocalDate.parse(appointmentDto.getAppointmentDate()).atStartOfDay();
        serviceOrder.doAppoint(workerId, time);
        serviceOrderDao.update(serviceOrder);
        return serviceOrder ;
    }


}