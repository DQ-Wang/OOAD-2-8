package cn.edu.xmu.oomall.mapper;
import cn.edu.xmu.oomall.mapper.po.ExpressPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpressMapper extends JpaRepository<ExpressPo, String> {


    // ===================== 单条售后单查询 =====================
    /**
     * 按店铺ID + 售后单ID查询（防止跨店铺查询）
     * 自动生成SQL：SELECT * FROM aftersale WHERE shop_id = ? AND id = ?
     * @param expressId 店铺ID
     * @return 售后单PO（Optional避免空指针）
     */
    Optional<ExpressPo> findByExpressId(String expressId);

}
