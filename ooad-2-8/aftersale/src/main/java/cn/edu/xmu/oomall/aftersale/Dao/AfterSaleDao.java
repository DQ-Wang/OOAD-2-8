package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.mapper.AfterSaleMapper;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
    private final AfterSaleMapper aftersaleMapper;
    private final AfterSaleFactory aftersaleFactory;
    @Autowired
    ServiceOrderFeignClient serviceOrderFeignClient;


    /**
     * 根据店铺ID + 售后单ID查询售后单（转换为BO对象）
     * 核心：PO→BO转换，供业务层直接使用BO而非PO
     * @param aftersaleId 售后单ID
     * @return 售后单BO对象（业务层使用）
     * @throws IllegalArgumentException 售后单不存在时抛出
     */
    public AfterSale findAftersaleById(Long aftersaleId) {
        log.info("【DAO层】开始查询售后单 - aftersaleId={}", aftersaleId);
        log.debug("findAftersaleById:aftersaleId={}",aftersaleId);
        
        // 1. 调用Mapper查询PO（带店铺ID校验）
        Optional<AfterSalePo> optionalPo=null;
        try {
            optionalPo = aftersaleMapper.findByaftersaleId(aftersaleId);
            log.debug("findByAftersaleId:aftersaleId={}", aftersaleId);
            if (optionalPo.isPresent()) {
                log.info("【DAO层】查询售后单成功 - aftersaleId={}, type={}, status={}", 
                        aftersaleId, optionalPo.get().getType(), optionalPo.get().getStatus());
            } else {
                log.warn("【DAO层】售后单不存在 - aftersaleId={}", aftersaleId);
            }
        }
        catch (Exception e) {
            log.error("【DAO层】查询售后单异常 - aftersaleId={}, 异常类型={}, 异常信息={}", 
                    aftersaleId, e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
        AfterSalePo po = optionalPo.orElseThrow(() -> {
            log.error("【DAO层】售后单不存在，抛出异常 - aftersaleId={}", aftersaleId);
            return new IllegalArgumentException("售后单不存在:"+ ", aftersaleId=" + aftersaleId);
        });

        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath:aftersale.xml");
        //AfterSaleFactory aftersaleFactory=new AfterSaleFactory();




        // 2. PO对象转换为BO对象（属性拷贝）
        log.debug("【DAO层】开始将PO转换为BO - aftersaleId={}", aftersaleId);
        AfterSale bo = aftersaleFactory.creatAfterSale(po,this,serviceOrderFeignClient);
        BeanUtils.copyProperties(po, bo); // 拷贝同名属性（驼峰命名需一致）
        log.info("【DAO层】PO转BO完成 - aftersaleId={}, BO类型={}", aftersaleId, bo.getClass().getSimpleName());

        return bo;
    }




    /**
     * 新增/更新售后单（JPA的save方法：主键存在则更新，不存在则新增）
     * @param afterSalePo 售后单PO对象
     * @return 保存后的售后单PO（含自增ID）
     */
    public AfterSalePo saveAftersale(AfterSalePo afterSalePo) {
        log.info("【DAO层】开始保存售后单到数据库 - aftersaleId={}, status={}, serviceOrderId={}", 
                afterSalePo.getAftersaleId(), afterSalePo.getStatus(), afterSalePo.getServiceOrderId());
        log.debug("saveAftersale:po={}",afterSalePo);
        
        AfterSalePo savedPo = aftersaleMapper.save(afterSalePo);
        
        log.info("【DAO层】售后单保存完成 - aftersaleId={}, 保存后的ID={}", 
                afterSalePo.getAftersaleId(), savedPo.getAftersaleId());
        return savedPo;
    }



}