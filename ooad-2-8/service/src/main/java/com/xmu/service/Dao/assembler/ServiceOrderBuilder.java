package com.xmu.service.Dao.assembler;


import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.po.ServiceOrderPo;
import com.xmu.service.mapper.ServiceOrderPoMapper;




/**
 * ServiceOrder 子类构建器接口
 * 负责根据 Po 构造具体的 ServiceOrder 子类，但子类本身不是 Spring Bean。
 *
 * 新增一种 ServiceOrder 子类时，只需要新增一个实现本接口的 Bean，
 * 不需要修改 ServiceOrderDao，满足开闭原则。
 */
public interface ServiceOrderBuilder {
    /**
     * 获取构建器支持的服务单类型（String类型，作为Map的key）
     * @return 服务单类型字符串
     */
    String getType();

    /**
     * 从PO构建领域对象
     * @param po 持久化对象
     * @param dao 数据访问对象
     * @return 服务单领域对象
     */
    ServiceOrder build(ServiceOrderPo po, ServiceOrderDao dao);
}
