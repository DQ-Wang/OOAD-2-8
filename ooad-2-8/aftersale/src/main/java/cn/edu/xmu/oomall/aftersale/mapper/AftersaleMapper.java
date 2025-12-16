package cn.edu.xmu.oomall.aftersale.mapper;

import cn.edu.xmu.oomall.aftersale.mapper.po.AftersalePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 售后单数据访问接口（Spring Data JPA）
 * 基于方法名自动生成SQL，无需手写，支持售后单的CRUD、多条件查询、分页/排序等
 * 适配业务场景：按店铺/类型/状态/订单ID/顾客ID等维度查询售后单
 */
@Repository
public interface AftersaleMapper extends JpaRepository<AftersalePo, Long> {

    // ===================== 基础多条件查询 =====================
    /**
     * 按店铺ID + 售后状态查询售后单（支持分页）
     * 自动生成SQL：SELECT * FROM aftersale WHERE shop_id = ? AND status = ? LIMIT ? OFFSET ?
     * @param shopId 店铺ID
     * @param status 售后状态：0=待审核 1=已同意 2=已拒绝
     * @param pageable 分页参数（页码、每页条数、排序规则）
     * @return 分页后的售后单列表
     */
    Page<AftersalePo> findByShopIdAndStatus(Long shopId, Byte status, Pageable pageable);

    /**
     * 按店铺ID + 售后类型 + 状态查询售后单（核心：区分维修/仅退款等类型）
     * 自动生成SQL：SELECT * FROM aftersale WHERE shop_id = ? AND type = ? AND status = ?
     * @param shopId 店铺ID
     * @param type 售后类型：1=维修 2=仅退款 3=退货退款 4=换货
     * @param status 售后状态
     * @return 符合条件的售后单列表
     */
    List<AftersalePo> findByShopIdAndTypeAndStatus(Long shopId, Byte type, Byte status);

    // ===================== 关联订单/顾客查询 =====================
    /**
     * 按订单ID查询售后单（售后关联订单）
     * 自动生成SQL：SELECT * FROM aftersale WHERE order_id = ?
     * @param orderId 订单ID
     * @return 该订单下的所有售后单
     */
    List<AftersalePo> findByOrderId(Long orderId);

    /**
     * 按顾客ID + 售后类型分页查询售后单
     * 自动生成SQL：SELECT * FROM aftersale WHERE customer_id = ? AND type = ? LIMIT ? OFFSET ?
     * @param customerId 顾客ID
     * @param type 售后类型
     * @param pageable 分页参数
     * @return 分页后的售后单列表
     */
    Page<AftersalePo> findByCustomerIdAndType(Long customerId, Byte type, Pageable pageable);

    // ===================== 统计/存在性判断 =====================
    /**
     * 统计某店铺下某类型的售后单数量
     * 自动生成SQL：SELECT COUNT(*) FROM aftersale WHERE shop_id = ? AND type = ?
     * @param shopId 店铺ID
     * @param type 售后类型
     * @return 数量
     */
    long countByShopIdAndType(Long shopId, Byte type);

    /**
     * 判断某订单是否存在未处理的售后单（状态=0待审核/1已同意）
     * 自动生成SQL：SELECT EXISTS(SELECT 1 FROM aftersale WHERE order_id = ? AND status IN (?))
     * @param orderId 订单ID
     * @param statusList 状态列表（0/1）
     * @return 是否存在
     */
    boolean existsByOrderIdAndStatusIn(Long orderId, List<Byte> statusList);

    // ===================== 单条售后单查询 =====================
    /**
     * 按店铺ID + 售后单ID查询（防止跨店铺查询）
     * 自动生成SQL：SELECT * FROM aftersale WHERE shop_id = ? AND id = ?
     * @param shopId 店铺ID
     * @param aftersaleId 售后单ID
     * @return 售后单PO（Optional避免空指针）
     */
    Optional<AftersalePo> findByShopIdAndId(Long shopId, Long aftersaleId);
}