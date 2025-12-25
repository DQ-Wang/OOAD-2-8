package com.xmu.service.Dao.assembler;


import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.mapper.po.ServiceOrderPo;
import com.xmu.service.mapper.ServiceOrderPoMapper;



/**
 * ServiceOrder 子类构建器接口
 * 负责根据 Po 或 DTO 构造具体的 ServiceOrder 子类
 *
 * 新增一种 ServiceOrder 子类时，只需要新增一个实现本接口的 Bean，
 *
 */
public interface ServiceOrderBuilder {
    /**
     * 获取构建器支持的服务单类型（Byte类型，用于匹配DTO中的type）
     * @return 服务单类型Byte值
     */
    Byte getType();

    /**
     * 从PO构建领域对象
     * @param po 持久化对象
     * @param dao 数据访问对象
     * @return 服务单领域对象
     */
    ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao);

    /**
     * 从DTO创建领域对象（用于新建服务单）
     * @param dto 创建服务单请求DTO
     * @param shopId 店铺ID
     * @param afterSaleId 售后单ID
     * @return 服务单领域对象
     */
    ServiceOrder createFromDto(ServiceOrderDto dto, Long shopId, Long afterSaleId);
}
