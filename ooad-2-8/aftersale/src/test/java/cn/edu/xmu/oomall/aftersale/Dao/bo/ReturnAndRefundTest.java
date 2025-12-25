//package cn.edu.xmu.oomall.aftersale.Dao.bo;
//
//
//import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
//import cn.edu.xmu.oomall.aftersale.controller.dto.CreateExpressDto;
//import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;
//import cn.edu.xmu.oomall.aftersale.service.feign.AfterSaleFeignClient;
//import cn.edu.xmu.oomall.aftersale.service.feign.ExpressClient;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//@SpringBootTest// 启动Spring上下文（集成测试）
//public class ReturnAndRefundTest {
//
//    @Autowired
//    // 1. 待测试的BO对象（自动注入Mock依赖）
//    private ReturnAndRefund refundAndReturn;
//
//    // 2. Mock依赖的组件（避免调用真实DAO/Feign）
//    @MockitoBean
//    private AfterSaleDao afterSaleDao;
//
//
//    @MockitoBean
//    private AfterSaleFeignClient afterSaleFeignClient;
//    @Autowired
//    private ExpressClient expressClient;
//
//    /**
//     * 测试场景1：创建退货运单成功
//     * 验证：调用物流Feign创建运单后，返回正确的运单号
//     */
//    // 3. 每个测试方法前初始化BO实例和Mock对象
//    @BeforeEach
//    void setUp() {
//        // 初始化Mockito Mock对象
//      //  MockitoAnnotations.openMocks(this);
//
//        // 手动创建BO实例（核心修正：不用Spring注入，自己new）
////        refundAndReturn = new ReturnAndRefund();
////        // 初始化BO的基础属性
//        refundAndReturn.setAftersaleId(1L);
//        refundAndReturn.setShopId(1001L);
//
//        refundAndReturn.setType((byte)3);           // 售后类型：1=维修 2=仅退款 3=退货退款 4=换货
//        refundAndReturn.setServiceType((byte)2);    // 服务类型：1=上门 2=寄送
//        refundAndReturn.setStatus((byte)0);
////        // 售后状态：0=待审核 1=已同意 2=已拒绝 3=商家待收货 4=待分配服务商 5=服务待完成 6=已完成 7=已取消 8=顾客待收货
//        refundAndReturn.setReason("");     // 审核原因
////
//         refundAndReturn.setConsignee("你好");  // 姓名
//         refundAndReturn.setMobile("13367731690");   // mobile;
//         refundAndReturn.setAddress("福建省厦门市翔安区");
////         refundAndReturn.setAftersaleFeignClient(afterSaleFeignClient);
////         refundAndReturn.setAftersalePo(new AfterSalePo()) ;
//    }
//
//   // @Test
//    void testCreateWayBill_Success() {
//        // ========== 1. 模拟依赖行为 ==========
//        // 模拟物流Feign返回成功的运单号
//        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok("SF1234567890"));
//
//        // ========== 2. 执行测试方法 ==========
//        String waybillId = refundAndReturn.createWayBill(refundAndReturn, expressClient);
//
//        // ========== 3. 验证结果 ==========
//        // 断言运单号非空且符合预期
//        assertNotNull(waybillId);
//        assertEquals("SF1234567890", waybillId);
//        // 断言BO对象的运单号已更新
//        assertEquals("SF1234567890", refundAndReturn.getReturnExpress());
//    }
//
//    @Test void testHandleAftersale_confirm()
//    {
//        // ========== 1. 模拟依赖行为 ==========
//        // 模拟物流Feign返回成功的运单号
//        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok("SF1234567890"));
//
//        // ========== 2. 执行测试方法 ==========
//        String waybillId = refundAndReturn.HandleAftersale(true, "");
//
//        // ========== 3. 验证结果 ==========
//        // 断言运单号非空且符合预期
//        assertNotNull(waybillId);
//        assertEquals("SF1234567890", waybillId);
//        // 断言BO对象的运单号已更新
//        assertEquals("SF1234567890", refundAndReturn.getReturnExpress());
//    }
//
//    @Test void testHandleAftersale_reject()
//    {
//        // ========== 1. 模拟依赖行为 ==========
//        // 模拟物流Feign返回成功的运单号
//        when(expressClient.createExpress(anyLong(), any(CreateExpressDto.class)))
//                .thenReturn(ResponseEntity.ok("SF1234567890"));
//
//        // ========== 2. 执行测试方法 ==========
//        String waybillId = refundAndReturn.HandleAftersale(true, "");
//
//        // ========== 3. 验证结果 ==========
//        // 断言运单号非空且符合预期
//        assertNotNull(waybillId);
//        assertEquals("SF1234567890", waybillId);
//        // 断言BO对象的运单号已更新
//        assertEquals("SF1234567890", refundAndReturn.getReturnExpress());
//    }
//}
//
//






