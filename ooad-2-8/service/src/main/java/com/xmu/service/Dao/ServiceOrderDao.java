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
import org.springframework.beans.BeanUtils;
import java.time.LocalDateTime;


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
        // 将所有构建器按 typeName 建立映射（供本类使用）
        this.builders = builders.stream()
                .collect(Collectors.toMap(ServiceOrderBuilder::getTypeName, Function.identity()));
        // 初始化 ServiceOrder 的构建器映射表（按 type）
        ServiceOrder.initBuilders(builders);

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

        // 使用 type 的 Byte 值查找对应的 builder
        // 需要将 Byte 转换为 String 作为 Map 的 key
        String typeName = ServiceOrder.TYPE_NAMES.get(po.getType());
        if (typeName == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: unknown type " + po.getType());
        }
        ServiceOrderBuilder builder = builders.get(typeName);

        if (builder == null) {
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderDao.build: unknown type " + po.getType());
        }
        // 具体子类的创建与属性拷贝交给对应的构建器完成
        ServiceOrder bo = builder.build(po, this);
        // 注入 ExpressClient（可选，测试时通过 @MockBean 提供）

        
        return bo;
    }
    


    /**
     * 更新服务单
     *
     *
     * 
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

        // 2. 查询现有数据（此时 oldPo 被持久化上下文管理）
        ServiceOrderPo oldPo = mapper.findById(bo.getId())
                .orElseThrow(() -> new BusinessException(
                        ReturnNo.RESOURCE_ID_NOTEXIST,
                        String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "服务单", bo.getId())
                ));

         // 3. 直接在 oldPo 上修改（不要 new）
         // 注意：这里要把 bo 的值覆盖到 oldPo 上
         // 第三个参数是“忽略字段”
         BeanUtils.copyProperties(bo, oldPo, "id");

        // 4. 保存
        // 此时 save 会触发 UPDATE 语句，因为 oldPo 已经有 ID 且在 Session 中
        mapper.save(oldPo);
        log.info("【ServiceOrderDao】更新服务单成功 - id={}", bo.getId());
    }



    /**
     * 插入服务单
     */
    public void insert(ServiceOrder bo)
    {
        ServiceOrderPo po = new ServiceOrderPo();
        BeanUtils.copyProperties(bo, po);
        po.setStatus(bo.getStatus());
        po.setType(bo.getType());
        LocalDateTime createTime = bo.getCreateTime();
        if (createTime == null) {
            createTime = java.time.LocalDate.now().atStartOfDay();
        }
        po.setCreateTime(createTime);
        mapper.save(po);
        // 回填生成的主键到 BO，便于返回 VO 时带出 id
        bo.setId(po.getId());
        bo.setServiceOrderDao(this);
        // 注入 ExpressClient（可选，测试时通过 @MockBean 提供）

        log.info("【ServiceOrderDao】插入服务单成功 - id={}", bo.getId());
    }


}