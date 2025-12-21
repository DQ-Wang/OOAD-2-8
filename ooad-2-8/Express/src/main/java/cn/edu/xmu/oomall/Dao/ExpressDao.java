package cn.edu.xmu.oomall.Dao;

import cn.edu.xmu.oomall.mapper.ExpressMapper;
import cn.edu.xmu.oomall.mapper.po.ExpressPo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 售后单DAO层实现类
 * 封装JPA Mapper的调用，统一处理数据访问细节（空值、分页、异常），对外暴露业务友好的方法
 * 注：DAO层仅负责数据访问，不包含任何业务逻辑
 */
@Repository
@RefreshScope
@RequiredArgsConstructor
@Slf4j
public class ExpressDao
{
    // 注入JPA Mapper接口（Spring自动生成代理类）
    private final ExpressMapper expressMapper;
    //private final Map<Byte, AfterSaleBuilder> builders;

    /**
     * 新增运单（返回持久化后的实体，包含自增ID）
     * @param expressPo 运单实体
     * @return 持久化后的运单实体（含主键ID）
     */
    public ExpressPo insertExpress(ExpressPo expressPo) {
        // 校验参数（可选，防止空值入库）
        if (expressPo == null) {
            throw new IllegalArgumentException("运单实体不能为空");
        }
        if (expressPo.getExpressId().isEmpty()) {
            throw new IllegalArgumentException("运单号不能为空");
        }

        // JPA的save方法：新增时会插入数据并返回持久化对象；更新时会修改数据
        ExpressPo savedPo = expressMapper.save(expressPo);
        log.info("【运单DAO】新增运单成功，运单ID：{}，运单号：{}", savedPo.getExpressId());
        return savedPo;
    }

    /**
     * 按主键ID删除运单
     * @param id 运单主键ID（数据库自增ID）
     */
    public void deleteExpress(String id) {
        // 校验参数
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("运单ID不能为空");
        }

        // 先校验运单是否存在（可选，避免删除不存在的数据时无提示）
        if (!expressMapper.existsById(id)) {
            throw new EntityNotFoundException("运单不存在，ID：" + id);
        }

        // JPA的deleteById方法：按主键删除
        expressMapper.deleteById(id);
        log.info("【运单DAO】删除运单成功，运单ID：{}", id);
    }
}
