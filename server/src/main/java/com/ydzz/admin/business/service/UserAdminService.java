package com.ydzz.admin.business.service;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.business.entity.*;
import com.ydzz.admin.business.mapper.*;
import com.ydzz.common.ErrorCode;
import com.ydzz.entity.User;
import com.ydzz.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 用户管理服务（读 zhouyi；禁用/启用为受控写 user.status）。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserAdminService {

    private final ZhouyiUserMapper userMapper;
    private final EightRecordMapper eightRecordMapper;
    private final EightCharNoteMapper eightCharNoteMapper;
    private final FortuneRecordMapper fortuneRecordMapper;
    private final UserTagMapper userTagMapper;
    private final SettingMapper settingMapper;
    private final OrdersMapper ordersMapper;
    private final UserBenefitMapper userBenefitMapper;
    private final SmsSendRecordMapper smsSendRecordMapper;

    public UserAdminService(ZhouyiUserMapper userMapper, EightRecordMapper eightRecordMapper,
                            EightCharNoteMapper eightCharNoteMapper, FortuneRecordMapper fortuneRecordMapper,
                            UserTagMapper userTagMapper, SettingMapper settingMapper, OrdersMapper ordersMapper,
                            UserBenefitMapper userBenefitMapper, SmsSendRecordMapper smsSendRecordMapper) {
        this.userMapper = userMapper;
        this.eightRecordMapper = eightRecordMapper;
        this.eightCharNoteMapper = eightCharNoteMapper;
        this.fortuneRecordMapper = fortuneRecordMapper;
        this.userTagMapper = userTagMapper;
        this.settingMapper = settingMapper;
        this.ordersMapper = ordersMapper;
        this.userBenefitMapper = userBenefitMapper;
        this.smsSendRecordMapper = smsSendRecordMapper;
    }

    public Page<User> page(long current, long size, String keyword, Integer isVip, Integer status,
                           LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(User::getPhoneCode, keyword)
                    .or().like(User::getNickName, keyword)
                    .or().eq(isNumeric(keyword), User::getId, keyword));
        }
        if (isVip != null) {
            qw.eq(User::getIsVip, isVip);
        }
        if (status != null) {
            qw.eq(User::getStatus, status);
        }
        if (startTime != null) {
            qw.ge(User::getCreateTime, startTime);
        }
        if (endTime != null) {
            qw.le(User::getCreateTime, endTime);
        }
        qw.orderByDesc(User::getCreateTime);
        Page<User> page = userMapper.selectPage(new Page<>(current, size), qw);
        page.getRecords().forEach(this::maskPhone);
        return page;
    }

    /** 用户详情聚合 */
    public Map<String, Object> detail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NotFound, "用户不存在");
        }
        maskPhone(user);

        // 逐段容错：zhouyi 各关联表 schema 不确定，单张表查询失败不应导致整个详情 500，
        // 失败段返回空并记录告警，便于按真实表结构定位修正。
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("user", user);
        data.put("eightRecords", safeList("eight_record", () -> eightRecordMapper.selectList(
                new LambdaQueryWrapper<EightRecord>().eq(EightRecord::getUserId, userId))));
        data.put("notes", safeList("eight_char_note", () -> eightCharNoteMapper.selectList(
                new LambdaQueryWrapper<EightCharNote>().eq(EightCharNote::getUserId, userId))));
        data.put("fortunes", safeList("fortune_record", () -> fortuneRecordMapper.selectList(
                new LambdaQueryWrapper<FortuneRecord>().eq(FortuneRecord::getUserId, userId))));
        data.put("tags", safeList("user_tag", () -> userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId))));
        data.put("setting", safeOne("setting", () -> settingMapper.selectById(userId)));
        data.put("orders", safeList("orders", () -> ordersMapper.selectList(
                new LambdaQueryWrapper<Orders>().eq(Orders::getUserId, userId).orderByDesc(Orders::getCreateTime))));
        data.put("benefits", safeList("user_benefits", () -> userBenefitMapper.selectList(
                new LambdaQueryWrapper<UserBenefit>().eq(UserBenefit::getUserId, userId))));
        data.put("smsRecords", safeList("sms_send_record", () -> smsSendRecordMapper.selectList(
                new LambdaQueryWrapper<SmsSendRecord>().eq(SmsSendRecord::getUserId, userId)
                        .orderByDesc(SmsSendRecord::getSendTime))));
        return data;
    }

    /** 容错执行列表查询，失败返回空列表并记录告警 */
    private <T> List<T> safeList(String table, Supplier<List<T>> query) {
        try {
            return query.get();
        } catch (Exception e) {
            log.warn("[用户详情] 查询 {} 失败（已忽略该段）：{}", table, e.getMessage());
            return Collections.emptyList();
        }
    }

    /** 容错执行单条查询，失败返回 null 并记录告警 */
    private <T> T safeOne(String table, Supplier<T> query) {
        try {
            return query.get();
        } catch (Exception e) {
            log.warn("[用户详情] 查询 {} 失败（已忽略该段）：{}", table, e.getMessage());
            return null;
        }
    }

    /** 启用/禁用用户（受控写：仅更新 zhouyi.user.status，不删除、不改其他字段） */
    public void changeStatus(Long userId, Integer status) {
        User exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NotFound, "用户不存在");
        }
        User update = new User();
        update.setId(userId);
        update.setStatus(status);
        userMapper.updateById(update);
    }

    /** 导出数据（返回明文列表，由前端或上层生成文件；此处沿用脱敏策略） */
    public List<User> exportList(String keyword, Integer isVip, Integer status) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(User::getPhoneCode, keyword).or().like(User::getNickName, keyword));
        }
        if (isVip != null) {
            qw.eq(User::getIsVip, isVip);
        }
        if (status != null) {
            qw.eq(User::getStatus, status);
        }
        qw.orderByDesc(User::getCreateTime);
        List<User> list = userMapper.selectList(qw);
        list.forEach(this::maskPhone);
        return list;
    }

    private void maskPhone(User user) {
        if (user != null && StrUtil.isNotBlank(user.getPhoneCode())) {
            user.setPhoneCode(DesensitizedUtil.mobilePhone(user.getPhoneCode()));
        }
    }

    private boolean isNumeric(String s) {
        return s != null && s.chars().allMatch(Character::isDigit);
    }
}
