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
        LOGGER.info("接收到创建服务单请求：商铺ID={}, 售后单ID={}, 请求体={}",
                shopId, aftersalesId, dto);
        ServiceOrderBo bo = serviceOrderService.createServiceOrder(shopId, aftersalesId, dto);
        return ResponseEntity.ok( bo.getServiceSn());
    }
}