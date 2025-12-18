package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.javaee.core.clonefactory.CopyFrom;
import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateServiceOrderDto;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

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


    public Maintenance(AfterSaleDao afterSaleDao,ServiceOrderFeignClient serviceOrderFeignClient) {
        this.afterSaleDao = afterSaleDao;
        this.serviceOrderFeignClient = serviceOrderFeignClient;
    }



    /**
     * 重写父类抽象方法（纯虚函数）：实现维修类售后审核逻辑
     * 核心逻辑：同意审核→调用服务模块创建服务单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public boolean HandleAftersale(boolean confirm, String reason)
    {
        log.debug("HandleAftersale:aftersaleId={}",this.getAftersaleId());
        // 1. 审核拒绝：仅更新状态，无额外逻辑
        if (!confirm) {
            super.SetStatus(false, reason); // 调用父类普通虚方法更新状态
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            return true;
        }

        // 2. 审核同意：调用服务模块创建服务单
        try {
            // 构造创建服务单的参数（适配Feign客户端入参）
            Long shopId = getShopId();
            Long aftersaleId = getAftersaleId();
            // 调用Feign客户端创建服务单（返回服务单ID）
            CreateServiceOrderDto createServiceOrderDto = new CreateServiceOrderDto();
            createServiceOrderDto.setCustomerId(this.getCustomerId());
            createServiceOrderDto.setProductId(this.getProductId());
            createServiceOrderDto.setAddress(this.getAddress());
            createServiceOrderDto.setMobile(this.getMobile());

            ResponseEntity<String> serviceId= serviceOrderFeignClient.createServiceOrder(shopId, aftersaleId, createServiceOrderDto);
            // 3. 更新维修类专属属性+售后状态
            log.debug(serviceId.getBody());
            this.setServiceOrderId(serviceId.getBody()); // 绑定服务单ID
            super.SetStatus(true, reason); // 调用父类方法更新状态
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());

            return true;
        } catch (Exception e) {

            return false;
        }

    }


}