package com.xmu.service.Dao.bo;


import com.xmu.service.controller.dto.CreateServiceOrderDto;
import com.xmu.service.mapper.po.ServiceOrderPo;
import java.util.Date;
import java.util.UUID;

/**
 * 服务单领域业务对象（充血模型）
 */
public class ServiceOrderBo {
    private Long id;
    private String serviceSn;
    private String serviceConsignee;
    private String serviceMobile;
    private String address;
    private Integer type;
    private Integer status;
    private Date createTime;
    private String problemImageUrl;
    private String description;
    private Long productId;
    private Long expressId;
    private Long customerId;
    private Long shopId;       // 对应API路径中的{shopId}，关联商铺
    private Long aftersalesId;

    /**
     * 用请求DTO初始化BO
     */
    public ServiceOrderBo(CreateServiceOrderDto dto, Long shopId, Long aftersalesId) {
        // 初始化属性
        this.serviceConsignee = dto.getName();
        this.serviceMobile = dto.getMobile();
        this.address = dto.getAddress();
        this.type = dto.getType();
        this.problemImageUrl = dto.getProblemImageUrl();
        this.description = dto.getDescription();
        this.productId = dto.getProductId();
        this.customerId = dto.getCustomerId();
        this.expressId = dto.getExpressId();
        this.shopId = shopId;
        this.aftersalesId = aftersalesId;

        // 业务规则：默认状态+生成服务单号
        this.status = 0; // 0-待处理
        this.createTime = new Date();
        this.serviceSn = generateServiceSn(shopId, aftersalesId);
    }

    /**
     * 生成唯一服务单号（业务规则封装）
     */
    private String generateServiceSn(Long shopId, Long aftersalesId) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000); // 秒级时间戳，缩短单号长度
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6); // 6位随机字符串
        return String.format("SERV_%s_%s_%s_%s", shopId, aftersalesId, timestamp, random);
    }

    /**
     * 校验必填信息（业务规则封装）
     */
    public void validate() {
        // 1. 校验关联字段（shopId、aftersalesId）
        if (this.shopId == null || this.shopId <= 0) {
            throw new IllegalArgumentException("关联商铺ID无效（不可为空或负数），无法创建服务单");
        }
        if (this.aftersalesId == null || this.aftersalesId <= 0) {
            throw new IllegalArgumentException("关联售后单ID无效（不可为空或负数），无法创建服务单");
        }

    }


    /**
     * BO转PO（数据持久化准备）
     */
    public ServiceOrderPo toPo() {
        ServiceOrderPo po = new ServiceOrderPo();
        po.setId(this.id);
        po.setServiceSn(this.serviceSn);
        po.setServiceConsignee(this.serviceConsignee);
        po.setServiceMobile(this.serviceMobile);
        po.setAddress(this.address);
        po.setType(this.type);
        po.setStatus(this.status);
        po.setCreateTime(this.createTime);
        po.setProblemImageUrl(this.problemImageUrl);
        po.setDescription(this.description);
        po.setProductId(this.productId);
        po.setExpressId(this.expressId);
        po.setCustomerId(this.customerId);

        po.setShopId(this.shopId);
        po.setAftersalesId(this.aftersalesId);
        return po;
    }

    // 必要的Getter/Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceSn() {
        return serviceSn;
    }

    public Long getAftersalesId() {
        return aftersalesId; // 暴露售后单ID，便于Service层日志打印关联关系
    }
}