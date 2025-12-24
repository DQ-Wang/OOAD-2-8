package com.xmu.service.mapper.po;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务单持久化对象
 */
@Entity
@Table(name = "service_order2")
@Getter
@Setter
public class ServiceOrderPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键策略
    private Long id;



    /**
     * 收货人
     */
    @Column(name = "service_consignee", length = 50)
    private String serviceConsignee;

    /**
     * 联系电话
     */
    @Column(name = "service_mobile", length = 20)
    private String serviceMobile;

    /**
     * 地址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 服务类型：如 ONSITE、DELIVERY
     */
    @Column(name = "type")
    private Byte type;

    /**
     * 状态
     */
    @Column(name = "status")
    private Byte status;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 快递单号
     */
    @Column(name = "express_id")
    private Long expressId;

    /**
     * 所属员工
     */
    @Column(name = "worker_id")
    private Long workerId;

    /**
     * 服务提供商ID
     */
    @Column(name = "service_provider_id")
    private Long serviceProviderId;

    /**
     * 预约上门时间
     */
    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @Column(name = "aftersales_id")
    private Long aftersalesId;
    /**
     * 所属商铺ID
     */
    @Column(name = "shop_id")
    private Long shopId;       // 对应API路径中的{shopId}，关联商铺
}


