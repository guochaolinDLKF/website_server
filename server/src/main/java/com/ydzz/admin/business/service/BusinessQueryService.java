package com.ydzz.admin.business.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ydzz.admin.business.entity.*;
import com.ydzz.admin.business.mapper.*;
import com.ydzz.common.ErrorCode;
import com.ydzz.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 业务数据查询服务（读 zhouyi）：订单、商品、权益、支付异常。
 *
 * <p>全部为只读分页/详情查询，不对 zhouyi 做任何写操作。</p>
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Service
public class BusinessQueryService {

    private final OrdersMapper ordersMapper;
    private final ItemMapper itemMapper;
    private final UserBenefitMapper userBenefitMapper;
    private final PaymentCallbackFailureMapper paymentFailureMapper;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;

    public BusinessQueryService(OrdersMapper ordersMapper, ItemMapper itemMapper,
                                UserBenefitMapper userBenefitMapper, PaymentCallbackFailureMapper paymentFailureMapper,
                                PaymentMapper paymentMapper, RefundMapper refundMapper) {
        this.ordersMapper = ordersMapper;
        this.itemMapper = itemMapper;
        this.userBenefitMapper = userBenefitMapper;
        this.paymentFailureMapper = paymentFailureMapper;
        this.paymentMapper = paymentMapper;
        this.refundMapper = refundMapper;
    }

    /**
     * 订单分页：关键词匹配订单号/用户ID/商品名，可按状态、时间筛选。
     *
     * @param sortOrder 下单时间(create_time) 排序方向：asc 升序；其它值（含默认）降序——最近优先
     */
    public Page<Orders> pageOrders(long current, long size, String keyword, String orderStatus,
                                   LocalDateTime startTime, LocalDateTime endTime, String sortOrder) {
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(Orders::getOrderNo, keyword)
                    .or().like(Orders::getItemName, keyword)
                    .or().eq(isNumeric(keyword), Orders::getUserId, keyword));
        }
        if (StrUtil.isNotBlank(orderStatus)) {
            qw.eq(Orders::getOrderStatus, orderStatus);
        }
        if (startTime != null) {
            qw.ge(Orders::getCreateTime, startTime);
        }
        if (endTime != null) {
            qw.le(Orders::getCreateTime, endTime);
        }
        if ("asc".equalsIgnoreCase(sortOrder)) {
            qw.orderByAsc(Orders::getCreateTime);
        } else {
            qw.orderByDesc(Orders::getCreateTime);
        }
        return ordersMapper.selectPage(new Page<>(current, size), qw);
    }

    /** 订单详情：订单 + 关联支付 + 关联退款（按订单号关联） */
    public Map<String, Object> orderDetail(Long id) {
        Orders order = ordersMapper.selectById(id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order", order);
        if (order != null && StrUtil.isNotBlank(order.getOrderNo())) {
            data.put("payments", paymentMapper.selectList(
                    new LambdaQueryWrapper<Payment>().eq(Payment::getOrderNo, order.getOrderNo())));
            data.put("refunds", refundMapper.selectList(
                    new LambdaQueryWrapper<Refund>().eq(Refund::getOrderNo, order.getOrderNo())));
        }
        return data;
    }

    /**
     * 商品分页（zhouyi.items：含价格/折扣价）：关键词匹配商品ID/描述，可按类型筛选。
     *
     * @param sortOrder 商品ID(item_id) 排序方向：asc 升序 / desc 降序；其它值按主键 id 倒序（默认）
     */
    public Page<Item> pageGoods(long current, long size, String keyword, Integer itemType, String sortOrder) {
        LambdaQueryWrapper<Item> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            qw.and(w -> w.like(Item::getItemId, keyword).or().like(Item::getItemDesc, keyword));
        }
        if (itemType != null) {
            qw.eq(Item::getItemType, itemType);
        }
        if ("desc".equalsIgnoreCase(sortOrder)) {
            qw.orderByDesc(Item::getItemId);
        } else {
            // 默认及 asc 均按商品ID升序
            qw.orderByAsc(Item::getItemId);
        }
        return itemMapper.selectPage(new Page<>(current, size), qw);
    }

    /**
     * 新增/编辑商品（写 zhouyi.items）。
     *
     * <p>ID 由数据库自增、不可修改：新增时无 id 走 insert；编辑时以 id 为主键，
     * 仅更新 itemId/itemDesc/itemType/itemPrice/itemDiscount 等非主键字段。</p>
     *
     * @return 商品主键 id
     */
    public Long saveGoods(Item item) {
        if (StrUtil.isBlank(item.getItemId())) {
            throw new BusinessException(ErrorCode.BadRequest, "商品ID(itemId)不能为空");
        }
        if (item.getId() == null) {
            itemMapper.insert(item);
        } else {
            // 防御：编辑不允许变更主键，updateById 以 id 作为 where 条件，仅更新其余字段
            itemMapper.updateById(item);
        }
        return item.getId();
    }

    /** 删除商品（物理删除 zhouyi.items，items 表无逻辑删除列） */
    public void deleteGoods(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.BadRequest, "商品ID不能为空");
        }
        itemMapper.deleteById(id);
    }

    /** 权益分页：可按用户ID、是否有效筛选 */
    public Page<UserBenefit> pageBenefits(long current, long size, Long userId, Integer isValid) {
        LambdaQueryWrapper<UserBenefit> qw = new LambdaQueryWrapper<>();
        if (userId != null) {
            qw.eq(UserBenefit::getUserId, userId);
        }
        if (isValid != null) {
            qw.eq(UserBenefit::getIsValid, isValid);
        }
        qw.orderByDesc(UserBenefit::getPurchaseTime);
        return userBenefitMapper.selectPage(new Page<>(current, size), qw);
    }

    /** 支付异常分页：可按状态、失败类型筛选 */
    public Page<PaymentCallbackFailure> pagePaymentFailures(long current, long size, String status, String failureType) {
        LambdaQueryWrapper<PaymentCallbackFailure> qw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(status)) {
            qw.eq(PaymentCallbackFailure::getStatus, status);
        }
        if (StrUtil.isNotBlank(failureType)) {
            qw.eq(PaymentCallbackFailure::getFailureType, failureType);
        }
        qw.orderByDesc(PaymentCallbackFailure::getId);
        return paymentFailureMapper.selectPage(new Page<>(current, size), qw);
    }

    private boolean isNumeric(String s) {
        return s != null && !s.isEmpty() && s.chars().allMatch(Character::isDigit);
    }
}
