package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xmu.service.openfeign.ExpressClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/21 11:19
 */
@Slf4j
public class DeliveryServiceOrder extends ServiceOrder {
    @JsonIgnore
    private ExpressClient expressClient;

    /**
     * 设置 ExpressClient
     */
    public void setExpressClient(ExpressClient expressClient) {
        this.expressClient = expressClient;
    }
   @Override
    public void acceptByProvider(Long serviceProviderId){
       super.acceptByProvider(serviceProviderId);
       // 服务商接受服务单后，生成顾客寄件运单
       if (expressClient != null) {
           Long waybillId = expressClient.createSendWaybill(this.id);
           this.expressId = waybillId;
       }

   }


    @Override
    public void doReceive(Long providerId) {
       if (!this.serviceProviderId.equals(providerId)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, "服务商ID与服务单服务商ID不一致");
        }
        if (!STATUS_ACCEPT.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可收件");
        }
        this.status = STATUS_RECEIVE;
        log.info("【ServiceOrder】服务商收件 - serviceOrderId={}, providerId={}", this.id, providerId);
    }

    @Override
    public void assign(Long providerId, Long workerId){
        super.assign(providerId, workerId);
        if (!STATUS_RECEIVE.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可派工");
        }
        this.status=STATUS_PROGRESS;
        log.info("【ServiceOrder】服务单分配工人 - serviceOrderId={}, workerId={}", this.id, workerId);
    }
    
    @Override
    public void finish(Long workerId){
        super.finish(workerId);
        // 维修工人完成后，生成反件运单
        if (expressClient != null) {
            Long waybillId = expressClient.createReturnWaybill(this.id);
            this.expressId = waybillId;
        }

    }

    @Override
    public void cancel(){
        super.cancel();
        Byte prevStatus = this.status;

        
        if (expressClient != null) {
            if(STATUS_ACCEPT.equals(prevStatus)){
                // 在服务商接受服务单之后取消，取消生成的寄件运单
                    expressClient.cancelWaybill(this.expressId);

            }
            else if(STATUS_RECEIVE.equals(prevStatus)||STATUS_PROGRESS.equals(prevStatus)){
                // 在 RECEIVE 之后取消，生成反件运单
                Long waybillId = expressClient.createReturnWaybill(this.id);
                this.expressId = waybillId;
            }
        }
        this.status = STATUS_CANCEL;

     
    }

    @Override
    public void doAppoint(Long workerId, java.time.LocalDateTime time) {
        throw new BusinessException(ReturnNo.STATENOTALLOW, "配送服务不支持上门预约操作");
    }
}