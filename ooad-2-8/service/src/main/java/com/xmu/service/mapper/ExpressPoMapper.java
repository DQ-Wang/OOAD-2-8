package com.xmu.service.mapper;
import com.xmu.service.mapper.po.ServiceOrderPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




import java.util.List;
import java.util.Optional;


public interface ExpressPoMapper extends JpaRepository<ServiceOrderPo, Long> {
}
