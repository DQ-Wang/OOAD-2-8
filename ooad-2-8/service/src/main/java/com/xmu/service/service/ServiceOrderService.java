package com.xmu.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.controller.dto.CreateServiceOrderDto;
import com.xmu.service.Dao.bo.ServiceOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 服务单领域服务（统筹业务流程）
 */
@Service
public class ServiceOrderService {
    @Autowired
    private ServiceOrderDao serviceOrderDao;

    private ServiceOrder serviceOrder;
    /**
     * 创建服务单核心流程
     */

    /**
     * 接收服务单
     */
    public void acceptServiceOrder(Long providerId,Long serviceOrderId) {

        serviceOrder =  serviceOrderDao.findById(serviceOrderId);

        serviceOrder.acceptByProvider(providerId);
    }
    /**
     * 完成服务单
     */
    public void finishServiceOrder(Long workerId,Long serviceOrderId) {
        serviceOrder = serviceOrderDao.findById(serviceOrderId);

        serviceOrder.finish(workerId);
    }
    /**
     * 取消服务单
     */
    //public void cancelServiceOrder(Long serviceOrderId) {
    //    serviceOrderDao.findById(serviceOrderId);
    //}
    /**
     * 服务商收到顾客寄件
     */
    public void receiveDeliveryreceive(Long providerId,Long serviceOrderId) {
        serviceOrder = serviceOrderDao.findById(serviceOrderId);
        serviceOrder.doReceive(providerId);
    }

    /**
     * 维修师傅预约上门
     */
    //public void appointment(Long workerId,Long serviceOrderId,appointment_dto) {
    //   serviceOrder = serviceOrderDao.findById(serviceOrderId);
    //    onSiteServiceOrder doAppoint(workerId,appointment_dto);
    //}


}