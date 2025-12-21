package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.service.feign.ServiceOrderFeignClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
public class ReturnAndRefund extends AfterSale implements RefundInterface
{



    @Override
    public String HandleAftersale(boolean confirm, String reason) {
        return "";
    }

    @Override
    public void refund() {
        
    }
}
