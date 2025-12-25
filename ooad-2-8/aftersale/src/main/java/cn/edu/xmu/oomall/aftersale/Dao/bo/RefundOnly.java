package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.service.feign.PaymentClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class RefundOnly extends AfterSale implements RefundInterface{


    public RefundOnly(AfterSaleDao afterSaleDao) {
        this.afterSaleDao = afterSaleDao;
        this.paymentClient = afterSaleDao.paymentClient;
    }

    @Resource
    @JsonIgnore
    private PaymentClient paymentClient;

    @Override
    public String HandleAftersale(boolean confirm, String reason)
    {
        log.info("【Refund BO】开始处理仅退款类售后审核 - aftersaleId={}, confirm={}, reason={}",
                this.getAftersaleId(), confirm, reason);
        log.debug("HandleAftersale:aftersaleId={}", this.getAftersaleId());

        // 1. 审核拒绝：仅更新状态，无额外逻辑
        if (!confirm) {
            log.info("【Refund BO】审核拒绝，仅更新售后状态 - aftersaleId={}", this.getAftersaleId());
            ConfirmAftersale(false, reason); // 调用父类普通虚方法更新状态
            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【Refund BO】审核拒绝处理完成 - aftersaleId={}", this.getAftersaleId());
            return "NULL";
        }

        // 2. 审核同意：日志打印退款信息
        log.info("【Refund BO】审核同意，准备退款 - aftersaleId={}", this.getAftersaleId());
        try {
            ConfirmAftersale(true, reason); // 调用父类方法更新状态
            log.info("【Refund BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());

            BeanUtils.copyProperties(this, this.aftersalePo); // 拷贝同名属性（驼峰命名需一致）
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【Refund BO】审核同意处理完成，已保存到数据库 - aftersaleId={}",
                    this.getAftersaleId());

            //调用接口的默认方法
            refund(this,paymentClient);

            return "NULL";
        } catch (Exception e) {
            log.error("【Refund BO】审核同意流程失败- aftersaleId={}", this.getAftersaleId());
            return "NULL";
        }
    }

    @Override
    public boolean CancleAftersale(String reason) {
        try
        {        throw new IllegalArgumentException("仅退款类型不能取消！");}
        catch (Exception e)
        {
            log.error("仅退款类型触发了取消售后逻辑,属于错误调用，请检查代码逻辑是否有误");
        }

        return false;
    }

    @Override
    public void ConfirmAftersale(boolean confirm, String reason) {
        if(confirm)
        {
            setStatus((byte)6); //设为已完成
            this.aftersalePo.setStatus((byte)6);
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【ReturnONly BO】已更新售后状态为已同意 - aftersaleId={}", this.getAftersaleId());
        }
        else
        {
            setStatus((byte)2); //设为已拒绝
            this.aftersalePo.setStatus((byte)2);
            this.afterSaleDao.saveAftersale(this.getAftersalePo());
            log.info("【ReturnONly BO】已更新售后状态为已拒绝 - aftersaleId={}", this.getAftersaleId());
        }
    }
}
