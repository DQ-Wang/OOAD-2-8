package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.ServiceOrderDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.Dao.factory.ServiceOrderFactory;
/**
 * 抽象服务单
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ServiceOrder  implements Serializable {

    /**
     * 服务单类型常量：上门服务
     */
    public static final Byte TYPE_ON_SITE = 0;

    /**
     * 服务单类型常量：寄件服务
     */
    public static final Byte TYPE_DELIVERY = 1;

    /**
     * 服务单状态常量：新建
     */
    public static final Byte STATUS_NEW = 0;

    /**
     * 服务单状态常量：已分配
     */
    public static final Byte STATUS_ASSIGN = 1;

    /**
     * 服务单状态常量：已取消
     */
    public static final Byte STATUS_CANCEL = 2;

    /**
     * 服务单状态常量：上门中
     */
    public static final Byte STATUS_ONDOOR = 3;

    /**
     * 服务单状态常量：已接收
     */
    public static final Byte STATUS_RECEIVE = 4;

    /**
     * 服务单状态常量：进行中
     */
    public static final Byte STATUS_PROGRESS = 5;

    /**
     * 服务单状态常量：已完成
     */
    public static final Byte STATUS_FINISH = 6;

    /**
     * 类型名称映射：Byte -> String
     */
    public static final Map<Byte, String> TYPE_NAMES = new HashMap<>() {
        {
            put(TYPE_ON_SITE, "ONSITE");
            put(TYPE_DELIVERY, "DELIVERY");
        }
    };

    /**
     * 状态名称映射：Byte -> String
     */
    public static final Map<Byte, String> STATUS_NAMES = new HashMap<>() {
        {
            put(STATUS_NEW, "NEW");
            put(STATUS_ASSIGN, "ASSIGN");
            put(STATUS_CANCEL, "CANCEL");
            put(STATUS_ONDOOR, "ONDOOR");
            put(STATUS_RECEIVE, "RECEIVE");
            put(STATUS_PROGRESS, "PROGRESS");
            put(STATUS_FINISH, "FINISH");
        }
    };

    @Getter
    @Setter
    protected String id;



    @Getter
    @Setter
    protected String serviceConsignee;  //联系人

    @Getter
    @Setter
    protected String serviceMobile;    //联系电话

    @Getter
    @Setter
    protected String address;            //地址

    @Getter
    @Setter
    protected Byte type;              //服务单类型：0-上门服务，1-寄件服务

    @Getter
    @Setter
    protected Byte status;            //服务单状态

    @Getter
    @Setter
    protected LocalDateTime createTime;

    @Getter
    @Setter
    protected String description;



    @Getter
    @Setter
    protected String expressId;



    @Getter
    @Setter
    protected String shopId;       // 对应API路径中的{shopId}，关联商铺

    @Getter
    @Setter
    protected String aftersalesId;

    @Getter
    @Setter
    protected String servicproviderId; // 服务提供商ID

    @Getter
    @Setter
    protected String workerId; // 维修师傅ID

    @Getter
    @Setter
    protected LocalDateTime appointmentTime; // 预约上门时间

    /**
     * 持久化访问对象（Dao），由 Dao 在 build 时注入
     */
    @JsonIgnore
    @ToString.Exclude
    @Setter
    protected transient ServiceOrderDao serviceOrderDao;

    /**
     * 工厂方法：根据类型创建具体服务单
     */
    public static ServiceOrder create(String shopId, String afterSaleId, ServiceOrderDto dto) {
        return ServiceOrderFactory.create(shopId, afterSaleId, dto);
    }

    /**
     * @param serviceProviderId    接单的服务商
     */
    public void acceptByProvider(String serviceProviderId) {
        if (!STATUS_NEW.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可接受");
        }
        this.servicproviderId = serviceProviderId;
        this.status = STATUS_ASSIGN;
    }

    public void assign(String providerId, String workerId) {
        if (!providerId.equals(this.servicproviderId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "服务商ID不匹配");
        }
        if (!STATUS_ASSIGN.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可指派");
        }
        this.workerId = workerId;
        this.status = STATUS_PROGRESS;
    }

    /**
     * 获取类型名称
     * @return 类型名称，如 "ONSITE"、"DELIVERY"
     */
    public String getTypeName() {
        return TYPE_NAMES.get(this.type);
    }

    /**
     * 获取状态名称
     * @return 状态名称，如 "NEW"、"ASSIGN" 等
     */
    public String getStatusName() {
        return STATUS_NAMES.get(this.status);
    }


    public void finish(String workerId) {
        if (!STATUS_PROGRESS.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可完成");
        }
        if (!this.workerId.equals(workerId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "无权限操作该服务单");
        }
        this.status = STATUS_FINISH;
    }

    public void cancel() {
        if (STATUS_FINISH.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "已完成的服务单不可取消");
        }
        this.status = STATUS_CANCEL;
    }

    public abstract void doAppoint(String workerId, LocalDateTime time);

    public abstract void doReceive(String providerId);






}