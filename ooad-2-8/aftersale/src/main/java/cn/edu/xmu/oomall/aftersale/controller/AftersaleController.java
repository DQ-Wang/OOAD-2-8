package cn.edu.xmu.oomall.aftersale.controller;
import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.oomall.aftersale.service.AfterSaleService;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.oomall.aftersale.service.vo.AftersaleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import cn.edu.xmu.javaee.core.aop.Audit;
//TODO: import AftersaleService

@RestController
@RequestMapping("/shops/{shopId}/aftersales")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AftersaleController {

    // 注入售后Service（业务逻辑层）
    private final AfterSaleService aftersaleService;

    /**
     * 商户审核售后单（对应API：PUT /shops/{shopId}/aftersales/{id}/confirm）
     *
     * @param shopId 路径参数：店铺id
     * @param id 路径参数：售后单id
     * @param dto 请求体：审核参数（confirm/conclusion/type）
     * @return 统一返回对象（包含操作结果）
     */
    @Audit(departName = "shops")
    @PutMapping("/{id}/confirm")
    public ReturnObject reviewAftersale(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AftersaleConfirmDto dto)
    {
        log.info("【审核售后API入口】收到审核请求 - shopId={}, aftersaleId={}, confirm={}, conclusion={}", 
                shopId, id, dto.getConfirm(), dto.getConclusion());
        log.debug("reviewAftersale(Controller): aftersaleId = {}", id);
        
        AftersaleVo aftersaleVo = aftersaleService.reviewAftersale(id,dto);

        log.info("【审核售后API完成】审核成功 - shopId={}, aftersaleId={}, 返回结果={}", shopId, id, aftersaleVo);
        return new ReturnObject(ReturnNo.OK,"成功",aftersaleVo);
    }
}
