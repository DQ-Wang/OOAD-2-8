package com.xmu.service.Dao;

import cn.edu.xmu.javaee.core.clonefactory.CloneFactory;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.assembler.ServiceOrderBuilder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.Dao.converter.ServiceOrderConverter;
import com.xmu.service.mapper.ServiceOrderPoMapper ;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    private final ServiceOrderConverter converter;

    @Autowired
    public ServiceOrderDao(ServiceOrderPoMapper mapper, List<ServiceOrderBuilder> builders, ServiceOrderConverter converter) {
        this.mapper = mapper;
        this.converter = converter;
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

    public List<ServiceOrder> retrieveByWorkerId(Long workerId) {
        return mapper.findByWorkerId(workerId, Pageable.unpaged()).stream()
                .map(this::build)
                .toList();
    }


    /* ================= 构建 BO ================= */

    private ServiceOrder build(ServiceOrderPo po) {
        if (po.getType() == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: po.type is null");
        }
        // 将Byte类型的type转换为String，用于查找对应的builder
        String typeStr = converter.convertTypeByteToString(po.getType());
        ServiceOrderBuilder builder = builders.get(typeStr);
        if (builder == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: unknown type " + po.getType());
        }
        // 具体子类的创建与属性拷贝交给对应的构建器完成
        ServiceOrder bo = builder.build(po, this);
        
        // 使用转换器处理需要类型转换的字段（id, type, status）
        converter.convertPoToBo(po, bo);
        
        return bo;
    }
    


    /**
     * 更新服务单
     *
     * 优化说明：
     * 1. 使用 CloneFactory.copyNotNull 自动拷贝所有匹配字段（替代手动设置8个字段）
     * 2. status 和 type 以数字（Byte）方式存入数据库
     * 3. 支持部分字段更新（只更新非空字段）
     *
     * @param bo 服务单业务对象
     * @throws BusinessException 如果服务单不存在
     */
    public void update(ServiceOrder bo) {
        // 1. 参数校验
        if (bo.getId() == null) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, "服务单ID不能为空");
        }

        // 2. 将 String id 转换为 Long id（转换器会在 convertBoToPo 中处理，这里先转换用于查询）
        Long id;
        try {
            id = Long.parseLong(bo.getId());
        } catch (NumberFormatException e) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, 
                    String.format("服务单ID格式错误: %s", bo.getId()));
        }

        // 3. 查询现有数据（用于合并更新，保留未修改的字段）
        ServiceOrderPo oldPo = mapper.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ReturnNo.RESOURCE_ID_NOTEXIST,
                        String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "服务单", id)
                ));

        // 4. 使用 CloneFactory.copyNotNull 自动拷贝所有匹配字段
        // 拷贝：serviceSn, serviceConsignee, serviceMobile, address,
        //            description, expressId, workerId, serviceProviderId 等所有匹配字段
        ServiceOrderPo po = CloneFactory.copyNotNull(oldPo, bo);

        // 5. 使用转换器处理需要类型转换的字段（id, type, status）
        converter.convertBoToPo(bo, po);

        // 6. 保存更新
        mapper.save(po);
        log.info("【ServiceOrderDao】更新服务单成功 - id={}", id);
    }






    /**
     * 将Date转换为LocalDateTime
     */
    private LocalDateTime convertDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public ServiceOrder insert(ServiceOrder bo)
    {
        // TODO: 实现插入逻辑
        return null;
    }


}