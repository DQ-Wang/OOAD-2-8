package com.xmu.service.service;

import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.assembler.DeliveryServiceOrderBuilder;
import com.xmu.service.Dao.assembler.OnsiteServiceOrderBuilder;
import com.xmu.service.Dao.assembler.ServiceOrderBuilder;
import com.xmu.service.Dao.bo.DeliveryServiceOrder;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.openfeign.ExpressClient;
import com.xmu.service.service.vo.ServiceOrderVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ServiceOrderService 测试类
 * 使用真实数据库，使用 @MockitoBean 模拟 ExpressClient
 */
@SpringBootTest
@Transactional
@Rollback
public class ServiceOrderServiceTest {

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceOrderDao serviceOrderDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 使用 @MockitoBean 模拟 ExpressClient（货运模块）
     */
    @MockitoBean
    private ExpressClient expressClient;

    private final Long testShopId = 1L;
    private final Long testAfterSaleId = 1L;
    private final Long testProviderId = 1L;
    private final Long testWorkerId = 1L;


    @BeforeEach
    public void setUp() {
        // 初始化构建器（需要在测试前初始化 ServiceOrder 的构建器映射表）
        List<ServiceOrderBuilder> builders = Arrays.asList(
                new OnsiteServiceOrderBuilder(),
                new DeliveryServiceOrderBuilder()
        );
        ServiceOrder.initBuilders(builders);

        // 重置 Mock 对象
        reset(expressClient);
        
        // 禁用外键约束检查（因为 Express 模块未实现，无法插入真实数据）
        // 这样可以测试 service 模块的逻辑，而不需要依赖 Express 表
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        } catch (Exception e) {
            // 如果禁用失败，忽略错误（可能数据库不支持或权限不足）
        }
    }

    @Test
    public void testCreateServiceOrder_Delivery() {
        // 准备测试数据
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("配送地址");
        dto.setDescription("配送描述");

        // 执行测试（使用真实数据库）
        ServiceOrderVo result = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getId()); // 数据库自动生成的ID
        assertEquals(ServiceOrder.TYPE_DELIVERY, result.getType());
        assertEquals("DELIVERY", result.getTypeName());

        // 验证 ExpressClient 未被调用（创建时不需要运单）
        verify(expressClient, never()).createSendWaybill(anyLong());
    }

    @Test
    public void testAcceptServiceOrder_Delivery_CreatesSendWaybill() {
        // 先创建一个配送服务单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("配送地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();

        // 配置 Mock：当调用 createSendWaybill 时返回运单ID
        // 注意：由于 Express 模块未实现，我们使用 @MockitoBean 模拟 ExpressClient
        // 外键约束检查已在 setUp() 中禁用，所以不需要插入真实的 express 记录
        Long mockWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrderId)).thenReturn(mockWaybillId);

        // 执行测试（ServiceOrderService 会自动设置 ExpressClient）
        ServiceOrderVo result = serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(testProviderId, result.getServiceProviderId());
        assertEquals(ServiceOrder.STATUS_ACCEPT, result.getStatus());
        assertEquals(mockWaybillId, result.getExpressId()); // 验证运单ID已设置

        // 验证 ExpressClient 被调用
        verify(expressClient, times(1)).createSendWaybill(serviceOrderId);
    }

    @Test
    public void testFinishServiceOrder_Delivery_CreatesReturnWaybill() {
        // 先创建配送服务单并完成流程
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("配送地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();
        
        // 配置 Mock：接受时创建寄件运单
        Long sendWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrderId)).thenReturn(sendWaybillId);
        
        // 接受服务单（ServiceOrderService 会自动设置 ExpressClient）
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);
        
        // 收件
        serviceOrderService.receiveDelivery(testProviderId, serviceOrderId);
        
        // 分配工人
        serviceOrderService.assignToWorker(testProviderId, testWorkerId, serviceOrderId);

        // 配置 Mock：完成时创建反件运单
        Long returnWaybillId = 200L;
        when(expressClient.createReturnWaybill(serviceOrderId)).thenReturn(returnWaybillId);

        // 执行测试（完成服务单，ServiceOrderService 会自动设置 ExpressClient）
        ServiceOrderVo result = serviceOrderService.finishServiceOrder(testWorkerId, serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(ServiceOrder.STATUS_FINISH, result.getStatus());
        assertEquals(returnWaybillId, result.getExpressId()); // 验证反件运单ID已设置

        // 验证 ExpressClient 被调用
        verify(expressClient, times(1)).createReturnWaybill(serviceOrderId);
    }

    @Test
    public void testCancelServiceOrder_Delivery_AfterAccept_CancelsWaybill() {
        // 先创建配送服务单并接受
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("配送地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();
        
        // 配置 Mock：接受时创建寄件运单
        Long sendWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrderId)).thenReturn(sendWaybillId);
        
        // 接受服务单（ServiceOrderService 会自动设置 ExpressClient）
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        // 配置 Mock：取消时取消运单
        doNothing().when(expressClient).cancelWaybill(sendWaybillId);

        // 执行测试（在 STATUS_ACCEPT 状态下取消，ServiceOrderService 会自动设置 ExpressClient）
        ServiceOrderVo result = serviceOrderService.cancelServiceOrder(serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(ServiceOrder.STATUS_CANCEL, result.getStatus());

        // 验证 ExpressClient 被调用：取消寄件运单
        verify(expressClient, times(1)).cancelWaybill(sendWaybillId);
        verify(expressClient, never()).createReturnWaybill(anyLong());
    }

    @Test
    public void testCancelServiceOrder_Delivery_AfterReceive_CreatesReturnWaybill() {
        // 先创建配送服务单并完成到 RECEIVE 状态
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("配送地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();
        
        // 配置 Mock：接受时创建寄件运单
        Long sendWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrderId)).thenReturn(sendWaybillId);
        
        // 接受服务单（ServiceOrderService 会自动设置 ExpressClient）
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);
        
        // 收件
        serviceOrderService.receiveDelivery(testProviderId, serviceOrderId);
        
        // 分配工人
        serviceOrderService.assignToWorker(testProviderId, testWorkerId, serviceOrderId);

        // 配置 Mock：取消时创建反件运单
        Long returnWaybillId = 200L;
        when(expressClient.createReturnWaybill(serviceOrderId)).thenReturn(returnWaybillId);

        // 执行测试（在 STATUS_RECEIVE 状态下取消，ServiceOrderService 会自动设置 ExpressClient）
        ServiceOrderVo result = serviceOrderService.cancelServiceOrder(serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(ServiceOrder.STATUS_CANCEL, result.getStatus());
        assertEquals(returnWaybillId, result.getExpressId()); // 验证反件运单ID已设置

        // 验证 ExpressClient 被调用：创建反件运单
        verify(expressClient, times(1)).createReturnWaybill(serviceOrderId);
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    @Test
    public void testAcceptServiceOrder_OnSite_NoWaybill() {
        // 先创建一个上门服务单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("张三");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();

        // 执行测试
        ServiceOrderVo result = serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(testProviderId, result.getServiceProviderId());
        assertEquals(ServiceOrder.STATUS_ACCEPT, result.getStatus());

        // 验证 ExpressClient 未被调用（上门服务不需要运单）
        verify(expressClient, never()).createSendWaybill(anyLong());
        verify(expressClient, never()).createReturnWaybill(anyLong());
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    @Test
    public void testCancelServiceOrder_OnSite_NoWaybill() {
        // 先创建一个上门服务单并接受
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("张三");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        
        ServiceOrderVo created = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = created.getId();
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        // 执行测试
        ServiceOrderVo result = serviceOrderService.cancelServiceOrder(serviceOrderId);

        // 验证结果
        assertNotNull(result);
        assertEquals(serviceOrderId, result.getId());
        assertEquals(ServiceOrder.STATUS_CANCEL, result.getStatus());

        // 验证 ExpressClient 未被调用（上门服务不需要运单）
        verify(expressClient, never()).cancelWaybill(anyLong());
        verify(expressClient, never()).createReturnWaybill(anyLong());
    }
}

