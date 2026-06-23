// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2020~2025 小蚂蚁云团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁云团队 团队荣誉出品
// +----------------------------------------------------------------------
// | 版权和免责声明:
// | 本团队对该软件框架产品拥有知识产权（包括但不限于商标权、专利权、著作权、商业秘密等）
// | 均受到相关法律法规的保护，任何个人、组织和单位不得在未经本团队书面授权的情况下对所授权
// | 软件框架产品本身申请相关的知识产权，被授权主体务必妥善保管官方所授权的软件产品源码，禁
// | 止以任何形式对外泄露(包括但不限于分享、开源、网络平台),禁止用于任何违法、侵害他人合法
// | 权益等恶意的行为，禁止用于任何违反我国法律法规的一切项目研发，任何个人、组织和单位用于
// | 项目研发而产生的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其造成的损失 (包括
// | 但不限于直接、间接、附带或衍生的损失等)，本团队不承担任何法律责任，本软件框架禁止任何
// | 单位、组织、个人用于任何违法、侵害他人合法利益等恶意的行为，如有发现违规、违法的犯罪行
// | 为，本团队将无条件配合公安机关调查取证同时保留一切以法律手段起诉的权利，本软件框架只能
// | 用于公司和个人内部的法律所允许的合法合规的软件产品研发，详细声明内容请阅读《框架免责声
// | 明》附件；
// +----------------------------------------------------------------------

package com.xiaomayi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.entity.Config;
import com.xiaomayi.system.entity.ConfigItem;
import com.xiaomayi.system.mapper.ConfigItemMapper;
import com.xiaomayi.system.mapper.ConfigMapper;
import com.xiaomayi.system.service.ConfigItemService;
import com.xiaomayi.system.service.ConfigWebService;
import com.xiaomayi.system.vo.configweb.ConfigDataItemVO;
import com.xiaomayi.system.vo.configweb.ConfigDataVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统配置 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
@AllArgsConstructor
public class ConfigWebServiceImpl implements ConfigWebService {

    private final ConfigMapper configMapper;
    private final ConfigItemMapper configItemMapper;
    private final ConfigItemService configItemService;

