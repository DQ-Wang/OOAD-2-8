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
 * 服务提供商持久化对象（对应service_provider表）
 */
@Entity
@Table(name = "service_provider") // 映射数据库表名
@Getter
@Setter
public class ServiceProviderPo {

    /**
     * 服务提供商主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键策略
    private Long id; // 对应表中id字段

    /**
     * 服务提供商名称
     */
    @Column(name = "name", length = 100) // 对应表中name字段（varchar(100)）
    private String name;

    /**
     * 联系人
     */
    @Column(name = "consignee", length = 50) // 对应表中consignee字段（varchar(50)）
    private String consignee;

    /**
     * 联系电话
     */
    @Column(name = "mobile", length = 20) // 对应表中mobile字段（varchar(20)）
    private String mobile;

    /**
     * 联系地址
     */
    @Column(name = "address", length = 200) // 对应表中address字段（varchar(200)）
    private String address;

    /**
     * 联系邮箱
     */
    @Column(name = "email", length = 100) // 对应表中email字段（varchar(100)）
    private String email;
}