package cn.edu.xmu.oomall.aftersale.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 售后单PO类：映射数据库中售后单表（aftersale）
 * 字段与业务层Aftersale BO类一一对应，支持PO↔BO的属性拷贝
 * 核心字段覆盖所有售后类型的通用属性，适配维修/仅退款/退货退款/换货场景
 */
@Entity
@Table(name = "aftersale") // 数据库表名（建议按模块+功能命名，避免冲突）
@Data // 自动生成get/set/equals/hashCode/toString
@NoArgsConstructor // 无参构造（JPA必需）
@AllArgsConstructor // 全参构造（方便测试/批量赋值）
@ToString // 生成toString，包含所有字段
public class AfterSalePo {

    /**
     * 售后单ID（主键，自增）
     * 对应BO类的aftersaleId字段
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增（适配MySQL）
    private Long aftersaleId;

    /**
     * 店铺ID
     * 关联店铺表，防止跨店铺查询售后单
     */
    private Long shopId;

    /**
     * 订单ID
     * 关联订单表，售后归属的订单
     */
    private Long orderId;

    /**
     * 商品ID
     * 关联商品表，售后对应的具体商品
     */
    private Long productId;

    /**
     * 顾客ID
     * 关联用户表，售后发起的顾客
     */
    private Long customerId;

    /**
     * 售后类型
     * 1=维修 2=仅退款 3=退货退款 4=换货
     */
    private Byte type;

    /**
     * 售后状态
     * 0=待审核 1=已同意 2=已拒绝
     */
    private Byte status;

    /**
     * 售后原因
     * 顾客填写的售后理由/审核人员的拒绝理由
     */
    private String reason;


    /**
     * 顾客手机号
     * 售后联系电话
     */
    private String mobile;

    /**
     * 售后详细地址
     * 结合regionId组成完整地址（省+市+区+详细地址）
     */
    private String address;

    /**
     * 售后商品数量
     * 申请售后的商品数量
     */
    private Integer quantity;

    /**
     * 服务单ID（维修类售后专属）
     * 关联服务订单模块的服务单，创建服务单后赋值
     */
    private String serviceOrderId;

    //退货运单号
    private String returnExpress;
    //发货运单号
    private String deliverExpress;


}