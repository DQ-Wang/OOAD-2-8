package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xmu.service.Dao.assembler.ServiceOrderBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.xmu.service.controller.dto.ServiceOrderDto;
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
     * 服务单状态常量：已接受
     */
    public static final Byte STATUS_ACCEPT = 1;

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
            put(STATUS_ACCEPT, "ACCEPT");
            put(STATUS_CANCEL, "CANCEL");
            put(STATUS_ONDOOR, "ONDOOR");
            put(STATUS_RECEIVE, "RECEIVE");
            put(STATUS_PROGRESS, "PROGRESS");
            put(STATUS_FINISH, "FINISH");
        }
    };

    /**
     * 构建器映射表：Byte -> ServiceOrderBuilder
     * 由 ServiceOrderDao 在初始化时设置
     * 包内可见，供 ServiceOrderDao 使用
     */
    public static Map<Byte, ServiceOrderBuilder> builders;

    /**
     * 初始化构建器映射表
     * @param builderList 构建器列表
     */
    public static void initBuilders(List<ServiceOrderBuilder> builderList) {
        builders = builderList.stream()
                .collect(Collectors.toMap(ServiceOrderBuilder::getType, Function.identity()));
    }

    @Getter
    @Setter
    protected Long id;



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
    protected Long expressId;



    @Getter
    @Setter
    protected Long shopId;       // 对应API路径中的{shopId}，关联商铺

    @Getter
    @Setter
    protected Long aftersalesId;

    @Getter
    @Setter
    protected Long serviceProviderId; // 服务提供商ID

    @Getter
    @Setter
    protected Long workerId; // 维修师傅ID

    @Getter
    @Setter
    protected LocalDateTime appointmentTime; // 预约上门时间


    /**
     * 根据类型创建具体服务单
     * @param shopId 店铺ID
     * @param afterSaleId 售后单ID
     * @param dto 创建服务单请求DTO
     * @return 服务单领域对象
     */
    public static ServiceOrder create(Long shopId, Long afterSaleId, ServiceOrderDto dto) {
        if (builders == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR, "构建器未初始化，请先调用 initBuilders()");
        }
        if (dto == null || dto.getType() == null) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "服务单类型不能为空");
        }
        ServiceOrderBuilder builder = builders.get(dto.getType());
        if (builder == null) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "未知的服务单类型: " + dto.getType());
        }
        return builder.createFromDto(dto, shopId, afterSaleId);
    }

    /**
     * @param serviceProviderId    接单的服务商
     */
    public void acceptByProvider(Long serviceProviderId) {
        if (!STATUS_NEW.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "当前状态不可接受");
        }
        this.serviceProviderId = serviceProviderId;
        this.status = STATUS_ACCEPT;
    }

    public void assign(Long providerId, Long workerId) {
        if (!providerId.equals(this.serviceProviderId)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, "服务商ID不匹配");
        }
        this.workerId = workerId;

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
     * @return 状态名称，如 "NEW"、"ACCEPT" 等
     */
    public String getStatusName() {
        return STATUS_NAMES.get(this.status);
    }


    public void finish(Long workerId) {
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
        
    }

    public abstract void doAppoint(Long workerId, LocalDateTime time);

    public abstract void doReceive(Long providerId);






}