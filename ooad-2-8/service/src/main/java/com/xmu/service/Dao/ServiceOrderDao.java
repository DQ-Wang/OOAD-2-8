package com.xmu.service.Dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.assembler.ServiceOrderBuilder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.ServiceOrderPoMapper ;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 服务单Dao（封装数据操作）
 */
@Slf4j
@Repository
public class ServiceOrderDao {


    private final ServiceOrderPoMapper mapper;
    private final Map<String, ServiceOrderBuilder> builders;

    @Autowired
    public ServiceOrderDao(ServiceOrderPoMapper mapper, List<ServiceOrderBuilder> builders) {
        this.mapper = mapper;
        // 将所有构建器按 type 建立映射，避免在 Dao 中写 switch / if-else
        this.builders = builders.stream()
                .collect(Collectors.toMap(ServiceOrderBuilder::getType, Function.identity()));
    }

    public ServiceOrder findById(Long id) {
        ServiceOrderPo po = mapper.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ReturnNo.RESOURCE_ID_NOTEXIST,
                        String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "服务单", id)
                ));
        return build(po);
    }

    /* ================= 构建 BO ================= */

    private ServiceOrder build(ServiceOrderPo po) {
        if (po.getType() == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: po.type is null");
        }
        ServiceOrderBuilder builder = builders.get(po.getType());
        if (builder == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: unknown type " + po.getType());
        }
        // 具体子类的创建与属性拷贝交给对应的构建器完成
        return builder.build(po, this);
    }


    public void update(ServiceOrder bo)
    {

    }

    public ServiceOrder insert(ServiceOrder bo)
    {

        return bo;
    }


}