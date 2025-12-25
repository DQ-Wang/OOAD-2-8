package com.xmu.service.Dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.ServiceOrderDao;
import com.xmu.service.Dao.assembler.DeliveryServiceOrderBuilder;
import com.xmu.service.Dao.assembler.OnsiteServiceOrderBuilder;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.openfeign.ExpressClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ServiceOrder BO 层分支覆盖测试
 * 专门测试各种异常分支和边界情况
 */
@SpringBootTest
@Transactional
@Rollback
public class ServiceOrderBranchTest {

    @Autowired
    private ServiceOrderDao serviceOrderDao;

    @MockitoBean
    private ExpressClient expressClient;

    private final Long testShopId = 1L;
    private final Long testAfterSaleId = 1L;
    private final Long testProviderId = 1L;
    private final Long testWorkerId = 1L;
    private final Long wrongProviderId = 999L;
    private final Long wrongWorkerId = 999L;

    @BeforeEach
    void setUp() {
        List<com.xmu.service.Dao.assembler.ServiceOrderBuilder> builders = Arrays.asList(
                new OnsiteServiceOrderBuilder(),
                new DeliveryServiceOrderBuilder()
        );
        ServiceOrder.initBuilders(builders);
        reset(expressClient);
    }

    // ========== ServiceOrder.create() 异常分支测试 ==========

