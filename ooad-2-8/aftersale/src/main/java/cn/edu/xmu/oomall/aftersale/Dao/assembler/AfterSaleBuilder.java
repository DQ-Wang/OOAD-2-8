package cn.edu.xmu.oomall.aftersale.Dao.assembler;

import cn.edu.xmu.oomall.aftersale.Dao.AfterSaleDao;
import cn.edu.xmu.oomall.aftersale.Dao.bo.AfterSale;
import cn.edu.xmu.oomall.aftersale.mapper.po.AfterSalePo;


public interface AfterSaleBuilder {
    /**
    * 该构建器支持的售后单类型
    */
    Byte getType();

    /**
     * 将 Po 转成具体的 AfterSale 子类
     */
    AfterSale build(AfterSalePo po, AfterSaleDao dao);
    }
