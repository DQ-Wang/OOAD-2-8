package com.xmu.service.Dao;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.bo.Worker;
import com.xmu.service.mapper.WorkerPoMapper;
import com.xmu.service.mapper.po.WorkerPo;
import org.springframework.stereotype.Repository;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/23 16:01
 */
@Repository
public class WorkerDao {

    private final WorkerPoMapper mapper;
    public WorkerDao(WorkerPoMapper mapper) {
        this.mapper = mapper;
    }

    public Worker findById(String id){
        WorkerPo po = mapper.findById(id).orElseThrow(() ->
                new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                        String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "员工", id))
        );
        Worker worker = new Worker();
        worker.setId(po.getId());
        worker.setName(po.getName());
        worker.setMobile(po.getMobile());
        return worker;
    }
}