package cn.edu.xmu.oomall.aftersale.service;


import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import cn.edu.xmu.oomall.aftersale.Dao.AftersaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.Aftersale;

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
    public void reviewAftersale(@PathVariable String id, @RequestBody AftersaleConfirmDto dto)
    {
        Aftersale aftersale = AfterSaleDao.findById(id);
        aftersale.handelAftersale(dto.getConfirm(),dto.getConclusion());
    }
}
