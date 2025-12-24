package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
import cn.edu.xmu.oomall.aftersale.service.feign.ExpressClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
@Component
public class ReturnAndRefund extends AfterSale implements RefundInterface,CreateWayBillInterface,ConfirmProductInterface
{
    //private  final ExpressDao expressDao;       //TODO:改成openfeign调用

    // 3. Spring自动注入Feign客户端（prototype Bean的依赖会被Spring自动填充）
    @Resource
    @JsonIgnore
    private AfterSaleFeignClient aftersaleFeignClient;


    @Resource
    @JsonIgnore
    private ExpressClient expressClient;


    public ReturnAndRefund(AfterSaleDao afterSaleDao) {
        this.afterSaleDao = afterSaleDao;
        this.aftersaleFeignClient = this.afterSaleDao.afterSaleFeignClient;
        this.expressClient = this.afterSaleDao.expressClient;
    }


    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
            log.info("【ReturnAndRefund BO】开始处理退货退款类售后审核 - aftersaleId={}, confirm={}, reason={}",
                    this.getAftersaleId(), confirm, reason);

            try
            {
                ConfirmAftersale(confirm, reason);
            }
            catch (Exception e) {
                log.error("【ReturnAndRefund BO】审核同意流程失败- aftersaleId={}", this.getAftersaleId());
                return "NULL";
            }
            return aftersalePo.getReturnExpress();
    }

    @Override
    public boolean CancleAftersale(String reason) {

        log.info("【ReturnAndRefund BO】取消，准备取消寄回运单 - aftersaleId={}", this.getAftersaleId());
        setStatus((byte) 7);
        expressClient.cancleExpress(getShopId(),Long.parseLong(getReturnExpress()),reason);
        setReason(reason);
        BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
        this.afterSaleDao.saveAftersale(this.getAftersalePo());
        log.info("【ReturnAndRefund BO】状态更新为已取消，已保存到数据库 - aftersaleId={}", this.getAftersaleId());
        return true;
    }

    /**
     * 重写父类方法，设置售后单状态
     * 核心逻辑：同意审核→调用物流模块创建运单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public void ConfirmAftersale(boolean confirm, String reason)
    {
        if(confirm)
        {
            log.info("【ReturnAndRefund BO】审核同意，准备产生运单并退款 - aftersaleId={}", this.getAftersaleId());

            setStatus((byte)3);
            log.info("【ReturnAndRefund BO】已更新售后状态为商家待收货 - aftersaleId={}", this.getAftersaleId());
            //调用接口的默认方法
            refund(this);
            //创建运单
            this.setReturnExpress(createWayBill(this,expressClient));

            log.info("【ReturnAndRefund BO】审核同意处理完成 - aftersaleId={}",this.getAftersaleId());
        }
        else
        {
            log.info("【ReturnAndRefund BO】审核拒绝，仅更新售后状态为已拒绝 - aftersaleId={}", this.getAftersaleId());
            setStatus((byte)2);

            this.setReturnExpress("");

            log.info("【ReturnAndRefund BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
        }

        BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
        this.afterSaleDao.saveAftersale(this.getAftersalePo());
        log.info("【ReturnAndRefund BO】已保存到数据库 - aftersaleId={}", this.getAftersaleId());
    }


    @Override
    public void confirmProduct(boolean confirm ,String reason)
    {
          if(confirm)
          {
              log.info("【ReturnAndRefund BO】确认验收售后商品，售后单状态设为已完成 - aftersaleId={}", this.getAftersaleId());
              setStatus((byte)6);
          }
          else
          {
              setStatus((byte)8);
              this.setDeliverExpress(createWayBill(this,expressClient));
              log.info("【ReturnAndRefund BO】确认验收售后商品，售后单状态设为顾客待收货，售后单添加对应的运单号 - aftersaleId={}，DeliverExpressId={}", this.getAftersaleId(), this.getDeliverExpress());
          }
          BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
          this.afterSaleDao.saveAftersale(this.getAftersalePo());
          log.info("【ReturnAndRefund BO】已保存到数据库 - aftersaleId={}", this.getAftersaleId());
    }
}
