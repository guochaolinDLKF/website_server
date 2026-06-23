package com.xiaomayi.system.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.entity.Param;
import com.xiaomayi.system.service.ParamService;
import lombok.experimental.UtilityClass;

/**
 * <p>
 * 参数解析工具类
 * 参数工具命名规则，借鉴Spring官方MultipartResolver的命名方式，因此此处命名为：DictResolver
 * 特别备注：
 * 1、@UtilityClass 注解用于创建一个具有静态方法的实用工具类，通常用于声明不可实例化的工具类。
 * 2、Lombok 会在编译时自动生成一些常见的实用方法，如静态工厂方法、静态常量等
 * 3、final类
 * 4、自动生成一个私有无参构造函数
 * 5、方法、内部类、变量 标记成statis
 * 6、static import时必须带*号
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-12
 */
@UtilityClass
public class ParamResolver {

    /**
     * 根据参数编码获取信息
     *
     * @param paramCode    参数编码
     * @param defaultValue 默认值
     * @return 返回结果
     */
    public String getParamValue(String paramCode, String defaultValue) {
        // 参数判空
        Assert.isTrue(StringUtils.isNotBlank(paramCode), "参数编码不能为空");
        // 获取参数数据Bean对象
        ParamService paramService = SpringUtil.getBean(ParamService.class);
        // 根据参数编码获取参数值
        Param param = paramService.getParamInfo(paramCode);
        if (StringUtils.isNull(param)) {
            return defaultValue;
        }
        // 参数值
        String paramValue = param.getValue();
        // 参数值
        if (StringUtils.isEmpty(paramValue)) {
            return defaultValue;
        }
        // 返回结果
        return paramValue;
    }

}
