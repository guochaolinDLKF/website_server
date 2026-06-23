package com.ydzz.admin.log;

import java.lang.annotation.*;

/**
 * 后台操作日志注解。标注在后台 Controller 方法上，由 {@link OperationLogAspect} 记录。
 *
 * @author WebsiteServer
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 所属模块，如「用户管理」 */
    String module() default "";

    /** 操作描述，如「禁用用户」 */
    String operation() default "";

    /** 是否记录请求参数 */
    boolean saveParam() default true;

    /** 是否记录响应结果 */
    boolean saveResult() default false;
}
