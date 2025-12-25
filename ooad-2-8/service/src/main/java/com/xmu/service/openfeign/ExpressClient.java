package com.xmu.service.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 货运模块 Feign 客户端
 * 用于调用外部 express 模块的接口
 */
@FeignClient("express-service")
public interface ExpressClient {
    /**
     * 创建寄件运单（顾客寄给服务商）
     * @param serviceOrderId 服务单ID
     * @return 运单ID
     */
    @PostMapping("/serviceorders/{id}/waybills/send")
    Long createSendWaybill(@PathVariable("id") Long serviceOrderId);

    /**
     * 创建退货运单（服务商寄回给顾客）
     * @param serviceOrderId 服务单ID
     * @return 运单ID
     */
    @PostMapping("/serviceorders/{id}/waybills/return")
    Long createReturnWaybill(@PathVariable("id") Long serviceOrderId);

    /**
     * 取消运单
     * @param waybillId 运单ID
     */
    @PutMapping("/serviceorders/{id}/waybills/cancel")
    void cancelWaybill(@PathVariable("id") Long waybillId);
}
