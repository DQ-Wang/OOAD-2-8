package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.mapper.AftersaleMapper;
import cn.edu.xmu.oomall.aftersale.mapper.po.AftersalePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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