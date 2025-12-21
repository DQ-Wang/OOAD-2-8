package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.Dao.ExpressDao;
import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import cn.edu.xmu.oomall.mapper.po.ExpressPo;
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
    private  final ExpressDao expressDao;

    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
            log.info("【ReturnAndRefund BO】开始处理仅退款类售后审核 - aftersaleId={}, confirm={}, reason={}",
                    this.getAftersaleId(), confirm, reason);
            log.debug("HandleAftersale:aftersaleId={}", this.getAftersaleId());

            // 1. 审核拒绝：仅更新状态，无额外逻辑
            if (!confirm) {
                log.info("【ReturnAndRefund BO】审核拒绝，仅更新售后状态 - aftersaleId={}", this.getAftersaleId());
                super.SetStatus(false, reason); // 调用父类普通虚方法更新状态
                BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
                this.afterSaleDao.saveAftersale(this.getAftersalePo());
                log.info("【ReturnAndRefund BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
                return "NULL";
            }

            // 2. 审核同意：日志打印退款信息并产生运单
            log.info("【ReturnAndRefund BO】审核同意，准备产生运单并退款 - aftersaleId={}", this.getAftersaleId());
            try {
                super.SetStatus(true, reason); // 调用父类方法更新状态
                log.info("【ReturnAndRefund BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());

                BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
                this.afterSaleDao.saveAftersale(this.getAftersalePo());
                log.info("【ReturnAndRefund BO】审核同意处理完成，已保存到数据库 - aftersaleId={}",
                        this.getAftersaleId());

                //调用接口的默认方法
                refund(this);
                //创建运单
                createWayBill(this);

                return "NULL";
            } catch (Exception e) {
                log.error("【ReturnAndRefund BO】审核同意流程失败- aftersaleId={}", this.getAftersaleId());
                return "NULL";
            }
    }


    @Override
    public String createWayBill(AfterSale afterSale) {
        log.info("【ReturnAndRefund BO】创建运单，已保存到数据库 - aftersaleId={}",
                this.getAftersaleId());

        ExpressPo expressPo = new ExpressPo();
        expressDao.insertExpress(expressPo);
        String ExpressId = "";
        return ExpressId;
    }
}
