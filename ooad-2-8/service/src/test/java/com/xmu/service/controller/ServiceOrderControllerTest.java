package com.xmu.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmu.service.controller.dto.AppointmentDto;
import com.xmu.service.controller.dto.ServiceOrderDto;
import com.xmu.service.Dao.factory.DeliveryServiceOrderFactory;
import com.xmu.service.Dao.factory.OnsiteServiceOrderFactory;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.openfeign.ExpressClient;
import com.xmu.service.service.ServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ServiceOrderController 测试类
 * 使用 MockMvc 测试 Controller 层的所有 API 端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ServiceOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private ExpressClient expressClient;

    private final Long testShopId = 1L;
    private final Long testAfterSaleId = 1L;
    private final Long testProviderId = 1L;
    private final Long testWorkerId = 1L;

    @BeforeEach
    void setUp() {
        // 初始化 ServiceOrder 的构建器
        List<com.xmu.service.Dao.factory.ServiceOrderFactory> factories = Arrays.asList(
                new OnsiteServiceOrderFactory(),
                new DeliveryServiceOrderFactory()
        );
        ServiceOrder.initBuilders(factories);
        
        // 禁用外键约束检查（因为 Express 模块未实现，无法插入真实数据）
        // 这样可以测试 service 模块的逻辑，而不需要依赖 Express 表
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        } catch (Exception e) {
            // 如果禁用失败，忽略错误（可能数据库不支持或权限不足）
        }
    }

    /**
     * 测试创建服务单（上门服务）
     */
    @Test
    void testCreateOnSiteServiceOrder() throws Exception {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0); // 上门服务
        dto.setServiceConsignee("张三");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        mockMvc.perform(post("/internal/shops/{shopId}/aftersales/{id}/serviceorders", testShopId, testAfterSaleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
    }

    /**
     * 测试创建服务单（寄件服务）
     */
    @Test
    void testCreateDeliveryServiceOrder() throws Exception {
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 1); // 寄件服务
        dto.setServiceConsignee("李四");
        dto.setServiceMobile("13900139000");
        dto.setAddress("测试地址2");
        dto.setDescription("测试描述2");

        // Mock ExpressClient
        // 注意：由于 Express 模块未实现，我们使用 @MockitoBean 模拟 ExpressClient
        // 外键约束检查已在 setUp() 中禁用，所以不需要插入真实的 express 记录
        Long mockWaybillId = 1001L;
        when(expressClient.createSendWaybill(anyLong())).thenReturn(mockWaybillId);

        mockMvc.perform(post("/internal/shops/{shopId}/aftersales/{id}/serviceorders", testShopId, testAfterSaleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
    }

    /**
     * 测试服务商接单
     */
    @Test
    void testAcceptServiceOrder() throws Exception {
        // 先创建一个服务单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();

        mockMvc.perform(post("/serviceproviders/{did}/services/{id}/accept", testProviderId, serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试完成服务单
     */
    @Test
    void testFinishServiceOrder() throws Exception {
        // 先创建一个服务单并接单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);
        serviceOrderService.assignToWorker(testProviderId, testWorkerId, serviceOrderId);
        serviceOrderService.appointment(testWorkerId, serviceOrderId, new AppointmentDto());

        mockMvc.perform(post("/workers/{workerId}/services/{id}/finish", testWorkerId, serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试取消服务单
     */
    @Test
    void testCancelServiceOrder() throws Exception {
        // 先创建一个服务单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();

        mockMvc.perform(put("/services/{id}/cancel", serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试服务商收件（寄件服务）
     */
    @Test
    void testReceiveDelivery() throws Exception {
        // 先创建一个寄件服务单并接单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 1);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        // Mock ExpressClient
        // 注意：由于 Express 模块未实现，我们使用 @MockitoBean 模拟 ExpressClient
        // 外键约束检查已在 setUp() 中禁用，所以不需要插入真实的 express 记录
        Long mockWaybillId = 1001L;
        when(expressClient.createSendWaybill(anyLong())).thenReturn(mockWaybillId);

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        mockMvc.perform(post("/serviceproviders/{did}/services/{id}/receive", testProviderId, serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试预约上门服务
     */
    @Test
    void testAppointment() throws Exception {
        // 先创建一个上门服务单并接单、指派
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);
        serviceOrderService.assignToWorker(testProviderId, testWorkerId, serviceOrderId);

        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setAppointmentTime(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/workers/{did}/service/{id}/appointment", testWorkerId, serviceOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试指派维修工
     */
    @Test
    void testDispatch() throws Exception {
        // 先创建一个服务单并接单
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setType((byte) 0);
        dto.setServiceConsignee("测试");
        dto.setServiceMobile("13800138000");
        dto.setAddress("测试地址");
        dto.setDescription("测试描述");

        com.xmu.service.service.vo.ServiceOrderVo serviceOrderVo = serviceOrderService.createServiceOrder(testShopId, testAfterSaleId, dto);
        Long serviceOrderId = serviceOrderVo.getId();
        serviceOrderService.acceptServiceOrder(testProviderId, serviceOrderId);

        mockMvc.perform(post("/serviceproviders/{did}/services/{id}/dispatch", testProviderId, serviceOrderId)
                        .param("workerId", testWorkerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试创建服务单 - 缺少必需字段
     * 注意：由于 Controller 在 com.xmu.service.controller 包中，不在 cn.edu.xmu..controller 包中，
     * ControllerAspect 不会处理 BusinessException，异常会直接抛出。
     * MockMvc 在遇到未处理的异常时会抛出异常。
     */
    @Test
    void testCreateServiceOrder_MissingFields() {
        ServiceOrderDto dto = new ServiceOrderDto();
        // 不设置 type

        // 由于异常没有被处理，MockMvc 会抛出异常
        Exception exception = assertThrows(
                Exception.class,
                () -> mockMvc.perform(post("/internal/shops/{shopId}/aftersales/{id}/serviceorders", testShopId, testAfterSaleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().is5xxServerError())
        );
        
        // 验证异常原因是否是 BusinessException
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof cn.edu.xmu.javaee.core.exception.BusinessException ||
                (cause != null && cause.getCause() instanceof cn.edu.xmu.javaee.core.exception.BusinessException));
    }

    /**
     * 测试接单 - 服务单不存在
     * 注意：由于 Controller 在 com.xmu.service.controller 包中，不在 cn.edu.xmu..controller 包中，
     * ControllerAspect 不会处理 BusinessException，异常会直接抛出。
     * MockMvc 在遇到未处理的异常时会抛出异常。
     */
    @Test
    void testAcceptServiceOrder_NotFound() {
        Long nonExistentId = 99999L;

        // 由于异常没有被处理，MockMvc 会抛出异常
        Exception exception = assertThrows(
                Exception.class,
                () -> mockMvc.perform(post("/serviceproviders/{did}/services/{id}/accept", testProviderId, nonExistentId))
                        .andExpect(status().is5xxServerError())
        );
        
        // 验证异常原因是否是 BusinessException
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof cn.edu.xmu.javaee.core.exception.BusinessException ||
                (cause != null && cause.getCause() instanceof cn.edu.xmu.javaee.core.exception.BusinessException));
    }
}

