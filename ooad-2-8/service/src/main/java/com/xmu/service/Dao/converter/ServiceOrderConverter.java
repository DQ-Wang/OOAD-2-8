package com.xmu.service.Dao.converter;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import com.xmu.service.Dao.bo.ServiceOrder;
import com.xmu.service.mapper.po.ServiceOrderPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 服务单类型转换器
 * 负责 BO（业务对象）和 PO（持久化对象）之间的类型转换
 */
@Slf4j
@Component
public class ServiceOrderConverter {

    /**
     * 将 PO 转换为 BO（在 build 方法中使用）
     * 处理需要类型转换的字段：id (Long->String), type (Byte->String), status (Byte->String)
     */
    public void convertPoToBo(ServiceOrderPo po, ServiceOrder bo) {
        // id 字段：Long -> String
        if (po.getId() != null) {
            bo.setId(String.valueOf(po.getId()));
        }
        // type 字段：Byte -> String
        if (po.getType() != null) {
            bo.setType(convertTypeByteToString(po.getType()));
        }
        // status 字段：Byte -> String
        if (po.getStatus() != null) {
            bo.setStatus(convertStatusByteToString(po.getStatus()));
        }
    }

    /**
     * 将 BO 转换为 PO（在 update/insert 方法中使用）
     * 处理需要类型转换的字段：type (String->Byte), status (String->Byte)
     * 注意：id 字段在 update 方法中已单独处理，这里不转换 id
     */
    public void convertBoToPo(ServiceOrder bo, ServiceOrderPo po) {
        // type 字段：String -> Byte
        if (bo.getType() != null) {
            po.setType(convertTypeStringToByte(bo.getType()));
        }
        // status 字段：String -> Byte
        if (bo.getStatus() != null) {
            po.setStatus(convertStatusStringToByte(bo.getStatus()));
        }
    }

    /**
     * 将 BO 转换为 PO（在 insert 方法中使用，包含 id 转换）
     * 处理需要类型转换的字段：id (String->Long), type (String->Byte), status (String->Byte)
     */
    public void convertBoToPoWithId(ServiceOrder bo, ServiceOrderPo po) {
        // id 字段：String -> Long
        if (bo.getId() != null) {
            try {
                po.setId(Long.parseLong(bo.getId()));
            } catch (NumberFormatException e) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                        String.format("服务单ID格式错误: %s", bo.getId()));
            }
        }
        // 调用普通转换方法处理 type 和 status
        convertBoToPo(bo, po);
    }

    /**
     * type: Byte -> String
     * 0 -> "ONSITE", 1 -> "DELIVERY"
     */
    public String convertTypeByteToString(Byte type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case 0 -> ServiceOrder.TYPE_ON_SITE;
            case 1 -> ServiceOrder.TYPE_DELIVERY;
            default -> throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderConverter: unknown type byte " + type);
        };
    }

    /**
     * type: String -> Byte
     * "ONSITE" -> 0, "DELIVERY" -> 1
     */
    public Byte convertTypeStringToByte(String type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case ServiceOrder.TYPE_ON_SITE -> (byte) 0;
            case ServiceOrder.TYPE_DELIVERY -> (byte) 1;
            default -> throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,
                    "ServiceOrderConverter: unknown type string " + type);
        };
    }

    /**
     * status: Byte -> String
     * 0 -> "NEW", 1 -> "ASSIGN", 2 -> "CANCEL", 3 -> "ONDOOR", 4 -> "RECEIVE", 5 -> "PROGRESS", 6 -> "FINISH"
     */
    public String convertStatusByteToString(Byte status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case 0 -> "NEW";
            case 1 -> "ASSIGN";
            case 2 -> "CANCEL";
            case 3 -> "ONDOOR";
            case 4 -> "RECEIVE";
            case 5 -> "PROGRESS";
            case 6 -> "FINISH";
            default -> {
                log.warn("【ServiceOrderConverter】未知状态字节: {}", status);
                yield "NEW";
            }
        };
    }

    /**
     * status: String -> Byte
     * "NEW" -> 0, "ASSIGN" -> 1, "CANCEL" -> 2, "ONDOOR" -> 3, "RECEIVE" -> 4, "PROGRESS" -> 5, "FINISH" -> 6
     */
    public Byte convertStatusStringToByte(String status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case "NEW" -> (byte) 0;
            case "ASSIGN" -> (byte) 1;
            case "CANCEL" -> (byte) 2;
            case "ONDOOR" -> (byte) 3;
            case "RECEIVE" -> (byte) 4;
            case "PROGRESS" -> (byte) 5;
            case "FINISH" -> (byte) 6;
            default -> {
                log.warn("【ServiceOrderConverter】未知状态: {}", status);
                yield (byte) 0;
            }
        };
    }
}

