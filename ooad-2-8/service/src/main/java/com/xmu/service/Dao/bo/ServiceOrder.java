package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import com.xmu.service.Dao.ServiceOrderDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


/**
 * 抽象服务单
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ServiceOrder  implements Serializable {

    /**
     * 服务单类型：上门服务
     */
    public static final String TYPE_ON_SITE = "ONSITE";

    /**
     * 服务单类型：寄件服务
     */
    public static final String TYPE_DELIVERY = "DELIVERY";

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String serviceSn;      //服务单编号，唯一

    @Getter
    @Setter
    private String serviceConsignee;  //联系人

    @Getter
    @Setter
    private String serviceMobile;    //联系电话

    @Getter
    @Setter
    private String address;            //地址

    @Getter
    @Setter
    private String type;              //服务单类型：0-上门服务，1-寄件服务

    @Getter
    @Setter
    private String status;            //服务单状态

    @Getter
    @Setter
    private Date createTime;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Long productId;

    @Getter
    @Setter
    private Long expressId;

    @Getter
    @Setter
    private Long customerId;

    @Getter
    @Setter
    private Long shopId;       // 对应API路径中的{shopId}，关联商铺

    @Getter
    @Setter
    private Long aftersalesId;

    @Getter
    @Setter
    protected Long servicproviderId; // 服务提供商ID

    @Getter
    @Setter
    protected Long workerId; // 维修师傅ID

    /**
     * 持久化访问对象（Dao），由 Dao 在 build 时注入
     */
    @JsonIgnore
    @ToString.Exclude
    @Setter
    protected transient ServiceOrderDao serviceOrderDao;


    /**
     * @param serviceProviderId    接单的服务商
     */
    void acceptByProvider(Long serviceProviderId) {
        if (!"NEW".equals(this.status)) {
            throw new BusinessException("当前状态不可接受");
        }
        this.servicproviderId = serviceProviderId;
        this.status = "ASSIGN";
        serviceOrderDao.update(this);
        log.info("【ServiceOrder】服务商接单 - serviceOrderId={}, providerId={}", this.id, serviceProviderId);
    }





    /**
     * 校验必填信息（业务规则封装）
     */
    public void validate() {
        // 1. 校验关联字段（shopId、aftersalesId）
        if (this.shopId == null || this.shopId <= 0) {
            throw new IllegalArgumentException("关联商铺ID无效（不可为空或负数），无法创建服务单");
        }
        if (this.aftersalesId == null || this.aftersalesId <= 0) {
            throw new IllegalArgumentException("关联售后单ID无效（不可为空或负数），无法创建服务单");
        }

    }




}