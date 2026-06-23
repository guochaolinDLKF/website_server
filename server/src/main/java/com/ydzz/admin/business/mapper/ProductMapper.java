package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * IAP 商品（zhouyi.products，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface ProductMapper extends BaseMapper<Product> {
}