    /**
     * 获取系统配置
     *
     * @return 返回结果
     */
    @Override
    public R index() {
        // 获取系统配置数据
        List<Config> configList = configMapper.selectList(new LambdaQueryWrapper<Config>()
                .eq(Config::getDelFlag, 0)
                .orderByAsc(Config::getSort));
        // 实例化配置对象VO列表
        List<ConfigDataVO> configDataVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(configList)) {
            // 遍历配置数据
            for (Config config : configList) {
                // 实例化配置对象
                ConfigDataVO configDataVO = new ConfigDataVO();
                configDataVO.setConfigId(config.getId());
                configDataVO.setConfigName(config.getName());
                // 获取指定配置的配置项数据
                List<ConfigItem> configItemList = configItemMapper.selectList(new LambdaQueryWrapper<ConfigItem>()
                        // 配置项状态：1-正常 2-停用
                        .eq(ConfigItem::getStatus, 1)
                        .eq(ConfigItem::getConfigId, config.getId())
                        .eq(ConfigItem::getDelFlag, 0)
                        .orderByAsc(ConfigItem::getSort));
                // 实例化配置项数据VO列表
                List<ConfigDataItemVO> configDataItemVOList = new ArrayList<>();
                // 配置项数据判空
                if (StringUtils.isNotEmpty(configItemList)) {
                    // 遍历配置项数据
                    for (ConfigItem item : configItemList) {
                        // 实例化配置项数据VO
                        ConfigDataItemVO configDataItemVO = new ConfigDataItemVO();
                        BeanUtils.copyProperties(item, configDataItemVO);

                        // 选项类型
                        String itemType = item.getType();
                        // 选项值
                        String itemValue = item.getValue();

                        // 条件处理
                        String[] strings = new String[]{"array", "radio", "checkbox", "select", "selects"};
                        // 数组转列表
                        List<String> paramList = Arrays.asList(strings);
                        // 判断当前参数是否在列表中
                        if (paramList.contains(itemType)) {
                            // 选择项值，案例如性别，具体值为：1=男,2=女,3=保密
                            String options = item.getOptions();
                            if (StringUtils.isNotEmpty(options)) {
                                // 中文逗号治安英文
                                String[] optionsList = options.replace("，", ",").split(",");
                                if (StringUtils.isNotEmpty(optionsList)) {
                                    Map<Integer, String> map = new HashMap<>();
                                    // 遍历选项值
                                    for (String option : optionsList) {
                                        String[] strings1 = option.split("=");
                                        map.put(Integer.valueOf(strings1[0]), strings1[1]);
                                    }
                                    configDataItemVO.setParam(map);
                                }
                            }
                        }
                        if (StringUtils.isNotEmpty(itemValue)) {
                            // 复选框
                            if ("checkbox".equals(itemType) || "selects".equals(itemType)) {
                                String[] valueList = itemValue.split(",");
                                configDataItemVO.setValueList(Arrays.asList(valueList));
                            } else if ("image".equals(itemType)) {
                                // 单图处理
                                configDataItemVO.setValue(CommonUtils.getFileURL(item.getValue()));
                            } else if ("images".equals(itemType)) {
                                // 多图处理
                                String[] stringList = itemValue.split(",");
                                // 集合数字转列表并拼接域名
                                List<String> valueList = Arrays.stream(stringList).map(CommonUtils::getFileURL).toList();
                                // 赋值给值对象
                                configDataItemVO.setValueList(valueList);
                            } else if ("file".equals(itemType) || "files".equals(itemType)) {
                                // 文件处理
                                String[] stringList = itemValue.split(",");
                                // 集合对象转列表
                                List<String> valueList = Arrays.stream(stringList).toList();
                                // 数据集处理
                                valueList = valueList.stream()
                                        // 过滤掉null空元素
                                        .filter(Objects::nonNull)
                                        .map(v -> {
                                            // 使用limit=2提高效率
                                            String[] items = v.split("\\|", 2);
                                            return items.length == 2
                                                    ? StringUtils.format("%s|%s", items[0], CommonUtils.getFileURL(items[1]))
                                                    : v;
                                        })
                                        .collect(Collectors.toList());
                                configDataItemVO.setValueList(valueList);
                            } else if ("ueditor".equals(itemType)) {
                                // 富文本处理
                                configDataItemVO.setValue(CommonUtils.getContent(item.getValue()));
                            }
                        }
                        // 加入列表
                        configDataItemVOList.add(configDataItemVO);
                    }
                }
                // 配置数据
                configDataVO.setDataList(configDataItemVOList);
                // 加入列表
                configDataVOList.add(configDataVO);
            }
        }
        // 返回结果
        return R.ok(configDataVOList);
    }

    /**
     * 保存系统配置
     *
     * @param data 数据源
     * @return 返回结果
     */
    @Override
    public R save(Map<String, Object> data) {
        // 参数判空
        if (StringUtils.isNull(data)) {
            return R.failed("参数不能为空");
        }
        // 遍历表单数据
        for (String key : data.keySet()) {
            // 参数键
            Object obj = data.get(key);
            // 参数键为空直接跳过
            if (StringUtils.isNull(obj)) {
                continue;
            }
            String value = "";
            // 判断参数是否List对象
            if (obj instanceof List) {
                // 此处主要是针对值列表参数处理并逗号拼接存储
                List<String> stringList = new ArrayList<>();
                // 遍历参数
                for (Object item : (ArrayList) obj) {
                    // 参数为空直接跳过
                    if (StringUtils.isNull(item)) {
                        continue;
                    }
                    // 参数
                    String param = item.toString().replaceAll(AppConfig.getDomain(), "");
                    // 加入列表
                    stringList.add(param);
                }
                // 参数值逗号拼接
                value = StringUtils.join(stringList, ",");
            } else if (obj.toString().contains("http://") || obj.toString().contains("https://")) {
                // 图片地址处理
                String param = obj.toString();
                // 资源附件地址替换
                value = param.replaceAll(AppConfig.getDomain(), "");
            } else {
                // 其他
                value = obj.toString();
            }
            // 根据编码查询配置项
            ConfigItem configItem = configItemService.getConfigItem(key);
            if (StringUtils.isNull(configItem)) {
                continue;
            }
            // 设置配置参数值
            configItem.setValue(value);
            // 更新配置数据
            configItemMapper.updateById(configItem);
        }
        return R.ok();
    }
}
