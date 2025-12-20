package com.xmu.service.controller;
import com.xmu.service.controller.dto.CreateServiceOrderDto ;
import com.xmu.service.service.ServiceOrderService;
import com.xmu.service.Dao.bo.ServiceOrderBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务单Controller（接口入口）
 */
@RestController
@RequestMapping("/internal/shops")
public class ServiceOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOrderController.class);
    @Autowired
    private ServiceOrderService serviceOrderService;

    @PostMapping("/{shopId}/aftersales/{id}/serviceorders")
    public ResponseEntity<String> createServiceOrder(
            @PathVariable("shopId") Long shopId, // 接收商铺ID路径参数
            @PathVariable("id") Long aftersalesId, // 接收售后单ID路径参数
            @RequestBody CreateServiceOrderDto dto) {
        LOGGER.info("【ServiceOrder Controller】收到Feign调用创建服务单请求 - shopId={}, aftersalesId={}, customerId={}, productId={}, address={}, mobile={}",
                shopId, aftersalesId, dto.getCustomerId(), dto.getProductId(), dto.getAddress(), dto.getMobile());
        LOGGER.debug("【ServiceOrder Controller】完整请求体={}", dto);
        
        try {
            ServiceOrderBo bo = serviceOrderService.createServiceOrder(shopId, aftersalesId, dto);
            String serviceSn = bo.getServiceSn();
            LOGGER.info("【ServiceOrder Controller】服务单创建成功，准备返回 - shopId={}, aftersalesId={}, serviceSn={}, serviceOrderId={}", 
                    shopId, aftersalesId, serviceSn, bo.getId());
            return ResponseEntity.ok(serviceSn);
        } catch (Exception e) {
            LOGGER.error("【ServiceOrder Controller】创建服务单异常 - shopId={}, aftersalesId={}, 异常类型={}, 异常信息={}", 
                    shopId, aftersalesId, e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
}