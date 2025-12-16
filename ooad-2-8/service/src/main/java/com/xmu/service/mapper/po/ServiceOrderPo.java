package com.xmu.service.mapper.po;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务单数据持久化对象（与数据库表对应）
 * 注：@Data 自动生成getter/setter、toString、equals、hashCode方法；
 * @NoArgsConstructor 生成无参构造器；@AllArgsConstructor 生成全参构造器，满足数据持久化场景下的对象创建需求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderPo {
    // 服务单ID（主键）
    private Long id;
    // 服务单号（业务唯一标识）
    private String serviceSn;
    // 联系人（对应API请求中的name）
    private String serviceConsignee;
    // 联系电话（对应API请求中的mobile）
    private String serviceMobile;
    // 服务详细地址
    private String address;
    // 服务类型：0上门、1寄件、2线下（与API参数type对应）
    private Integer type;
    // 服务单状态：0待处理、1处理中、2已完成（默认初始化0）
    private Integer status;
    // 服务单创建时间（自动填充，无需手动赋值）
    private Date createTime;
    // 问题图片URL（非必传，API请求中的problemImageUrl）
    private String problemImageUrl;
    // 问题描述（非必传，API请求中的description）
    private String description;
    // 产品ID（关联商品表）
    private Long productId;
    // 快递ID（寄件类型时必传，关联快递表）
    private Long expressId;
    // 客户ID（关联用户表，标识服务单归属客户）
    private Long customerId;
    // 新增：与SQL对应的关联字段（解决问题2）
    private Long shopId; // 关联API路径中的{shopId}
    private Long aftersalesId; // 关联API路径中的{id}（售后单ID）
}