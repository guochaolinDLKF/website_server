package com.ydzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydzz.dto.LoginResponse;
import com.ydzz.dto.AccountUpdateRequest;
import com.ydzz.dto.AccountUpdateResponse;
import com.ydzz.entity.User;

/**
 * 用户服务接口
 * 
 * @author FortuneTelling
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

	/**
	 * 手机号 + 验证码登录
	 */
	LoginResponse loginByPhone(String phoneCode, String verifyCode);

	/**
	 * 用户注册（手机号制）
	 */
	boolean register(User user);

	/**
	 * 根据手机号查询用户
	 */
	User getByPhone(String phoneCode);

	/**
	 * 修改昵称等非敏感信息
	 */
	boolean updateNickName(Long userId, String nickName);

	/**
	 * 从缓存中获取用户信息
	 */
	User getUserFromCache(Long userId);

	/**
	 * 更新缓存中的用户信息
	 */
	void updateUserCache(User user);

	/**
	 * 删除缓存中的用户信息
	 */
	void deleteUserCache(Long userId);

	/**
	 * 账户注销
	 */
	Boolean accountLogoff(Long userId, String verifyCode, String reason);

	/**
	 * 更新用户账户信息
	 */
	AccountUpdateResponse updateAccount(Long userId, AccountUpdateRequest request);

	/**
	 * 通过Token登录
	 */
	LoginResponse loginByToken();
}
