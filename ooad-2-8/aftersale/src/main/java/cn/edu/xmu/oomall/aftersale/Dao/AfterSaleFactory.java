package cn.edu.xmu.oomall.aftersale.Dao;

import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.Dao.bo.Maintenance;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AfterSaleFactory {

    @Autowired
    private ApplicationContext context;


    public AfterSale creatAfterSale(AfterSalePo aftersalePo, AfterSaleDao afterSaleDao, ServiceOrderFeignClient serviceOrderFeignClient)
    {

        AfterSale bo;
        switch (aftersalePo.getType()) {
            case 1:
                bo=new Maintenance(afterSaleDao,serviceOrderFeignClient);
                break;

                default:bo=null;
        }
        bo.setMobile(aftersalePo.getMobile());
        bo.setAddress(aftersalePo.getAddress());
        bo.setQuantity(aftersalePo.getQuantity());
        bo.setAftersalePo(aftersalePo);
        return bo;
    }
}
