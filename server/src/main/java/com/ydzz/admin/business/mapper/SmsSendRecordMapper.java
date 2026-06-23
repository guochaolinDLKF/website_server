package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.SmsSendRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信发送记录（zhouyi.sms_send_record，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface SmsSendRecordMapper extends BaseMapper<SmsSendRecord> {
}
