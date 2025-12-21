package cn.edu.xmu.oomall.mapper.po;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 售后单PO类：映射数据库中售后单表（aftersale）
 * 字段与业务层Aftersale BO类一一对应，支持PO↔BO的属性拷贝
 * 核心字段覆盖所有售后类型的通用属性，适配维修/仅退款/退货退款/换货场景
 */
@Entity
@Table(name = "Express") // 数据库表名（建议按模块+功能命名，避免冲突）
@Data // 自动生成get/set/equals/hashCode/toString
@NoArgsConstructor // 无参构造（JPA必需）
@AllArgsConstructor // 全参构造（方便测试/批量赋值）
@ToString // 生成toString，包含所有字段
public class ExpressPo
{
    /**
     * 售后单ID（主键，自增）
     * 对应BO类的aftersaleId字段
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增（适配MySQL）
    private String expressId;
}
