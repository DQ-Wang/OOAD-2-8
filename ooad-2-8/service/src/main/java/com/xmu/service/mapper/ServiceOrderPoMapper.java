package com.xmu.service.mapper;

import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 服务单持久化对象 Mapper
 * 注意：JPA Repository 接口不需要 @Repository 注解，Spring Data JPA 会自动识别
 */
public interface ServiceOrderPoMapper extends JpaRepository<ServiceOrderPo, Long> {



}