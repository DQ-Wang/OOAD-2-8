//School of Informatics Xiamen University, GPL-3.0 license
package com.xmu.service.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.IdNameTypeVo;
import cn.edu.xmu.javaee.core.model.UserToken;
import cn.edu.xmu.javaee.core.util.CloneFactory;
import com.xmu.service.controller.dto.ServiceOrderDto ;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 售后审核通过创建服务单
 *
 * @author 你的名字
 * @task 对应你的任务编号（如2025-dgn2-001）
 */
@RestController
@RequestMapping("/internal/shops")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceOrderService;

    /**
     * 创建服务单（适配实验要求的API路径）
     * 仿照ShopController的createShops方法实现
     */
    @PostMapping("/{shopId}/aftersales/{id}/serviceorders")
    @Audit(departName = "serviceorders")
    public ReturnObject createServiceOrder(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long aftersaleId,
            @Validated @RequestBody ServiceOrderDto serviceOrderDto,
            @LoginUser UserToken user
    ) {
//        // 1. 权限校验（仿照ShopController的权限逻辑）
//        // 规则：仅允许无商铺用户/非平台管理员创建服务单（可根据实际需求调整）
//        if (NOSHOP != user.getDepartId() || PLATFORM == user.getDepartId()) {
//            throw new BusinessException(ReturnNo.SERVICEORDER_USER_FORBIDDEN,
//                    String.format(ReturnNo.SERVICEORDER_USER_FORBIDDEN.getMessage(), user.getId()));
//        }

        // 2. DTO转BO（核心：复用CloneFactory.copy，和ShopController一致）
        ServiceOrder serviceOrder = CloneFactory.copy(new ServiceOrder(), serviceOrderDto);

        // 3. 设置路径参数和默认值（仿照Shop设置freeThreshold的默认值逻辑）
        serviceOrder.setShopId(shopId); // 从路径参数获取店铺ID
        serviceOrder.setAftersaleId(aftersaleId); // 从路径参数获取售后单ID
        serviceOrder.setStatus((byte) 0); // 服务单初始状态：待处理（非空字段手动赋默认值）
        serviceOrder.setServiceType("REPAIR"); // 维修类型默认值（非空字段兜底）

        // 4. 调用Service层创建服务单（和ShopController调用createShop逻辑一致）
        ServiceOrder ret = this.serviceOrderService.createServiceOrder(serviceOrder, user);

        // 5. 构建返回VO（复用IdNameTypeVo，和ShopController返回结构一致）
        IdNameTypeVo vo = IdNameTypeVo.builder()
                .id(ret.getId())
                .name(ret.getServiceType()) // 名称字段填充服务类型，贴合Vo结构
                .build();

        // 6. 返回创建成功结果（和ShopController一致使用ReturnNo.CREATED）
        return new ReturnObject(ReturnNo.CREATED, vo);
    }
}