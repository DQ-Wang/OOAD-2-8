package com.xmu.service.Dao.bo;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/21 11:19
 */
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.bo.ServiceOrder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;


@Slf4j
@ToString(callSuper = true, doNotUseGetters = true)
public class OnSiteServiceOrder extends ServiceOrder {
    @Override
    public void acceptByProvider(String serviceProviderId){
        super.acceptByProvider(serviceProviderId);

        log.info("【ServiceOrder】服务商接单 - serviceOrderId={}, providerId={}", this.id, serviceProviderId);

    }

    @Override
    public void finish(String workerId){
        super.finish(workerId);

        log.info("【ServiceOrder】服务单完成 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void cancel(){
        super.cancel();

        log.info("【ServiceOrder】服务单取消 - serviceOrderId={}", this.id);
    }

    /** 上门预约 */
    @Override
    public void doAppoint(String workerId, LocalDateTime time) {
        if (!STATUS_ASSIGN.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可预约");
        }
        this.appointmentTime = time;
        this.status = STATUS_ONDOOR;
        log.info("【ServiceOrder】上门预约 - serviceOrderId={}, workerId={}, appointmentTime={}", this.id, workerId, time);
    }

    @Override
    public void doReceive(String providerId) {
        throw new BusinessException(ReturnNo.STATENOTALLOW, "上门服务不支持收件操作");
    }
}