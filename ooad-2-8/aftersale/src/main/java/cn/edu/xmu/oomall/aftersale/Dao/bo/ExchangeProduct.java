package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateServiceOrderDto;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;



@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class ExchangeProduct extends AfterSale{
    

    // 3. Spring自动注入Feign客户端（prototype Bean的依赖会被Spring自动填充）
    @Resource
    @JsonIgnore
    private ServiceOrderFeignClient serviceOrderFeignClient;


    public ExchangeProduct(AfterSaleDao afterSaleDao) {
        this.afterSaleDao = afterSaleDao;
        this.serviceOrderFeignClient = this.afterSaleDao.serviceOrderFeignClient;
    }


    /**
     * 重写父类抽象方法（纯虚函数）：实现换货类售后审核逻辑
     * 核心逻辑：同意审核→调用服务模块创建服务单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
        String result;
        log.info("【ExchangeProduct BO】开始处理换货类售后审核 - aftersaleId={}, confirm={}, reason={}",
                this.getAftersaleId(), confirm, reason);
        log.debug("HandleAftersale:aftersaleId={}",this.getAftersaleId());

        // 1. 审核拒绝：仅更新状态，无额外逻辑
        if (!confirm) {
            log.info("【ExchangeProduct BO】审核拒绝，仅更新售后状态 - aftersaleId={}", this.getAftersaleId());
            super.ConfirmAftersale(false, reason); // 调用父类普通虚方法更新状态
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            log.info("【ExchangeProduct BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
            result="NULL";
        }
        else{
        // 2. 审核同意：调用运单模块创建运单
        log.info("【ExchangeProduct BO】审核同意，准备调用运单模块创建运单 - aftersaleId={}", this.getAftersaleId());
        try {
            // 构造创建服务单的参数（适配Feign客户端入参）
            Long shopId = getShopId();
            Long aftersaleId = getAftersaleId();
            log.info("【ExchangeProduct BO】准备Feign调用参数 - shopId={}, aftersaleId={}, customerId={}, productId={}",
                    shopId, aftersaleId, this.getCustomerId(), this.getProductId());

            // 调用Feign客户端创建服务单（返回服务单ID）
            CreateServiceOrderDto createServiceOrderDto = new CreateServiceOrderDto();
            createServiceOrderDto.setCustomerId(this.getCustomerId());
            createServiceOrderDto.setProductId(this.getProductId());
            createServiceOrderDto.setAddress(this.getAddress());
            createServiceOrderDto.setMobile(this.getMobile());
            log.info("【ExchangeProduct BO】Feign调用参数构造完成 - DTO={}", createServiceOrderDto);

            log.info("【ExchangeProduct BO】开始Feign调用服务订单模块 - URL将通过service.order.base-url配置, shopId={}, aftersaleId={}",
                    shopId, aftersaleId);
            ResponseEntity<String> serviceId = afterSaleDao.serviceOrderFeignClient.createServiceOrder(shopId, aftersaleId, createServiceOrderDto);

            String serviceOrderSn = serviceId.getBody();
            log.info("【ExchangeProduct BO】Feign调用成功，收到服务单号 - aftersaleId={}, serviceOrderSn={}",
                    aftersaleId, serviceOrderSn);
            log.debug("服务单号: {}", serviceOrderSn);

            // 3. 更新维修类专属属性+售后状态
            this.setServiceOrderId(serviceOrderSn); // 绑定服务单ID
            log.info("【ExchangeProduct BO】已绑定服务单号到售后单 - aftersaleId={}, serviceOrderId={}",
                    this.getAftersaleId(), serviceOrderSn);

            super.ConfirmAftersale(true, reason); // 调用父类方法更新状态
            log.info("【ExchangeProduct BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());

            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            log.info("【ExchangeProduct BO】审核同意处理完成，已保存到数据库 - aftersaleId={}, serviceOrderSn={}",
                    this.getAftersaleId(), serviceOrderSn);

            result=serviceOrderSn;
        } catch (Exception e) {
            log.error("【ExchangeProduct BO】Feign调用服务订单模块异常 - aftersaleId={}, 异常类型={}, 异常信息={}",
                    this.getAftersaleId(), e.getClass().getName(), e.getMessage(), e);
            log.error("【ExchangeProduct BO】审核同意流程失败，返回ERROR - aftersaleId={}", this.getAftersaleId());
            result="ERROR";
        }
        }
        this.afterSaleDao.saveAftersale(this.getAftersalePo());
        return result;

    }

    @Override
    public String CancleAftersale(String reason) {
        return "";
    }


}
