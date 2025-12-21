package cn.edu.xmu.oomall.aftersale.service.feign;

import cn.edu.xmu.oomall.aftersale.Dao.bo.Maintenance;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateServiceOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 服务订单模块Feign客户端
 * 售后模块（尤其是维修类售后）通过该客户端调用服务订单模块的接口，创建/查询服务单
 * 注：需确保服务订单模块已提供对应接口，且服务名（service-order）与注册中心一致
 */
// value：服务订单模块的服务名（需与Nacos/Eureka等注册中心的服务名一致）



@FeignClient(
        value = "service-order",
        // 通过配置指定服务地址，本地和云上可分别配置
        url = "${service.order.base-url}"
)
public interface ServiceOrderFeignClient {

    /**
     * 创建维修服务单（核心方法：维修类售后审核同意时调用）
     * 适配场景：售后模块→服务订单模块，创建与售后单关联的维修服务单
     * @param shopId 店铺ID（必填，服务单归属店铺）
     * @param aftersaleId 售后单ID（必填，关联售后单，对应服务端路径的{id}）
     * @param createServiceOrderDto 维修类售后BO（包含服务类型、技师等专属信息）
     * @return 服务单ID（创建成功后返回自增主键）
     */
    // 服务端路径
    @PostMapping("/internal/shops/{shopId}/aftersales/{id}/serviceorders")
    ResponseEntity<String> createServiceOrder(
            // 路径占位符{shopId} → @PathVariable("shopId") 绑定
            @PathVariable("shopId") Long shopId,
            // 路径占位符{id} → @PathVariable("id") 绑定（参数名可仍为aftersaleId，注解内指定"id"即可）
            @PathVariable("id") Long aftersaleId,
            // 请求体参数
            @RequestBody CreateServiceOrderDto createServiceOrderDto
    );


    @PostMapping("/internal/shops/{shopId}/packages")
    ResponseEntity<String> createExpress(
            // 路径占位符{shopId} → @PathVariable("shopId") 绑定
            @PathVariable("shopId") Long shopId,
            // 请求体参数
            @RequestBody CreateServiceOrderDto createServiceOrderDto
    );


    @PostMapping("/internal/shops/{shopId}/packages/{id}/cancel")
    ResponseEntity<String> cancleExpress(
            // 路径占位符{shopId} → @PathVariable("shopId") 绑定
            @PathVariable("shopId") Long shopId,
            // 路径占位符{id} → @PathVariable("id") 绑定（参数名可仍为expressId，注解内指定"id"即可）
            @PathVariable("id") Long expressId,
            // 请求体参数
            @RequestBody CreateServiceOrderDto createServiceOrderDto
    );


}