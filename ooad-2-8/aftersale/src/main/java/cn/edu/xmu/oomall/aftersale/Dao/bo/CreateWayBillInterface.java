package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.controller.dto.AddressDto;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateExpressDto;
import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
import org.springframework.http.ResponseEntity;

public interface CreateWayBillInterface
{
   default String createWayBill(AfterSale afterSale, AfterSaleFeignClient afterSaleFeignClient)
   {
       CreateExpressDto createExpressDto = new CreateExpressDto();
       AddressDto addressDto = new AddressDto();
       addressDto.setAddress(afterSale.getAddress());
       addressDto.setMobile(afterSale.getMobile());
       createExpressDto.setAddress(addressDto);
       ResponseEntity<String> expressId = afterSaleFeignClient.createExpress(afterSale.getShopId(),createExpressDto);
       return expressId.getBody();

   }
}
