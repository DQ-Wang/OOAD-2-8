package com.xmu.service.Dao;

import com.xmu.service.Dao.bo.ServiceOrderBo;
import com.xmu.service.mapper.ServiceOrderMapper ;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * 服务单Dao（封装数据操作）
 */
@Repository
public class ServiceOrderDao {
    @Autowired
    private ServiceOrderMapper serviceOrderMapper;

    /**
     * 保存服务单（调用Mapper持久化）
     */
    public void save(ServiceOrderBo bo) {
        ServiceOrderPo po = bo.toPo();
        serviceOrderMapper.insert(po);
        bo.setId(po.getId()); // 回写主键ID
    }
}