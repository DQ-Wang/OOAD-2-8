package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.Dao.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.mapper.AftersaleMapper;
import cn.edu.xmu.oomall.aftersale.mapper.po.AftersalePo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 售后单DAO层实现类
 * 封装JPA Mapper的调用，统一处理数据访问细节（空值、分页、异常），对外暴露业务友好的方法
 * 注：DAO层仅负责数据访问，不包含任何业务逻辑
 */
@Repository
public class AftersaleDao {

    // 注入JPA Mapper接口（Spring自动生成代理类）
    @Autowired
    private AftersaleMapper aftersaleMapper;


    /**
     * 根据店铺ID + 售后单ID查询售后单（转换为BO对象）
     * 核心：PO→BO转换，供业务层直接使用BO而非PO
     * @param shopId 店铺ID（防止跨店铺查询，数据安全）
     * @param aftersaleId 售后单ID
     * @return 售后单BO对象（业务层使用）
     * @throws IllegalArgumentException 售后单不存在时抛出
     */
    public Aftersale findAfterSaleById(Long shopId, Long aftersaleId) {
        // 1. 调用Mapper查询PO（带店铺ID校验）
        Optional<AftersalePo> optionalPo = aftersaleMapper.findByShopIdAndId(shopId, aftersaleId);
        AftersalePo po = optionalPo.orElseThrow(() ->
                new IllegalArgumentException("售后单不存在：shopId=" + shopId + ", aftersaleId=" + aftersaleId)
        );

        // 2. PO对象转换为BO对象（属性拷贝）
        Aftersale bo = new Aftersale() {
            // 必须实现抽象方法HandleAftersale
            @Override
            public boolean HandleAftersale(boolean isAgree, String reason) {
                // 这里可以写临时逻辑（或留空，根据业务需求补充）
                return isAgree;
            }
        }; // 匿名实现抽象类（或用具体子类，如Maintenance/RefundOnly）
        BeanUtils.copyProperties(po, bo); // 拷贝同名属性（驼峰命名需一致）

        // 3. 补充PO中无但BO需要的属性（若有）
        // 示例：若BO的serviceOrderId在PO中无，需额外查询（此处仅演示）
        // bo.setServiceOrderId(getServiceOrderIdByAftersaleId(aftersaleId));

        return bo;
    }



    /**
     * 新增/更新售后单（JPA的save方法：主键存在则更新，不存在则新增）
     * @param afterSalePo 售后单PO对象
     * @return 保存后的售后单PO（含自增ID）
     */
    public AftersalePo saveAftersale(AftersalePo afterSalePo) {
        return aftersaleMapper.save(afterSalePo);
    }

    /**
     * 根据售后单ID删除售后单（物理删除，若需逻辑删除可修改status字段）
     * @param aftersaleId 售后单ID
     */
    public void deleteAftersale(Long aftersaleId) {
        aftersaleMapper.deleteById(aftersaleId);
    }


}