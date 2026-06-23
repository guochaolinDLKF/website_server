package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品/付费功能（zhouyi.items）。读 + 受控编辑价格/描述。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface ItemMapper extends BaseMapper<Item> {
}
