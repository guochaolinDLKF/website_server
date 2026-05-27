package com.ydzz.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ydzz.common.Result;
import com.ydzz.dto.AccountUpdateRequest;
import com.ydzz.dto.AccountUpdateResponse;
import com.ydzz.dto.LoginRequest;
import com.ydzz.dto.LoginResponse;
import com.ydzz.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author FortuneTelling
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户接口", description = "用户登录、注册、信息管理")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "用户登录", description = "手机号 + 验证码登录")
	@PostMapping("/login")
	public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = userService.loginByPhone(request.getPhoneCode(), request.getVerifyCode());
		return Result.success("登录成功", response);
	}

	@Operation(summary = "通过Token登录", description = "使用已有Token获取最新用户信息")
	@PostMapping("/login_by_token")
	public Result<LoginResponse> loginByToken() {
		LoginResponse response = userService.loginByToken();
		if (response != null) {
			return Result.success("登录成功", response);
		}
		return Result.error(401, "Token无效或已过期");
	}

	@Operation(summary = "用户登出", description = "退出登录")
	@PostMapping("/logout")
	public Result<String> logout() {
		StpUtil.logout();
		return Result.success("登出成功", "用户已登出");
	}

	@Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
	@GetMapping("/info")
	public Result<LoginResponse.UserInfo> getUserInfo() {
		Long userId = StpUtil.getLoginIdAsLong();
		var user = userService.getById(userId);
		if (user == null) {
			return Result.error("用户不存在");
		}
		// 构建 UserInfo
		LoginResponse.UserInfo info = new LoginResponse.UserInfo();
		info.setPhoneCode(user.getPhoneCode());
		info.setNickName(user.getNickName());
		info.setGender(user.getGender());
		info.setBirthTime(user.getBirthTime());
		info.setBirthArea(user.getBirthArea());
		info.setBirthCity(user.getBirthCity());
		info.setBirthProvince(user.getBirthProvince());
		info.setIsVip(user.getIsVip());
		info.setNotes(user.getNotes());
		info.setAvatar(user.getAvatar());
		info.setStatus(user.getStatus());
		return Result.success(info);
	}

	@Operation(summary = "更新账户信息", description = "更新当前用户的信息")
	@PostMapping("/update")
	public Result<AccountUpdateResponse> updateAccount(@RequestBody AccountUpdateRequest request) {
		Long userId = StpUtil.getLoginIdAsLong();
		AccountUpdateResponse result = userService.updateAccount(userId, request);
		if (result != null) {
			return Result.success("账户信息更新成功", result);
		}
		return Result.error("账户信息更新失败");
	}

	@Operation(summary = "注销账户", description = "注销当前账户")
	@PostMapping("/logoff")
	public Result<String> accountLogoff() {
		Long userId = StpUtil.getLoginIdAsLong();
		Boolean result = userService.accountLogoff(userId, null, "用户主动注销");
		StpUtil.logout();
		return Result.success("账户注销成功", "账户已注销");
	}
}
