package com.xmu.service.mapper;

import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




import java.util.List;

/**
 * 服务单持久化对象 Mapper
 */
@Repository
public interface ServiceOrderPoMapper extends JpaRepository<ServiceOrderPo, Long> {

    /**
     * 根据员工ID查询服务单列表
     */
    List<ServiceOrderPo> findByWorkerId(Long workerId, Pageable pageable);



    /**
     * 根据服务单编号查询
     */


    /**
     * 根据服务提供商ID查询服务单列表
     */
    List<ServiceOrderPo> findByServiceProviderId(Long serviceProviderId, Pageable pageable);


}