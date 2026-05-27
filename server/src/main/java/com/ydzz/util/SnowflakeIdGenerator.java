package com.ydzz.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * ID生成器
 * 
 * 分布式环境下生成唯一ID
 * 机器ID通过Redis自增获取（01-99），仅在机器重启时获取一次
 * 格式：ORDER_年月日时分秒_两位机器码_随机数（总共32位）
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@Component
public class SnowflakeIdGenerator implements CommandLineRunner {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 机器ID最大值（99）
    private static final long MAX_MACHINE_NUM = 99;
    
    // 机器ID（从Redis获取，01-99，仅在启动时获取一次）
    private String machineId; // 格式化为两位字符串，如 "01", "02"
    
    // 随机数生成器
    private final Random random = new Random();
    
    // 时间格式化器：年月日时分秒
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * 应用启动完成后从Redis获取机器ID（两位数字，01-99）
     * 仅在启动时获取一次，后续不再获取
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // 从Redis获取机器ID（自增，范围01-99）
            Long machineIdFromRedis = stringRedisTemplate.opsForValue().increment("snowflake:machine:id");
            // 限制在01-99范围内（循环使用）
            long machineIdNum = (machineIdFromRedis % MAX_MACHINE_NUM);
            if (machineIdNum == 0) {
                machineIdNum = MAX_MACHINE_NUM; // 如果余数为0，使用99
            }
            // 格式化为两位字符串，左边补零
            this.machineId = String.format("%02d", machineIdNum);
            log.info("ID生成器初始化成功，机器ID: {} (从Redis获取，仅启动时获取一次)", this.machineId);
        } catch (Exception e) {
            // Redis不可用时，使用随机数作为机器ID（01-99）
            long randomMachineId = (long) (Math.random() * MAX_MACHINE_NUM) + 1;
            this.machineId = String.format("%02d", randomMachineId);
            log.warn("Redis不可用，使用随机机器ID: {} (仅启动时获取一次)", this.machineId);
        }
    }
    
    /**
     * 获取当前时间的年月日时分秒格式
     * 格式：yyyyMMddHHmmss（14位）
     * 
     * @return 年月日时分秒字符串
     */
    private String getCurrentDateTimeStr() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
    
    /**
     * 生成8位随机数（用于确保唯一性）
     * 
     * @return 8位随机数字符串（00000000-99999999）
     */
    private String generateRandomNumber() {
        // 生成0-99999999之间的随机数，格式化为8位（左边补零）
        int randomNum = random.nextInt(100000000); // 0-99999999
        return String.format("%08d", randomNum);
    }
    
    /**
     * 生成10位随机数（用于确保唯一性）
     * 
     * @return 10位随机数字符串（0000000000-9999999999）
     */
    private String generateRandomNumber10() {
        // 生成0-9999999999之间的随机数，格式化为10位（左边补零）
        // 使用long类型避免int溢出
        long randomNum = (long) (Math.random() * 10000000000L); // 0-9999999999
        return String.format("%010d", randomNum);
    }
    
    /**
     * 生成订单号
     * 格式：ORDER_年月日时分秒_两位机器码_随机数（总共32位）
     * 例如：ORDER_20241109153025_01_12345678
     * 
     * 计算：ORDER_(6) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(8) = 32位
     * 
     * @return 订单号（32位）
     */
    public String generateOrderNo() {
        String dateTimeStr = getCurrentDateTimeStr(); // 年月日时分秒（14位）
        String randomNum = generateRandomNumber(); // 8位随机数
        // ORDER_(6) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(8) = 32位
        String orderNo = String.format("ORDER_%s_%s_%s", dateTimeStr, machineId, randomNum);
        // 验证长度（确保是32位）
        if (orderNo.length() != 32) {
            log.warn("订单号长度异常: {} (期望32位，实际{}位)", orderNo, orderNo.length());
        }
        return orderNo;
    }
    
    /**
     * 生成支付单号
     * 格式：PAY_年月日时分秒_两位机器码_随机数（总共32位）
     * 例如：PAY_20241109153025_01_1234567890
     * 
     * 计算：PAY_(4) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(10) = 32位
     * 
     * @return 支付单号（32位）
     */
    public String generatePaymentNo() {
        String dateTimeStr = getCurrentDateTimeStr(); // 年月日时分秒（14位）
        String randomNum10 = generateRandomNumber10(); // 10位随机数
        
        // PAY_(4) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(10) = 32位
        String paymentNo = String.format("PAY_%s_%s_%s", dateTimeStr, machineId, randomNum10);
        
        // 验证长度（确保是32位）
        if (paymentNo.length() != 32) {
            log.warn("支付单号长度异常: {} (期望32位，实际{}位)", paymentNo, paymentNo.length());
        }
        
        return paymentNo;
    }
    
    /**
     * 生成退款单号
     * 格式：REF_年月日时分秒_两位机器码_随机数（总共32位）
     * 例如：REF_20241109153025_01_1234567890
     * 
     * 计算：REF_(4) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(10) = 32位
     * 
     * @return 退款单号（32位）
     */
    public String generateRefundNo() {
        String dateTimeStr = getCurrentDateTimeStr(); // 年月日时分秒（14位）
        String randomNum10 = generateRandomNumber10(); // 10位随机数
        
        // REF_(4) + 年月日时分秒(14) + _(1) + 机器码(2) + _(1) + 随机数(10) = 32位
        String refundNo = String.format("REF_%s_%s_%s", dateTimeStr, machineId, randomNum10);
        
        // 验证长度（确保是32位）
        if (refundNo.length() != 32) {
            log.warn("退款单号长度异常: {} (期望32位，实际{}位)", refundNo, refundNo.length());
        }
        
        return refundNo;
    }
}
