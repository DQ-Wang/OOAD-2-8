package cn.edu.xmu.oomall.aftersale.mapper;

import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
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
public interface AfterSaleMapper extends JpaRepository<AfterSalePo, Long> {


    // ===================== 单条售后单查询 =====================
    /**
     * 按店铺ID + 售后单ID查询（防止跨店铺查询）
     * 自动生成SQL：SELECT * FROM aftersale WHERE shop_id = ? AND id = ?
     * @param shopId 店铺ID
     * @param aftersaleId 售后单ID
     * @return 售后单PO（Optional避免空指针）
     */
    Optional<AfterSalePo> findByShopIdAndAftersaleId(Long shopId, Long aftersaleId);



    /**
     * 按店铺ID + 售后单ID查询（防止跨店铺查询）
     * 自动生成SQL：SELECT * FROM aftersale WHERE AftersaleId = ?
     * @param aftersaleId 售后单ID
     * @return 售后单PO（Optional避免空指针）
     */
    Optional<AfterSalePo> findByaftersaleId(Long aftersaleId);


}