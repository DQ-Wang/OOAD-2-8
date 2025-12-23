package com.xmu.service.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xmu.service.mapper.po.WorkerPo;

/**
 * 员工持久化对象（对应worker表）
 */
@Repository
public interface WorkerPoMapper extends JpaRepository<WorkerPo, String>{


}
