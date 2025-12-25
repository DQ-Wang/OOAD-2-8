package cn.edu.xmu.oomall.aftersale.Dao.bo;
import cn.edu.xmu.oomall.aftersale.service.feign.PaymentClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface RefundInterface
{
    default void refund(AfterSale afterSale, PaymentClient paymentClient)
    {
        Logger log = LoggerFactory.getLogger(RefundInterface.class);
        log.info("【{} BO已退款】 - aftersaleId={}",afterSale.getClass().getSimpleName(),afterSale.getServiceOrderId());

        paymentClient.refund(afterSale.getShopId(),1L);     //因为mock掉了所以随便传一个支付单的id
    }

}
