package cn.edu.xmu.oomall.aftersale.service;


import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.javaee.core.model.IdNameTypeVo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
@Slf4j
public class AfterSaleService {

    private final AfterSaleDao afterSaleDao;

    /**
     * 审核售后单
     *

     * @param id               售后单id
     * @param dto              审核售后单dto
     */
    public IdNameTypeVo reviewAftersale(@PathVariable Long id, @RequestBody AftersaleConfirmDto dto)
    {
        log.info("【Service层】开始审核售后单 - aftersaleId={}, confirm={}, conclusion={}", 
                id, dto.getConfirm(), dto.getConclusion());
        log.debug("reviewAftersale(Service): aftersaleId = {}", id);
        
        AfterSale aftersale = afterSaleDao.findAftersaleById(id);
        log.info("【Service层】查询到售后单 - aftersaleId={}, type={}, status={}", 
                aftersale.getAftersaleId(), aftersale.getType(), aftersale.getStatus());
        
        IdNameTypeVo vo = IdNameTypeVo.builder().id(aftersale.getAftersaleId()).name("").build();
        
        log.info("【Service层】开始执行售后单审核处理逻辑 - aftersaleId={}", id);
        String handleResult = aftersale.HandleAftersale(dto.getConfirm(), dto.getConclusion());
        log.info("【Service层】售后单审核处理完成 - aftersaleId={}, 处理结果={}", id, handleResult);
        
        return vo;
    }
}
