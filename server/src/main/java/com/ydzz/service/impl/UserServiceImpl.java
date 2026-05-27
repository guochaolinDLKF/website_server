package com.ydzz.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheRefresh;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydzz.config.CustomTokenParser;
import com.ydzz.common.ErrorCode;
import com.ydzz.dto.AccountUpdateRequest;
import com.ydzz.dto.AccountUpdateResponse;
import com.ydzz.dto.LoginResponse;
import com.ydzz.entity.User;
import com.ydzz.enums.UserGender;
import com.ydzz.exception.BusinessException;
import com.ydzz.mapper.UserMapper;
import com.ydzz.service.UserService;
import com.ydzz.util.BusinessAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@Service("userService")
@Primary
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CustomTokenParser customTokenParser;

	/**
	 * 手机号 + 验证码登录（验证码校验由业务具体实现）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LoginResponse loginByPhone(String phoneCode, String verifyCode) {
		BusinessAssert.notBlank(phoneCode, ErrorCode.PhoneIsNull);

		// 查询用户
		User user = applicationContext.getBean(UserServiceImpl.class).getByPhone(phoneCode);
		boolean isNewUser = false;

		if (user == null) {
			user = createDefaultUser(phoneCode);
			isNewUser = true;
		}

		if (isNewUser) {
			save(user);
		}

		// Sa-Token 登录
		StpUtil.login(user.getId());
		String originalToken = StpUtil.getTokenValue();
		String encryptedToken = customTokenParser.encryptToken(originalToken);

		// 缓存用户信息
		cacheUserInfo(user);

		log.info("用户登录成功，手机号: {}, 用户ID: {}", phoneCode, user.getId());

		return buildLoginResponse(encryptedToken, user);
	}

	@Override
	public boolean register(User user) {
		if (user == null || StrUtil.isBlank(user.getPhoneCode())) {
			return false;
		}

		User existingUser = applicationContext.getBean(UserServiceImpl.class).getByPhone(user.getPhoneCode());
		BusinessAssert.isFalse(existingUser != null, ErrorCode.PhoneAlreadyHas);

		if (StrUtil.isBlank(user.getNickName())) {
			user.setNickName("用户" + System.currentTimeMillis() % 100000);
		}

		boolean success = save(user);
		if (success) {
			log.info("用户注册成功，用户ID: {}, 手机号: {}", user.getId(), user.getPhoneCode());
		}
		return success;
	}

	@Override
	@Cached(name = "userByPhoneCache", key = "#phoneCode", expire = 3600)
	@CacheRefresh(refresh = 2400)
	public User getByPhone(String phoneCode) {
		if (StrUtil.isBlank(phoneCode)) {
			return null;
		}

		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getPhoneCode, phoneCode);
		User user = getOne(wrapper);

		if (user != null) {
			log.debug("从数据库查询用户信息，手机号: {}", phoneCode);
		}
		return user;
	}

	@Override
	public boolean updateNickName(Long userId, String nickName) {
		if (userId == null || StrUtil.isBlank(nickName)) {
			return false;
		}

		User user = getById(userId);
		if (user == null) {
			return false;
		}

		user.setNickName(nickName);
		boolean success = updateById(user);

		if (success) {
			updateUserCache(user);
		}
		return success;
	}

	@Cached(name = "userInfoCache", key = "#user.id", expire = 3600)
	@CacheRefresh(refresh = 2400)
	public User cacheUserInfo(User user) {
		log.debug("用户信息已缓存，用户ID: {}", user.getId());
		return user;
	}

	@Override
	@Cached(name = "userInfoCache", key = "#userId", expire = 3600)
	@CacheRefresh(refresh = 2400)
	public User getUserFromCache(Long userId) {
		if (userId == null) {
			return null;
		}
		User user = getById(userId);
		if (user != null) {
			log.debug("从数据库查询用户信息，用户ID: {}", userId);
		}
		return user;
	}

	@Override
	@CacheUpdate(name = "userInfoCache", key = "#user.id", value = "#user")
	public void updateUserCache(User user) {
		if (user != null && user.getId() != null) {
			log.debug("更新用户缓存，用户ID: {}", user.getId());
		}
	}

	@Override
	@CacheInvalidate(name = "userInfoCache", key = "#userId")
	public void deleteUserCache(Long userId) {
		if (userId != null) {
			log.debug("删除用户缓存，用户ID: {}", userId);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean accountLogoff(Long userId, String verifyCode, String reason) {
		log.info("开始执行账户注销，用户ID: {}", userId);

		User user = getById(userId);
		if (user == null) {
			log.warn("账户注销失败，用户不存在，用户ID: {}", userId);
			return false;
		}

		int deleteCount = baseMapper.deleteById(userId);
		if (deleteCount <= 0) {
			log.error("账户注销失败，用户ID: {}", userId);
			return false;
		}

		deleteUserCache(userId);
		log.info("账户注销成功，用户ID: {}", userId);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AccountUpdateResponse updateAccount(Long userId, AccountUpdateRequest request) {
		User existingUser = getById(userId);
		if (existingUser == null) {
			log.error("用户不存在，用户ID: {}", userId);
			return null;
		}

		User updateUser = new User();
		updateUser.setId(userId);
		updateUser.setNickName(request.getName());
		updateUser.setGender(request.getGender() ? 1 : 0);
		updateUser.setBirthTime(request.getBirthTime());
		updateUser.setBirthArea(request.getBirthArea());
		updateUser.setBirthCity(request.getBirthCity());
		updateUser.setBirthProvince(request.getBirthProvince());
		updateUser.setIsVip(request.getIsVip() ? 1 : 0);
		updateUser.setUpdateTime(LocalDateTime.now());

		boolean success = updateById(updateUser);
		if (success) {
			deleteUserCache(userId);
			log.info("用户信息更新成功，用户ID: {}", userId);

			AccountUpdateResponse response = new AccountUpdateResponse();
			response.setId(updateUser.getId());
			response.setPhone(existingUser.getPhoneCode());
			response.setNickName(updateUser.getNickName());
			response.setGender(updateUser.getGender() == 1);
			response.setBirthTime(updateUser.getBirthTime());
			response.setBirthArea(updateUser.getBirthArea());
			response.setBirthCity(updateUser.getBirthCity());
			response.setBirthProvince(updateUser.getBirthProvince());
			response.setIsVip(updateUser.getIsVip() == 1);
			response.setNotes(existingUser.getNotes());
			response.setAvatar(existingUser.getAvatar());
			response.setStatus(existingUser.getStatus());
			return response;
		} else {
			log.error("用户信息更新失败，用户ID: {}", userId);
			return null;
		}
	}

	@Override
	public LoginResponse loginByToken() {
		if (!StpUtil.isLogin()) {
			log.warn("Token无效或已过期");
			return null;
		}

		Long userId = StpUtil.getLoginIdAsLong();
		log.info("通过Token登录，用户ID: {}", userId);

		User user = getUserFromCache(userId);
		if (user == null) {
			log.warn("用户信息不存在，用户ID: {}", userId);
			return null;
		}

		String originalToken = StpUtil.getTokenValue();
		String encryptedToken = customTokenParser.encryptToken(originalToken);

		return buildLoginResponse(encryptedToken, user);
	}

	/**
	 * 创建默认新用户
	 */
	private User createDefaultUser(String phoneCode) {
		User user = new User();
		user.setPhoneCode(phoneCode);
		user.setNickName("用户" + phoneCode.substring(7));
		user.setGender(UserGender.MALE.getCode());
		user.setBirthTime(0L);
		user.setBirthArea("");
		user.setBirthCity("");
		user.setBirthProvince("");
		user.setIsVip(0);
		user.setAvatar("");
		user.setStatus(1);
		return user;
	}

	/**
	 * 构建登录响应
	 */
	private LoginResponse buildLoginResponse(String token, User user) {
		LoginResponse response = new LoginResponse();
		response.setToken(token);

		LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
		userInfo.setPhoneCode(user.getPhoneCode());
		userInfo.setNickName(user.getNickName());
		userInfo.setGender(user.getGender());
		userInfo.setBirthTime(user.getBirthTime());
		userInfo.setBirthArea(user.getBirthArea());
		userInfo.setBirthCity(user.getBirthCity());
		userInfo.setBirthProvince(user.getBirthProvince());
		userInfo.setIsVip(user.getIsVip());
		userInfo.setNotes(user.getNotes());
		userInfo.setAvatar(user.getAvatar());
		userInfo.setStatus(user.getStatus());

		response.setUserInfo(userInfo);
		return response;
	}
}
