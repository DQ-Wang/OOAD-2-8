package cn.edu.xmu.oomall.aftersale.Dao.bo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface RefundInterface
{
    default void refund(AfterSale afterSale)
    {
        Logger log = LoggerFactory.getLogger(RefundInterface.class);
        log.info("【Refund BO已退款】 - aftersaleId={}",afterSale.getServiceOrderId());
    }

}
