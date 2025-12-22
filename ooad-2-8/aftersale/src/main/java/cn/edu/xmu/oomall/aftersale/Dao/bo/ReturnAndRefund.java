package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class ReturnAndRefund extends AfterSale implements RefundInterface,CreateWayBillInterface
{
    //private  final ExpressDao expressDao;       //TODO:改成openfeign调用

    // 3. Spring自动注入Feign客户端（prototype Bean的依赖会被Spring自动填充）
    @Resource
    @JsonIgnore
    private AfterSaleFeignClient aftersaleFeignClient;

    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
            log.info("【ReturnAndRefund BO】开始处理仅退款类售后审核 - aftersaleId={}, confirm={}, reason={}",
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

        setStatus((byte) 7);

        return true;
    }

    /**
     * 重写父类方法，设置售后单状态
     * 核心逻辑：同意审核→调用服务模块创建服务单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public void ConfirmAftersale(boolean confirm, String reason)
    {
        if(confirm)
        {
            log.info("【ReturnAndRefund BO】审核同意，准备产生运单并退款 - aftersaleId={}", this.getAftersaleId());

            setStatus((byte)3);
            log.info("【ReturnAndRefund BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());
            //调用接口的默认方法
            refund(this);
            //创建运单
            this.setReturnExpress(createWayBill(this,aftersaleFeignClient));

            log.info("【ReturnAndRefund BO】审核同意处理完成 - aftersaleId={}",this.getAftersaleId());
        }
        else
        {
            log.info("【ReturnAndRefund BO】审核拒绝，仅更新售后状态 - aftersaleId={}", this.getAftersaleId());
            setStatus((byte)2);

            this.setReturnExpress("");

            log.info("【ReturnAndRefund BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
        }

        BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
        this.afterSaleDao.saveAftersale(this.getAftersalePo());
        log.info("【ReturnAndRefund BO】已保存到数据库 - aftersaleId={}", this.getAftersaleId());
    }


}
