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
    public void acceptByProvider(String serviceProviderId){
       super.acceptByProvider(serviceProviderId);
       CreateWaybill();

       log.info("【ServiceOrder】服务商接单 - serviceOrderId={}, providerId={}", this.id, serviceProviderId);

   }
    @Override
    public void doReceive(String providerId) {
        if (!STATUS_ASSIGN.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可收件");
        }
        
        this.status = STATUS_RECEIVE;
        
        log.info("【ServiceOrder】服务商收件 - serviceOrderId={}, providerId={}", this.id, providerId);
    }
    @Override
    public void finish(String workerId){
        super.finish(workerId);
        CreateWaybill();

        log.info("【ServiceOrder】服务单完成 - serviceOrderId={}, workerId={}", this.id, workerId);
    }

    @Override
    public void cancel(){
        super.cancel();
        if(this.status==STATUS_ASSIGN){
            CancelWaybill();
        }
        else{
            CreateWaybill();
        }

        log.info("【ServiceOrder】服务单取消 - serviceOrderId={}", this.id);
    }

    public  void CreateWaybill(){
       log.info("【ServiceOrder】创建运单 - serviceOrderId={}", this.id);

    }

    public  void CancelWaybill(){
        log.info("【ServiceOrder】取消运单 - serviceOrderId={}", this.id);
    }

    @Override
    public void doAppoint(String workerId, java.time.LocalDateTime time) {
        throw new BusinessException(ReturnNo.STATENOTALLOW, "配送服务不支持上门预约操作");
    }
}