package com.ydzz.admin.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.admin.business.entity.EightCharNote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 断事笔记（zhouyi.eight_char_note，只读）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Mapper
@DS("zhouyi")
public interface EightCharNoteMapper extends BaseMapper<EightCharNote> {
}
