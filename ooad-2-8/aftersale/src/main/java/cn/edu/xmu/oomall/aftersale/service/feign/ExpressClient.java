package cn.edu.xmu.oomall.aftersale.service.feign;

import cn.edu.xmu.oomall.aftersale.controller.dto.CreateExpressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 服务订单模块Feign客户端
 * 售后模块（尤其是维修类售后）通过该客户端调用服务订单模块的接口，创建/查询服务单
 * 注：需确保服务订单模块已提供对应接口，且服务名（service-order）与注册中心一致
 */
// value：服务订单模块的服务名（需与Nacos/Eureka等注册中心的服务名一致）



@FeignClient(
        value = "express",
        // 通过配置指定服务地址，本地和云上可分别配置
        url = "${service.order.base-url}"
)
public interface ExpressClient {

    /**
     * 创建运单
     * @param shopId 门店ID（路径参数）
     * @param createExpressDto 运单创建参数（请求体）
     * @return 运单信息
     */
    @PostMapping("/internal/shops/{shopId}/packages")
    ResponseEntity<String> createExpress(
            // 路径占位符{shopId} → @PathVariable("shopId") 绑定
            @PathVariable("shopId") Long shopId,
            // 请求体参数
            @RequestBody CreateExpressDto createExpressDto
    );


    /**
     * 取消运单
     * @param shopId 门店ID（路径参数）
     * @param expressId 运单ID（路径参数）
     * @param reason 取消原因参数（请求体）
     * @return 取消结果
     */
    @PutMapping("/internal/shops/{shopId}/packages/{id}/cancel")
    ResponseEntity<String> cancleExpress(
            // 路径占位符{shopId} → @PathVariable("shopId") 绑定
            @PathVariable("shopId") Long shopId,
            // 路径占位符{id} → @PathVariable("id") 绑定（参数名可仍为expressId，注解内指定"id"即可）
            @PathVariable("id") Long expressId,
            // 请求体参数
            @RequestBody String reason
    );


}