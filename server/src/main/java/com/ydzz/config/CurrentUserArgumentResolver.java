package com.ydzz.config;

import com.ydzz.annotation.CurrentUser;
import com.ydzz.entity.User;
import com.ydzz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 解析@CurrentUser注解的参数解析器
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) 
               && parameter.getParameterType().equals(User.class);
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // 直接从缓存获取当前用户信息
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        return userService.getUserFromCache(userId);
    }
}
