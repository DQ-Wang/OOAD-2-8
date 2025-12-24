package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/21 11:19
 */
@Slf4j
public class DeliveryServiceOrder extends ServiceOrder {
   @Override
    public void acceptByProvider(Long serviceProviderId){
       super.acceptByProvider(serviceProviderId);
       CreateWaybill();
       log.info("【ServiceOrder】服务商接单 - serviceOrderId={}, providerId={}", this.id, serviceProviderId);

   }

   @Override
    public void assign(Long providerId, Long workerId){
        if (!providerId.equals(this.serviceProviderId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "服务商ID不匹配");
        }
        if (!STATUS_ASSIGN.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可指派");
        }
        this.workerId = workerId;
        log.info("【ServiceOrder】服务单分配工人 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void doReceive(Long providerId) {
       if (!this.serviceProviderId.equals(providerId)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, "服务商ID与服务单服务商ID不一致");
        }
        if (!STATUS_ASSIGN.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可收件");
        }
        this.status = STATUS_RECEIVE;
        log.info("【ServiceOrder】服务商收件 - serviceOrderId={}, providerId={}", this.id, providerId);
    }
    @Override
    public void finish(Long workerId){
        if (!STATUS_RECEIVE.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可完成");
        }
        if (!this.workerId.equals(workerId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "无权限操作该服务单");
        }
        this.status = STATUS_FINISH;
        CreateWaybill();
        log.info("【ServiceOrder】服务单完成 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void cancel(){
        super.cancel();
        Byte prevStatus = this.status;
        
        if(STATUS_ASSIGN.equals(prevStatus)){
            CancelWaybill();
        }
        else if(STATUS_RECEIVE.equals(prevStatus)){
            CreateWaybill();
        }
        this.status = STATUS_CANCEL;

        log.info("【ServiceOrder】服务单取消 - serviceOrderId={}", this.id);
    }

    public  void CreateWaybill(){
       log.info("【ServiceOrder】创建运单 - serviceOrderId={}", this.id);

    }

    public  void CancelWaybill(){
        log.info("【ServiceOrder】取消运单 - serviceOrderId={}", this.id);
    }

    @Override
    public void doAppoint(Long workerId, java.time.LocalDateTime time) {
        throw new BusinessException(ReturnNo.STATENOTALLOW, "配送服务不支持上门预约操作");
    }
}