package cn.edu.xmu.oomall.aftersale.Dao.bo;

import cn.edu.xmu.oomall.aftersale.feign.ServiceOrderFeignClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对应类图中的maintenance（维修类售后子类）
 * 继承抽象父类Aftersale，重写核心抽象方法实现维修专属逻辑
 * 核心特性：审核同意时调用服务模块创建服务单，关联服务单ID
 */
@Component // 交给Spring管理，方便注入Feign客户端
@Data // 自动生成get/set，继承父类属性的get/set
@NoArgsConstructor // 无参构造
@EqualsAndHashCode(callSuper = true) // 重写equals/hashCode时包含父类属性
@ToString(callSuper = true) // toString时包含父类属性
public class Maintenance extends Aftersale {

    // 维修类专属属性（类图未标注但业务必需）
    private Long serviceOrderId; // 关联的服务单ID（创建服务单后赋值）
    private String serviceType;  // 服务类型：如"上门维修"/"寄修"
    private String technician;   // 维修技师姓名（可选）


    /**
     * 重写父类抽象方法（纯虚函数）：实现维修类售后审核逻辑
     * 核心逻辑：同意审核→调用服务模块创建服务单→更新状态；拒绝审核→仅更新状态
     */
    @Override
    public boolean HandleAftersale(boolean confirm, String reason)
    {
        // 1. 审核拒绝：仅更新状态，无额外逻辑
        if (!confirm) {
            super.SetStatus(false, reason); // 调用父类普通虚方法更新状态
            System.out.println("维修类售后审核拒绝 | 售后单ID：" + getAftersaleId() + "，原因：" + reason);
            return true;
        }

        // 2. 审核同意：调用服务模块创建服务单
        try {
            // 构造创建服务单的参数（适配Feign客户端入参）
            Long shopId = getShopId();
            Long aftersaleId = getAftersaleId();
            // 调用Feign客户端创建服务单（返回服务单ID）
            Long serviceId = serviceOrderFeignClient.createServiceOrder(shopId, aftersaleId, this);

            // 3. 更新维修类专属属性+售后状态
            this.serviceOrderId = serviceId; // 绑定服务单ID
            super.SetStatus(true, reason); // 调用父类方法更新状态
            System.out.println("维修类售后审核同意 | 售后单ID：" + aftersaleId +
                    "，创建服务单ID：" + serviceId + "，服务类型：" + this.serviceType);
            return true;
        } catch (Exception e) {
            System.err.println("维修类售后创建服务单失败 | 售后单ID：" + getAftersaleId() + "，异常：" + e.getMessage());
            return false;
        }
    }

    // 注入Feign客户端：调用服务模块创建服务单（维修类核心依赖）
    @Autowired
    private ServiceOrderFeignClient serviceOrderFeignClient;




    /**
     * 可选重写父类普通虚方法：扩展维修类的商品确认逻辑
     */
    @Override
    public void confirmProduct(boolean confirm, String reason) {
        // 先执行父类通用逻辑
        super.confirmProduct(confirm, reason);
        // 维修类专属扩展：确认商品时校验维修配件是否齐全
        if (confirm) {
            System.out.println("维修类商品确认通过 | 售后单ID：" + getAftersaleId() + "，已校验维修配件齐全");
        }
    }
}