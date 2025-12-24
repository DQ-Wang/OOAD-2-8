package cn.edu.xmu.oomall.aftersale.service;


import cn.edu.xmu.oomall.aftersale.Dao.bo.ConfirmProductInterface;
import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.oomall.aftersale.service.vo.AftersaleProductVo;
import cn.edu.xmu.oomall.aftersale.service.vo.AftersaleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;

@Service
@Transactional(propagation = Propagation.REQUIRED)
//@RequiredArgsConstructor
@Slf4j
public class AfterSaleService {

//    @Autowired
    private final AfterSaleDao aftersaleDao;


    // Service层构造函数注入AfterSaleDao（Spring自动注入）
    @Autowired
    public AfterSaleService(AfterSaleDao aftersaleDao) {
        this.aftersaleDao = aftersaleDao;
        log.info("【AfterSaleService】注入AfterSaleDao成功");
    }


    /**
     * 审核售后单
     *
     * @param id               售后单id
     * @param dto              审核售后单dto
     */
    public AftersaleVo reviewAftersale(@PathVariable Long id, @RequestBody AftersaleConfirmDto dto)
    {
        log.info("【Service层】开始审核售后单 - aftersaleId={}, confirm={}, conclusion={}", 
                id, dto.getConfirm(), dto.getConclusion());
        log.debug("reviewAftersale(Service): aftersaleId = {}", id);
        
        AfterSale aftersale = aftersaleDao.findAftersaleById(id);
        log.info("【Service层】查询到售后单 - aftersaleId={}, type={}, status={}", 
                aftersale.getAftersaleId(), aftersale.getType(), aftersale.getStatus());
        

        
        log.info("【Service层】开始执行售后单审核处理逻辑 - aftersaleId={}", id);
        String handleResult = aftersale.HandleAftersale(dto.getConfirm(), dto.getConclusion());
        log.info("【Service层】售后单审核处理完成 - aftersaleId={}, 处理结果={}", id, handleResult);

        //IdNameTypeVo vo = IdNameTypeVo.builder().id(aftersale.getAftersaleId()).name("").build();

        AftersaleVo aftersaleVo = new AftersaleVo(aftersale.getAftersaleId(),handleResult);
        return aftersaleVo;
    }

    /**
     * 验收售后商品
     * @param aftersaleId   售后单id
     */
    public AftersaleProductVo confirmProduct(@PathVariable Long aftersaleId, @RequestBody boolean confirm, @RequestBody String reason)
    {
        log.info("【Service层】开始验收售后商品 - aftersaleId={}, confirm={}, reason={}",
                aftersaleId, confirm, reason);
        log.debug("confirmAftersaleProduct(Service): aftersaleId = {}", aftersaleId);

        AfterSale aftersale = aftersaleDao.findAftersaleById(aftersaleId);
        log.info("【Service层】查询到售后商品对应的售后单 - aftersaleId={}, type={}, status={}",
                aftersale.getAftersaleId(), aftersale.getType(), aftersale.getStatus());

        if(aftersale instanceof ConfirmProductInterface)
        {
            log.info("【Service层】开始执行验收售后商品处理逻辑 - aftersaleId={}", aftersaleId);
            ConfirmProductInterface confirmProductInterface = (ConfirmProductInterface) aftersale;
            confirmProductInterface.confirmProduct(confirm,reason);
        }
        else
        {
            log.error("【Service层】该售后类型不支持验收售后商品 - aftersaleId={}, type={}", aftersaleId, aftersale.getClass().getSimpleName());
            throw new ClassCastException("该售后类型不支持验收售后商品");
        }



        //IdNameTypeVo vo = IdNameTypeVo.builder().id(aftersale.getAftersaleId()).name("").build();

        AftersaleProductVo aftersaleProductVo = new AftersaleProductVo(aftersale.getAftersaleId(),aftersale.getStatus());
        return aftersaleProductVo;

    }

    /**
     * 取消售后单
     * @param aftersaleId   售后单id
     */
    public AftersaleVo cancelAftersale(@PathVariable Long aftersaleId,String reason)
    {
        log.info("【Service层】开始取消售后单 - aftersaleId={}",
                aftersaleId);
        log.debug("cancelAftersaleProduct(Service): aftersaleId = {}", aftersaleId);

        AfterSale aftersale = aftersaleDao.findAftersaleById(aftersaleId);
        log.info("【Service层】查询到对应的售后单 - aftersaleId={}, type={}, status={}",
                aftersale.getAftersaleId(), aftersale.getType(), aftersale.getStatus());


        log.info("【Service层】开始执行售后单取消逻辑 - aftersaleId={}", aftersaleId);
        boolean handleResult= aftersale.CancleAftersale(reason);        //返回handleResult
        log.info("【Service层】售后单取消处理完成 - aftersaleId={}, 处理结果={}",aftersaleId, handleResult);

        //IdNameTypeVo vo = IdNameTypeVo.builder().id(aftersale.getAftersaleId()).name("").build();

        AftersaleVo aftersaleVo = new AftersaleVo(aftersale.getAftersaleId(),aftersale.getServiceOrderId());    //TODO:返回加上运单Id
        return aftersaleVo;
    }


}
