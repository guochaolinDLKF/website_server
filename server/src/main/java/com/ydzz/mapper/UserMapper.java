package com.ydzz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydzz.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper接口
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 更新用户手机号
     * @param userId 用户ID
     * @param phoneCode 新手机号
     * @return 更新影响的行数
     */
    int updatePhoneCode(@Param("userId") Long userId, @Param("phoneCode") String phoneCode);





} 