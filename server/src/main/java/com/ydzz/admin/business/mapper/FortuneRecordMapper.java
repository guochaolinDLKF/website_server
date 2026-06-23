package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.FortuneRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运势记录（zhouyi.fortune_record，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface FortuneRecordMapper extends BaseMapper<FortuneRecord> {
}
