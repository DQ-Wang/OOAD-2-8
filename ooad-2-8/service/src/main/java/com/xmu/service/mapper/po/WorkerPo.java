package com.xmu.service.mapper.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 员工持久化对象（对应worker表）
 */
@Entity
@Table(name = "worker") // 映射数据库表名
@Getter
@Setter
public class WorkerPo {

    /**
     * 员工主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键策略
    private Long id; // 对应表中id字段

    /**
     * 员工姓名
     */
    @Column(name = "name", length = 50) // 对应表中name字段（varchar(50)）
    private String name;

    /**
     * 员工联系电话
     */
    @Column(name = "mobile", length = 20) // 对应表中mobile字段（varchar(20)）
    private String mobile;

    /**
     * 员工状态（如：1=在职，2=离职）
     */
    @Column(name = "status") // 对应表中status字段（tinyint类型）
    private Byte status;

    /**
     * 所属服务提供商ID（关联service_provider表的主键）
     */
    @Column(name = "ServiceProvider_id") // 对应表中ServiceProvider_id字段（int类型）
    private Integer serviceProviderId;
}