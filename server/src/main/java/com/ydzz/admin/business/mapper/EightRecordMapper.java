package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.EightRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户八字记录（zhouyi.eight_record，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface EightRecordMapper extends BaseMapper<EightRecord> {
}
