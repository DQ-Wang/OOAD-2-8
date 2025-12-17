package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AfterSaleFactory {

    private final ApplicationContext context;


    public AfterSale creatAfterSale(Optional<AfterSalePo> optionalPo)
    {
        log.debug("creating aftersale:optionalPo={}",optionalPo);
        AfterSalePo aftersalePo = optionalPo.orElseThrow(() ->
                new IllegalArgumentException("售后单PO不存在（Optional为空）")
        );

        return (AfterSale) context.getBean(aftersalePo.getBeanName());
    }
}
