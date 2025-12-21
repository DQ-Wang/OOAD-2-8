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
 * 快递单持久化对象（对应express表）
 */
@Entity
@Table(name = "express") // 映射数据库表名：express
@Getter
@Setter
public class ExpressPo {

    /**
     * 快递单主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键策略（适配MySQL等数据库）
    private Long id; // 对应表中id字段

    /**
     * 快递单号（物流单号）
     */
    @Column(name = "bill_num", length = 50) // 对应表中bill_num字段（varchar(50)）
    private String billNum;
}