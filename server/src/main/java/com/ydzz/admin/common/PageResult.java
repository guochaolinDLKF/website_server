package com.ydzz.admin.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一分页返回结构
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "当前页")
    private long current;

    @Schema(description = "每页条数")
    private long size;

    @Schema(description = "数据列表")
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, long current, long size, List<T> records) {
        this.total = total;
        this.current = current;
        this.size = size;
        this.records = records;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    public static <T> PageResult<T> of(long total, long current, long size, List<T> records) {
        return new PageResult<>(total, current, size, records);
    }
}
