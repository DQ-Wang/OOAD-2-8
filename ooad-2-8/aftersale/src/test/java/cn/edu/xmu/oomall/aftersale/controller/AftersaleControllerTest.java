package cn.edu.xmu.oomall.aftersale.controller;


import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.ReturnAndRefund;
import cn.edu.xmu.oomall.aftersale.controller.dto.AftersaleConfirmDto;
import cn.edu.xmu.oomall.aftersale.controller.dto.CreateExpressDto;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.service.AfterSaleService;
import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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



    @MockitoBean
    private AfterSaleFeignClient afterSaleFeignClient;

    /**
     * 测试场景1：创建退货运单成功
     * 验证：调用物流Feign创建运单后，返回正确的运单号
     */
    // 3. 每个测试方法前初始化BO实例和Mock对象
    @BeforeEach
    void setUp() {

    }

     @Test
    void reviewAftersaleTest_Success() {
        // ========== 1. 模拟依赖行为 ==========
        // 模拟物流Feign返回成功的运单号
         Random random = new Random();
         String randomWaybillId = "SF" + random.nextLong(10000000000L); // 生成0-9999999999的随机数
        when(afterSaleFeignClient.createExpress(anyLong(), any(CreateExpressDto.class)))
                .thenReturn(ResponseEntity.ok(randomWaybillId));

        // ========== 2. 执行测试方法 ==========
        Long aftersaleId = 1L;
        AftersaleConfirmDto aftersaleConfirmDto = new AftersaleConfirmDto(true,"");

        afterSaleService.reviewAftersale(aftersaleId,aftersaleConfirmDto);

    }

}