    /**
     * 测试 ServiceOrder.create() - builders 未初始化
     */
    @Test
    void testCreate_BuildersNotInitialized() {
        // 使用反射清空 builders（因为 initBuilders 不接受 null）
        try {
            java.lang.reflect.Field field = ServiceOrder.class.getDeclaredField("builders");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            fail("无法清空 builders: " + e.getMessage());
        }

        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ServiceOrder.create(testShopId, testAfterSaleId, dto);
        });

        assertEquals(ReturnNo.INTERNAL_SERVER_ERR, exception.getErrno());
    }

    /**
     * 测试 ServiceOrder.create() - dto 为 null
     */
    @Test
    void testCreate_DtoIsNull() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ServiceOrder.create(testShopId, testAfterSaleId, null);
        });

        assertEquals(ReturnNo.FIELD_NOTVALID, exception.getErrno());
    }

    /**
     * 测试 ServiceOrder.create() - type 为 null
     */
    @Test
    void testCreate_TypeIsNull() {
        ServiceOrderDto dto = new ServiceOrderDto();
        // 不设置 type

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ServiceOrder.create(testShopId, testAfterSaleId, dto);
        });

        assertEquals(ReturnNo.FIELD_NOTVALID, exception.getErrno());
    }

    /**
     * 测试 ServiceOrder.create() - 未知类型
     */
    @Test
    void testCreate_UnknownType() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 99); // 未知类型

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ServiceOrder.create(testShopId, testAfterSaleId, dto);
        });

        assertEquals(ReturnNo.FIELD_NOTVALID, exception.getErrno());
    }

    // ========== acceptByProvider() 异常分支测试 ==========

    /**
     * 测试 acceptByProvider() - 非 NEW 状态
     */
    @Test
    void testAcceptByProvider_NotNewStatus() {
        // 创建一个服务单并接单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);

        // 再次尝试接单（应该失败）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.acceptByProvider(testProviderId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    // ========== assign() 异常分支测试 ==========

    /**
     * 测试 assign() - providerId 不匹配
     */
    @Test
    void testAssign_ProviderIdMismatch() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);

        // 使用错误的 providerId 指派
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.assign(wrongProviderId, testWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 assign() - 状态不正确（非 ASSIGN）- NEW 状态
     */
    @Test
    void testAssign_StatusNotAssign_NewStatus() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        // 不接单，直接指派（应该失败，状态是 NEW）
        // 需要先设置 serviceProviderId，否则会先检查 providerId 不匹配
        serviceOrder.setServiceProviderId(testProviderId);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.assign(testProviderId, testWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
        assertTrue(exception.getMessage().contains("当前状态不可派工"));
    }

    /**
     * 测试 assign() - 状态不正确（非 RECEIVE）- ACCEPT 状态
     */
    @Test
    void testAssign_StatusNotReceive_AcceptStatus() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId); // 状态变为 ACCEPT
        
        // 在 ACCEPT 状态下尝试指派（应该失败，因为需要 RECEIVE 状态）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.assign(testProviderId, testWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
        assertTrue(exception.getMessage().contains("当前状态不可派工"));
    }

    // ========== finish() 异常分支测试 ==========

    /**
     * 测试 finish() - 状态不正确（非 PROGRESS，对于上门服务）
     */
    @Test
    void testFinish_StatusNotProgress_OnSite() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);
        serviceOrder.assign(testProviderId, testWorkerId);
        // 不预约，直接完成（应该失败）

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.finish(testWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 finish() - workerId 不匹配
     */
    @Test
    void testFinish_WorkerIdMismatch() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);
        serviceOrder.assign(testProviderId, testWorkerId);
        serviceOrder.doAppoint(testWorkerId, LocalDateTime.now().plusDays(1));

        // 使用错误的 workerId 完成
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.finish(wrongWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 finish() - 状态不正确（非 RECEIVE，对于寄件服务）
     */
    @Test
    void testFinish_StatusNotReceive_Delivery() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId); // NEW -> ACCEPT
        serviceOrder.doReceive(testProviderId); // ACCEPT -> RECEIVE
        // 不分配工人，直接完成（应该失败，因为状态是 RECEIVE，不是 PROGRESS）
        // 需要先设置 workerId，否则 finish 方法会先检查 workerId 不匹配
        serviceOrder.setWorkerId(testWorkerId);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.finish(testWorkerId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    // ========== cancel() 异常分支测试 ==========

    /**
     * 测试 cancel() - 已完成状态不可取消
     */
    @Test
    void testCancel_FinishedStatus() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);
        serviceOrder.assign(testProviderId, testWorkerId);
        serviceOrder.doAppoint(testWorkerId, LocalDateTime.now().plusDays(1));
        serviceOrder.finish(testWorkerId);

        // 尝试取消已完成的服务单（应该失败）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.cancel();
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    // ========== DeliveryServiceOrder 分支测试 ==========

    /**
     * 测试 DeliveryServiceOrder.acceptByProvider() - expressClient 为 null
     */
    @Test
    void testDeliveryAcceptByProvider_ExpressClientNull() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        // insert() 不会注入 expressClient（只在 build() 时注入），所以这里显式设置为 null 以确保测试 expressClient 为 null 的情况
        serviceOrder.setExpressClient(null);

        // 应该不会抛出异常，只是不会创建运单
        assertDoesNotThrow(() -> {
            serviceOrder.acceptByProvider(testProviderId);
        });

        // expressId 可能为 null 或 0（数据库默认值）
        assertTrue(serviceOrder.getExpressId() == null || serviceOrder.getExpressId() == 0);
        verify(expressClient, never()).createSendWaybill(anyLong());
    }

    /**
     * 测试 DeliveryServiceOrder.cancel() - expressClient 为 null
     */
    @Test
    void testDeliveryCancel_ExpressClientNull() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrder.setExpressClient(null);
        serviceOrder.setExpressId(null); // 确保 expressId 为 null
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);

        // 应该不会抛出异常，只是不会取消运单
        assertDoesNotThrow(() -> {
            serviceOrder.cancel();
        });

        assertEquals(ServiceOrder.STATUS_CANCEL, serviceOrder.getStatus());
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    /**
     * 测试 DeliveryServiceOrder.cancel() - expressId 为 null（STATUS_ACCEPT 状态）
     */
    @Test
    void testDeliveryCancel_ExpressIdNull_AfterAssign() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrder.setExpressClient(expressClient);
        serviceOrderDao.insert(serviceOrder);
        // 手动设置状态为 ASSIGN，但不设置 expressId（模拟 acceptByProvider 时 expressClient 为 null 的情况）
        serviceOrder.acceptByProvider(testProviderId);
        serviceOrder.setExpressId(null); // 设置为 null

        // 应该不会抛出异常，只是不会取消运单（因为 expressId 为 null）
        assertDoesNotThrow(() -> {
            serviceOrder.cancel();
        });

        assertEquals(ServiceOrder.STATUS_CANCEL, serviceOrder.getStatus());
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    /**
     * 测试 DeliveryServiceOrder.cancel() - 其他状态（非 ASSIGN、RECEIVE、PROGRESS）
     */
    @Test
    void testDeliveryCancel_OtherStatus() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrder.setExpressClient(expressClient);
        serviceOrderDao.insert(serviceOrder);
        // 保持在 NEW 状态

        // 应该不会抛出异常，只是不会调用 expressClient
        assertDoesNotThrow(() -> {
            serviceOrder.cancel();
        });

        assertEquals(ServiceOrder.STATUS_CANCEL, serviceOrder.getStatus());
        verify(expressClient, never()).cancelWaybill(anyLong());
        verify(expressClient, never()).createReturnWaybill(anyLong());
    }

    /**
     * 测试 DeliveryServiceOrder.cancel() - STATUS_RECEIVE 状态下取消
     * 应该生成反件运单
     */
    @Test
    void testDeliveryCancel_StatusReceive() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrder.setExpressClient(expressClient);
        serviceOrderDao.insert(serviceOrder);
        
        // 设置 Mock：接受时创建寄件运单
        Long sendWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrder.getId())).thenReturn(sendWaybillId);
        serviceOrder.acceptByProvider(testProviderId); // NEW -> ACCEPT
        serviceOrder.doReceive(testProviderId); // ACCEPT -> RECEIVE
        
        // 设置 Mock：取消时创建反件运单
        Long returnWaybillId = 200L;
        when(expressClient.createReturnWaybill(serviceOrder.getId())).thenReturn(returnWaybillId);

        // 在 RECEIVE 状态下取消，应该生成反件运单
        assertDoesNotThrow(() -> {
            serviceOrder.cancel();
        });

        assertEquals(ServiceOrder.STATUS_CANCEL, serviceOrder.getStatus());
        assertEquals(returnWaybillId, serviceOrder.getExpressId());
        verify(expressClient, times(1)).createSendWaybill(serviceOrder.getId());
        verify(expressClient, times(1)).createReturnWaybill(serviceOrder.getId());
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    /**
     * 测试 DeliveryServiceOrder.cancel() - STATUS_PROGRESS 状态下取消
     * 应该生成反件运单
     * 
     * 注意：DeliveryServiceOrder 没有 doAppoint 方法，所以无法通过正常流程到达 PROGRESS 状态
     * 这里我们手动设置状态为 PROGRESS 来测试 cancel() 方法中的这个分支
     */
    @Test
    void testDeliveryCancel_StatusProgress() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrder.setExpressClient(expressClient);
        serviceOrderDao.insert(serviceOrder);
        
        // 设置 Mock：接受时创建寄件运单
        Long sendWaybillId = 100L;
        when(expressClient.createSendWaybill(serviceOrder.getId())).thenReturn(sendWaybillId);
        serviceOrder.acceptByProvider(testProviderId); // NEW -> ACCEPT
        serviceOrder.doReceive(testProviderId); // ACCEPT -> RECEIVE
        serviceOrder.assign(testProviderId, testWorkerId); // RECEIVE -> PROGRESS
        
        // 设置 Mock：取消时创建反件运单
        Long returnWaybillId = 200L;
        when(expressClient.createReturnWaybill(serviceOrder.getId())).thenReturn(returnWaybillId);

        // 在 PROGRESS 状态下取消，应该生成反件运单
        assertDoesNotThrow(() -> {
            serviceOrder.cancel();
        });

        assertEquals(ServiceOrder.STATUS_CANCEL, serviceOrder.getStatus());
        assertEquals(returnWaybillId, serviceOrder.getExpressId());
        verify(expressClient, times(1)).createSendWaybill(serviceOrder.getId());
        verify(expressClient, times(1)).createReturnWaybill(serviceOrder.getId());
        verify(expressClient, never()).cancelWaybill(anyLong());
    }

    // ========== OnSiteServiceOrder 分支测试 ==========

    /**
     * 测试 OnSiteServiceOrder.doAppoint() - workerId 不匹配
     */
    @Test
    void testOnSiteDoAppoint_WorkerIdMismatch() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);
        serviceOrder.assign(testProviderId, testWorkerId);

        // 使用错误的 workerId 预约
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doAppoint(wrongWorkerId, LocalDateTime.now().plusDays(1));
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 OnSiteServiceOrder.doAppoint() - 状态不正确（非 ONDOOR）
     */
    @Test
    void testOnSiteDoAppoint_StatusNotOndoor() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId);
        // 不指派，直接预约（应该失败）
        // 需要先设置 workerId，否则会 NPE
        serviceOrder.setWorkerId(testWorkerId);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doAppoint(testWorkerId, LocalDateTime.now().plusDays(1));
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 OnSiteServiceOrder.doReceive() - 不支持
     */
    @Test
    void testOnSiteDoReceive_NotSupported() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_ON_SITE);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doReceive(testProviderId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    // ========== DeliveryServiceOrder 分支测试 ==========

    /**
     * 测试 DeliveryServiceOrder.doReceive() - providerId 不匹配
     */
    @Test
    void testDeliveryDoReceive_ProviderIdMismatch() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        DeliveryServiceOrder serviceOrder = (DeliveryServiceOrder) ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        serviceOrder.acceptByProvider(testProviderId); // NEW -> ACCEPT

        // 使用错误的 providerId 收件（应该失败，因为 providerId 不匹配）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doReceive(wrongProviderId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 DeliveryServiceOrder.doReceive() - 状态不正确（非 ACCEPT）
     */
    @Test
    void testDeliveryDoReceive_StatusNotAssign() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);
        // 不接单，直接收件（应该失败）
        // 需要先设置 serviceProviderId，否则会 NPE
        serviceOrder.setServiceProviderId(testProviderId);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doReceive(testProviderId);
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }

    /**
     * 测试 DeliveryServiceOrder.doAppoint() - 不支持
     */
    @Test
    void testDeliveryDoAppoint_NotSupported() {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType(ServiceOrder.TYPE_DELIVERY);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");

        ServiceOrder serviceOrder = ServiceOrder.create(testShopId, testAfterSaleId, dto);
        serviceOrderDao.insert(serviceOrder);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            serviceOrder.doAppoint(testWorkerId, LocalDateTime.now().plusDays(1));
        });

        assertEquals(ReturnNo.STATENOTALLOW, exception.getErrno());
    }
}

