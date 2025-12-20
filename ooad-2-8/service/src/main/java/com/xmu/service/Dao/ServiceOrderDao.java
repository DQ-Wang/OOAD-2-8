package com.xmu.service.Dao;

import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.ServiceOrderMapper ;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * 服务单Dao（封装数据操作）
 */
@Repository
public class ServiceOrderDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOrderDao.class);
    
    @Autowired
    private ServiceOrderMapper serviceOrderMapper;

    /**
     * 保存服务单（调用Mapper持久化）
     */
    public void save(ServiceOrder bo) {
        LOGGER.info("【ServiceOrder Dao】开始保存服务单到数据库 - serviceSn={}, aftersalesId={}", 
                bo.getServiceSn(), bo.getAftersalesId());
        LOGGER.debug("【ServiceOrder Dao】BO转PO前 - BO={}", bo);
        
        ServiceOrderPo po = bo.toPo();
        LOGGER.debug("【ServiceOrder Dao】BO转PO完成，准备执行INSERT - PO={}", po);
        
        serviceOrderMapper.insert(po);
        LOGGER.info("【ServiceOrder Dao】INSERT执行完成，准备回写主键ID到BO");
        
        // 回写主键ID（MyBatis的@Options会自动填充po.id）
        bo.setId(po.getId()); 
        LOGGER.info("【ServiceOrder Dao】服务单保存完成 - serviceOrderId={}, serviceSn={}", 
                bo.getId(), bo.getServiceSn());
    }
}