package cn.edu.xmu.oomall.aftersale.controller;


import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ReturnAndRefund;
import cn.edu.xmu.oomall.aftersale.assembler.AfterSaleBuilder;
import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateExpressDto;
import cn.edu.xmu.oomall.aftersale.mapper.AfterSaleMapper;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.AfterSaleService;
import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
import cn.edu.xmu.oomall.aftersale.service.feign.ExpressClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest// 启动Spring上下文（集成测试）
public class AftersaleControllerTest
{
    @Autowired
    // 1. 待测试的BO对象（自动注入Mock依赖）
    private AfterSaleService afterSaleService;

    @Autowired
    private AfterSaleMapper afterSaleMapper;
    @Autowired
    private List<AfterSaleBuilder> builderList;

    @Autowired
    private AfterSaleDao afterSaleDao;

    @MockitoBean
    private ExpressClient expressClient;


    @Autowired
    private AfterSaleFeignClient afterSaleFeignClient;

    /**
     * 测试场景1：创建退货运单成功
     * 验证：调用物流Feign创建运单后，返回正确的运单号
     */
    // 3. 每个测试方法前初始化BO实例和Mock对象
    @BeforeEach
    void setUp() {
        afterSaleDao=new AfterSaleDao(afterSaleMapper,builderList);
        afterSaleDao.afterSaleFeignClient=afterSaleFeignClient;
        afterSaleService=new AfterSaleService(afterSaleDao);
    }

     @Test
    void reviewMaintenanceTest_confirm() {
        // ========== 1. 模拟依赖行为 ==========
        // 模拟物流Feign返回成功的运单号
         Random random = new Random();
         String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 1L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(true,"");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);

    }

    @Test
    void reviewMaintenanceTest_reject() {
        // ========== 1. 模拟依赖行为 ==========
        // 模拟物流Feign返回成功的运单号
//        Random random = new Random();
//        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
//        when(afterSaleFeignClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 2L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(false,"已过维修售后期");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    @Test
    void reviewRefundOnlyTest_confirm() {
        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 3L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(true,"");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);

    }

    @Test
    void reviewRefundOnlyTest_reject() {

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 4L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(false,"仅退款理由不充分");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    @Test
    void reviewReturnAndRefundTest_confirm() {

        Random random = new Random();
        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 5L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(true,"");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    @Test
    void reviewReturnAndRefundTest_reject() {
        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 6L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(false,"退货退款理由不充分");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    @Test
    void reviewExchangeTest_confirm() {
        // ========== 2. 执行测试方法 ==========

        Random random = new Random();
        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        Long aftersaleId = 7L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(true,"");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    @Test
    void reviewExchangeTest_reject() {
        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 8L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(false,"已过换货售后期");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);
    }

    /**
     * 测试场景2：商户验收售商品
     * 验证：数据库状态发生改变/调用物流Feign创建运单后，返回正确的运单号
     */
    @Test
    void confirmProductTest_trueReturnAndRefund() {//用售后单ID=10测试

//        Random random = new Random();
//        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
//        when(afterSaleFeignClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 10L;

        afterSaleService.confirmProduct(aftersaleId,true,"符合验收标准");
    }


    @Test
    void confirmProductTest_falseReturnAndRefund() {//用售后单ID=11测试

        Random random = new Random();
        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 11L;

        afterSaleService.confirmProduct(aftersaleId,false,"商品破损");
    }



    @Test
    void confirmProductTest_trueExchange() {//用售后单ID=12测试

        Random random = new Random();
        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 12L;

        afterSaleService.confirmProduct(aftersaleId,true,"符合验收标准");
    }



    @Test
    void confirmProductTest_falseExchange() {//用售后单ID=13测试

        Random random = new Random();
        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 13L;

        afterSaleService.confirmProduct(aftersaleId,false,"商品破损");
    }





    /**
     * 测试场景3：商户取消售后
     * 验证：数据库状态发生改变/调用物流Feign创建运单后，返回正确的运单号
     */
    @Test
    void cancleAfterSaleTest_RefundOnly() {//用售后单ID=14测试

//        Random random = new Random();
//        String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
//        when(afterSaleFeignClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 14L;

        afterSaleService.cancelAftersale(aftersaleId,"");
    }




}
