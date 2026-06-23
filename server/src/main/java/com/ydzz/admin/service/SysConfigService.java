package com.ydzz.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.entity.SysConfig;
import com.ydzz.admin.mapper.SysConfigMapper;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务（键值型）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    public SysConfigService(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    public Page<SysConfig> page(long current, long size, String keyword, String group) {
        LambdaQueryWrapper<SysConfig> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(SysConfig::getConfigKey, keyword)
                    .or().like(SysConfig::getConfigName, keyword));
        }
        if (StrUtil.isNotBlank(group)) {
            qw.eq(SysConfig::getConfigGroup, group);
        }
        qw.orderByDesc(SysConfig::getUpdateTime);
        return sysConfigMapper.selectPage(new Page<>(current, size), qw);
    }

    public Long save(SysConfig req, String updateBy) {
        req.setUpdateBy(updateBy);
        if (req.getId() == null) {
            LambdaQueryWrapper<SysConfig> qw = new LambdaQueryWrapper<>();
            qw.eq(SysConfig::getConfigKey, req.getConfigKey());
            if (sysConfigMapper.selectCount(qw) > 0) {
                throw new BusinessException(ErrorCode.Conflict, "配置键已存在");
            }
            sysConfigMapper.insert(req);
        } else {
            sysConfigMapper.updateById(req);
        }
        return req.getId();
    }

    public void removeById(Long id) {
        sysConfigMapper.deleteById(id);
    }
}
