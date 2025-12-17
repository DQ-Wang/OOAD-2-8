package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.mapper.AfterSaleMapper;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 售后单DAO层实现类
 * 封装JPA Mapper的调用，统一处理数据访问细节（空值、分页、异常），对外暴露业务友好的方法
 * 注：DAO层仅负责数据访问，不包含任何业务逻辑
 */
@Repository
@RefreshScope
@RequiredArgsConstructor
@Slf4j
public class AfterSaleDao {

    // 注入JPA Mapper接口（Spring自动生成代理类）
    @Autowired
    private static AfterSaleMapper aftersaleMapper;


    /**
     * 根据店铺ID + 售后单ID查询售后单（转换为BO对象）
     * 核心：PO→BO转换，供业务层直接使用BO而非PO
     * @param aftersaleId 售后单ID
     * @return 售后单BO对象（业务层使用）
     * @throws IllegalArgumentException 售后单不存在时抛出
     */
    public static AfterSale findAftersaleById(Long aftersaleId) {
        log.debug("findAftersaleById:aftersaleId={}",aftersaleId);
        // 1. 调用Mapper查询PO（带店铺ID校验）
        Optional<AfterSalePo> optionalPo = aftersaleMapper.findByAftersaleId(aftersaleId);
        log.debug("findByAftersaleId:aftersaleId={}",aftersaleId);

        AfterSalePo po = optionalPo.orElseThrow(() ->
                new IllegalArgumentException("售后单不存在:"+ ", aftersaleId=" + aftersaleId)
        );

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:aftersale.xml");
        AfterSaleFactory aftersaleFactory=new AfterSaleFactory(context);

        // 2. PO对象转换为BO对象（属性拷贝）
        AfterSale bo = aftersaleFactory.creatAfterSale(optionalPo);

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
    public AfterSalePo saveAftersale(AfterSalePo afterSalePo) {
        log.debug("saveAftersale:po={}",afterSalePo);
        return aftersaleMapper.save(afterSalePo);
    }



}