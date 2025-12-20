package cn.edu.xmu.oomall.aftersale.controller.dto;

/**
 * 创建服务单请求DTO
 */
public class CreateServiceOrderDto {

    private String name;


    private String mobile;


    private String address;


    private Integer type;

    private String problemImageUrl;
    private String description;
    private Long productId;
    private Long customerId;
    private Long expressId;

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getProblemImageUrl() {
        return problemImageUrl;
    }

    public void setProblemImageUrl(String problemImageUrl) {
        this.problemImageUrl = problemImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getExpressId() {
        return expressId;
    }

    public void setExpressId(Long expressId) {
        this.expressId = expressId;
    }
}