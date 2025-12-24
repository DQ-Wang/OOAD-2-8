package com.xmu.service.controller;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import com.xmu.service.controller.dto.AppointmentDto;
import com.xmu.service.service.ServiceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xmu.service.controller.dto.ServiceOrderDto;

/**
 * 服务单Controller（接口入口）
 */
@Slf4j
@RestController
public class ServiceOrderController {


    @Autowired
    private ServiceOrderService serviceOrderService;

    @PostMapping("/internal/shops/{shopId}/aftersales/{id}/serviceorders")
    public ReturnObject createServiceOrder(
            @PathVariable("shopId") Long shopId, // 接收店铺ID路径参数
            @PathVariable("id") Long afterSaleId, // 接收售后单ID路径参数
            @RequestBody ServiceOrderDto dto) // 接收创建服务单请求体参数
    {

            // 调用Service层方法创建服务订单，返回创建后的服务单VO
            var serviceOrder = serviceOrderService.createServiceOrder(shopId, afterSaleId, dto);
            log.info("服务单创建成功 - shopId={}, afterSaleId={}, dto={}",
                    shopId, afterSaleId, dto);
            return new ReturnObject(serviceOrder);

    }

    @PostMapping("/serviceproviders/{did}/services/{id}/accept")
    public ReturnObject acceptServiceOrder(
            @PathVariable("did") Long providerId, // 接收服务提供商ID路径参数
            @PathVariable("id") Long serviceOrderId) // 接收服务单ID路径参数
    {
        
            // 调用Service层方法接受服务订单
        var serviceOrder =serviceOrderService.acceptServiceOrder(providerId, serviceOrderId);
            log.info("服务商接单成功 - providerId={}, serviceOrderId={}",
                    providerId, serviceOrderId);
            return new ReturnObject(serviceOrder);
      
    }


    @PostMapping("/workers/{workerId}/services/{id}/finish")
    public ReturnObject finishServiceOrder(
            @PathVariable("workerId") Long workerId, // 接收维修工ID路径参数
            @PathVariable("id") Long serviceOrderId) // 接收服务单ID路径参数
    {
        
            // 调用Service层方法完成服务订单
        var serviceOrder =serviceOrderService.finishServiceOrder(workerId, serviceOrderId);
            log.info("服务单完成成功 - workerId={}, serviceOrderId={}",
                    workerId, serviceOrderId);
            return new ReturnObject(serviceOrder);
       
    }

    @PostMapping("/services/{id}/cancel")
    public ReturnObject cancelServiceOrder(
            @PathVariable("id") Long serviceOrderId) // 接收服务单ID路径参数
    {
        
            // 调用Service层方法取消服务订单
        var serviceOrder =serviceOrderService.cancelServiceOrder(serviceOrderId);
            log.info("服务单取消成功 - serviceOrderId={}", serviceOrderId);
            return new ReturnObject(serviceOrder);
       
    }

    @PostMapping("/serviceproviders/{did}/services/{id}/receive")
    public ReturnObject receiveDelivery(
            @PathVariable("did") Long providerId, // 接收服务提供商ID路径参数
            @PathVariable("id") Long serviceOrderId) // 接收服务单ID路径参数
    {
       
            // 调用Service层方法接受服务订单，返回接受后的服务单对象
        var serviceOrder =serviceOrderService.receiveDelivery(providerId, serviceOrderId);

            log.info("服务商收件成功 - providerId={}, serviceOrderId={}",
                    providerId, serviceOrderId);
            return new ReturnObject(serviceOrder);
      
    }

    @PostMapping("/workers/{did}/service/{id}/appointment")
    public ReturnObject appointment(
            @PathVariable("did") Long workerId, // 接收维修工ID路径参数
            @PathVariable("id") Long serviceOrderId,
            @RequestBody AppointmentDto appointmentDto) // 预约时间在请求体中
    {
        // 调用Service层方法预约上门服务
        var serviceOrder =serviceOrderService.appointment(workerId, serviceOrderId, appointmentDto);
        log.info("维修师傅预约上门成功 - workerId={}, serviceOrderId={}",
                workerId, serviceOrderId);
        return new ReturnObject(serviceOrder);
    }

    @PostMapping("/serviceproviders/{did}/services/{id}/dispatch")
    public ReturnObject dispatch(
            @PathVariable("did") Long providerId, // 接收服务提供商ID路径参数
            @PathVariable("id") Long serviceOrderId,
            @RequestParam("workerId") Long workerId) // 维修工ID作为请求参数传递
    {
        // 调用Service层方法指派维修工
        var serviceOrder =serviceOrderService.assignToWorker(providerId, workerId, serviceOrderId);
        log.info("指派维修工成功 - providerId={}, workerId={}, serviceOrderId={}",
                providerId, workerId, serviceOrderId);
        return new ReturnObject(serviceOrder);
    }
}
