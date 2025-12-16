package com.xmu.service.mapper;

import com.xmu.service.mapper.po.ServiceOrderPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;


/**
 * 服务单Mapper（数据库操作）
 */
@Mapper
public interface ServiceOrderMapper {
    @Insert("INSERT INTO service_order (" +
            "service_sn, service_consignee, service_mobile, address, type, status, " +
            "create_time, problem_image_url, description, product_id, express_id, customer_id, " +
            "shop_id, aftersales_id" + // 新增：关联商铺ID和售后单ID（实验核心关联字段）
            ") VALUES (" +
            "#{serviceSn}, #{serviceConsignee}, #{serviceMobile}, #{address}, #{type}, #{status}, " +
            "#{createTime}, #{problemImageUrl}, #{description}, #{productId}, #{expressId}, #{customerId}, " +
            "#{shopId}, #{aftersalesId}" + // 对应新增字段的参数占位符
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(ServiceOrderPo po);
}
