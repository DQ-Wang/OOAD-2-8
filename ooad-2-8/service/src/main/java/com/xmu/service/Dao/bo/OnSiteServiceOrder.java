package com.xmu.service.Dao.bo;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/21 11:19
 */
import com.xmu.service.Dao.bo.ServiceOrder;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true, doNotUseGetters = true)
public class OnSiteServiceOrder extends ServiceOrder {

    /** 上门预约 */
    public void appointment(LocalDateTime time) {

    }


}