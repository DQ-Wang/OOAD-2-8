package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateServiceOrderDto;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;

/**
 * 对应类图中的maintenance（维修类售后子类）
 * 继承抽象父类Aftersale，重写核心抽象方法实现维修专属逻辑
 * 核心特性：审核同意时调用服务模块创建服务单，关联服务单ID
 */
// 标记为Spring组件，用于扫描注册
//@Repository("maintenanceBO") // 指定beanName，方便后续通过名称获取
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j

public class Maintenance extends AfterSale {


    // 3. Spring自动注入Feign客户端（prototype Bean的依赖会被Spring自动填充）
    @Resource
    @JsonIgnore
    private ServiceOrderFeignClient serviceOrderFeignClient;


    public Maintenance(AfterSaleDao afterSaleDao) {
        this.afterSaleDao = afterSaleDao;
        this.serviceOrderFeignClient = this.afterSaleDao.serviceOrderFeignClient;
    }



    /**
     * 重写父类抽象方法（纯虚函数）：实现维修类售后审核逻辑
     * 核心逻辑：同意审核→调用服务模块创建服务单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
        log.info("【Maintenance BO】开始处理维修类售后审核 - aftersaleId={}, confirm={}, reason={}", 
                this.getAftersaleId(), confirm, reason);
        log.debug("HandleAftersale:aftersaleId={}",this.getAftersaleId());
        
        // 1. 审核拒绝：仅更新状态，无额外逻辑
        if (!confirm) {
            log.info("【Maintenance BO】审核拒绝，仅更新售后状态 - aftersaleId={}", this.getAftersaleId());
            super.SetStatus(false, reason); // 调用父类普通虚方法更新状态
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【Maintenance BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
            return "NULL";
        }

        // 2. 审核同意：调用服务模块创建服务单
        log.info("【Maintenance BO】审核同意，准备调用服务订单模块创建服务单 - aftersaleId={}", this.getAftersaleId());
        try {
            // 构造创建服务单的参数（适配Feign客户端入参）
            Long shopId = getShopId();
            Long aftersaleId = getAftersaleId();
            log.info("【Maintenance BO】准备Feign调用参数 - shopId={}, aftersaleId={}, customerId={}, productId={}", 
                    shopId, aftersaleId, this.getCustomerId(), this.getProductId());
            
            // 调用Feign客户端创建服务单（返回服务单ID）
            CreateServiceOrderDto createServiceOrderDto = new CreateServiceOrderDto();
            createServiceOrderDto.setCustomerId(this.getCustomerId());
            createServiceOrderDto.setProductId(this.getProductId());
            createServiceOrderDto.setAddress(this.getAddress());
            createServiceOrderDto.setMobile(this.getMobile());
            log.info("【Maintenance BO】Feign调用参数构造完成 - DTO={}", createServiceOrderDto);

            log.info("【Maintenance BO】开始Feign调用服务订单模块 - URL将通过service.order.base-url配置, shopId={}, aftersaleId={}", 
                    shopId, aftersaleId);
            ResponseEntity<String> serviceId = afterSaleDao.serviceOrderFeignClient.createServiceOrder(shopId, aftersaleId, createServiceOrderDto);
            
            String serviceOrderSn = serviceId.getBody();
            log.info("【Maintenance BO】Feign调用成功，收到服务单号 - aftersaleId={}, serviceOrderSn={}", 
                    aftersaleId, serviceOrderSn);
            log.debug("服务单号: {}", serviceOrderSn);
            
            // 3. 更新维修类专属属性+售后状态
            this.setServiceOrderId(serviceOrderSn); // 绑定服务单ID
            log.info("【Maintenance BO】已绑定服务单号到售后单 - aftersaleId={}, serviceOrderId={}", 
                    this.getAftersaleId(), serviceOrderSn);
            
            super.SetStatus(true, reason); // 调用父类方法更新状态
            log.info("【Maintenance BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());
            
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【Maintenance BO】审核同意处理完成，已保存到数据库 - aftersaleId={}, serviceOrderSn={}", 
                    this.getAftersaleId(), serviceOrderSn);

            return serviceOrderSn;
        } catch (Exception e) {
            log.error("【Maintenance BO】Feign调用服务订单模块异常 - aftersaleId={}, 异常类型={}, 异常信息={}", 
                    this.getAftersaleId(), e.getClass().getName(), e.getMessage(), e);
            log.error("【Maintenance BO】审核同意流程失败，返回ERROR - aftersaleId={}", this.getAftersaleId());
            return "ERROR";
        }

    }

    @Override
    public String CancleAftersale(String reason) {
        return "";
    }


}