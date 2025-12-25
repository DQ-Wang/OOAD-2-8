package com.xmu.service.Dao.bo;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/21 11:19
 */
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;


@Slf4j
@ToString(callSuper = true, doNotUseGetters = true)
public class OnSiteServiceOrder extends ServiceOrder {
    @Override
    public void acceptByProvider(Long serviceProviderId){
        super.acceptByProvider(serviceProviderId);

        log.info("【ServiceOrder】服务商接单 - serviceOrderId={}, providerId={}", this.id, serviceProviderId);

    }

    @Override
    public void assign(Long providerId, Long workerId){
        super.assign(providerId,workerId);
        if (!STATUS_ACCEPT.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可派工");
        }
        this.status=STATUS_ONDOOR;
        log.info("【ServiceOrder】服务单分配工人 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void finish(Long workerId){
        super.finish(workerId);

        log.info("【ServiceOrder】服务单完成 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void cancel(){
        super.cancel();
        this.status = STATUS_CANCEL;

        log.info("【ServiceOrder】服务单取消 - serviceOrderId={}", this.id);
    }

    /** 上门预约 */
    @Override
    public void doAppoint(Long workerId, LocalDateTime time) {
        if (!this.workerId.equals(workerId)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, "工人ID与服务单工人ID不一致");
        }
        if (!STATUS_ONDOOR.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可预约");
        }
        this.appointmentTime = time;
        this.status = STATUS_PROGRESS;
        log.info("【ServiceOrder】上门预约 - serviceOrderId={}, workerId={}, appointmentTime={}", this.id, workerId, time);
    }

    @Override
    public void doReceive(Long providerId) {
        throw new BusinessException(ReturnNo.STATENOTALLOW, "上门服务不支持收件操作");
    }
}