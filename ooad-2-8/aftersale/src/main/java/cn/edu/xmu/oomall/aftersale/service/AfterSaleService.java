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

    private final AfterSaleDao regionDao;

    /**
     * 审核售后单
     *

     * @param id               售后单id
     * @param dto              审核售后单dto
     */
    public IdNameTypeVo reviewAftersale(@PathVariable Long id, @RequestBody AftersaleConfirmDto dto)
    {
        log.debug("reviewAftersale(Service): aftersaleId = {}", id);
        AfterSale aftersale = AfterSaleDao.findAftersaleById(id);
        IdNameTypeVo vo = IdNameTypeVo.builder().id(aftersale.getAftersaleId()).name("").build();
        aftersale.HandleAftersale(dto.getConfirm(),dto.getConclusion());
        return vo;
    }
}
