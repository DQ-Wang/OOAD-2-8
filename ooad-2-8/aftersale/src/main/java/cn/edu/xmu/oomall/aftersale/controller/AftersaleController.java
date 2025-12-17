package cn.edu.xmu.oomall.aftersale.controller;
import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.oomall.aftersale.service.AfterSaleService;
import cn.edu.xmu.javaee.core.model.IdNameTypeVo;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
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

        log.debug("reviewAftersale(Controller): aftersaleId = {}", id);
        IdNameTypeVo vo = aftersaleService.reviewAftersale(id,dto);
        return new ReturnObject(ReturnNo.OK,"成功",vo);
    }
}
