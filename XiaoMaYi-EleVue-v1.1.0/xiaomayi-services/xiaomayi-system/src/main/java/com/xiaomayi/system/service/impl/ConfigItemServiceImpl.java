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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.configitem.ConfigItemAddDTO;
import com.xiaomayi.system.dto.configitem.ConfigItemListDTO;
import com.xiaomayi.system.dto.configitem.ConfigItemPageDTO;
import com.xiaomayi.system.dto.configitem.ConfigItemUpdateDTO;
import com.xiaomayi.system.entity.Config;
import com.xiaomayi.system.entity.ConfigItem;
import com.xiaomayi.system.mapper.ConfigItemMapper;
import com.xiaomayi.system.service.ConfigItemService;
import com.xiaomayi.system.service.ConfigService;
import com.xiaomayi.system.vo.configitem.ConfigItemInfoVO;
import com.xiaomayi.system.vo.configitem.ConfigItemListVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * 配置项 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
@AllArgsConstructor
public class ConfigItemServiceImpl extends ServiceImpl<ConfigItemMapper, ConfigItem> implements ConfigItemService {

    private final ConfigService configService;

    /**
     * 查询分页列表
     *
     * @param configItemPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<ConfigItem> page(ConfigItemPageDTO configItemPageDTO) {
        // 配置项名称
        String name = configItemPageDTO.getName();
        // 配置项状态：1-正常 2-停用
        Integer status = configItemPageDTO.getStatus();
        // 查询条件
        LambdaQueryWrapper<ConfigItem> wrapper = new LambdaQueryWrapper<ConfigItem>()
                // 配置ID
                .eq(ConfigItem::getConfigId, configItemPageDTO.getConfigId())
                // 配置项名称
                .like(StringUtils.isNotEmpty(name), ConfigItem::getName, name)
                // 配置项状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(status) && status > 0, ConfigItem::getStatus, status)
                .eq(ConfigItem::getDelFlag, 0)
                .orderByAsc(ConfigItem::getSort);
        // 分页设置
        Page<ConfigItem> page = new Page<>(configItemPageDTO.getPageNo(), configItemPageDTO.getPageSize());
        // 查询分页数据
        Page<ConfigItem> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            ConfigItemListVO configItemListVO = new ConfigItemListVO();
            BeanUtils.copyProperties(item, configItemListVO);
            return configItemListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param configItemListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<ConfigItemListVO> getList(ConfigItemListDTO configItemListDTO) {
        // 配置项名称
        String name = configItemListDTO.getName();
        // 配置项状态：1-正常 2-停用
        Integer status = configItemListDTO.getStatus();
        // 查询数据源
        List<ConfigItem> configItemList = list(new LambdaQueryWrapper<ConfigItem>()
                // 配置ID
                .eq(ConfigItem::getConfigId, configItemListDTO.getConfigId())
                // 配置项名称
                .like(StringUtils.isNotEmpty(name), ConfigItem::getName, name)
                // 配置项状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(status) && status > 0, ConfigItem::getStatus, status)
                .eq(ConfigItem::getDelFlag, 0)
                .orderByAsc(ConfigItem::getId));
        // 实例化VO列表
        List<ConfigItemListVO> configItemListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(configItemList)) {
            // 遍历数据源
            for (ConfigItem configItem : configItemList) {
                // 实例化VO对象
                ConfigItemListVO configItemListVO = new ConfigItemListVO();
                BeanUtils.copyProperties(configItem, configItemListVO);
                // 加入列表
                configItemListVOList.add(configItemListVO);
            }
        }
        // 返回结果
        return configItemListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 配置项ID
     * @return 返回结果
     */
    @Override
    public ConfigItem getInfo(Integer id) {
        ConfigItem configItem = getById(id);
        if (StringUtils.isNull(configItem) || !configItem.getDelFlag().equals(0)) {
            return null;
        }
        return configItem;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 配置项ID
     * @return 返回结果
     */
    @Override
    public ConfigItemInfoVO getDetail(Integer id) {
        ConfigItem configItem = getInfo(id);
        if (StringUtils.isNull(configItem)) {
            return null;
        }
        // 实例化VO
        ConfigItemInfoVO configItemInfoVO = new ConfigItemInfoVO();
        BeanUtils.copyProperties(configItem, configItemInfoVO);

        // 配置类型
        String itemType = configItemInfoVO.getType();
        // 配置值
        String itemValue = configItemInfoVO.getValue();
        // 配置值判空
        if (StringUtils.isNotEmpty(itemValue)) {
            if ("image".equals(itemType)) {
                // 单图处理
                configItemInfoVO.setValue(CommonUtils.getFileURL(itemValue));
            } else if ("images".equals(itemType)) {
                // 多图处理
                String[] stringList = itemValue.split(",");
                // 集合数字转列表并拼接域名
                List<String> valueList = Arrays.stream(stringList).map(CommonUtils::getFileURL).toList();
                // 赋值给值对象
                configItemInfoVO.setValueList(valueList);
            } else if ("file".equals(itemType) || "files".equals(itemType)) {
                // 文件处理
                String[] stringList = itemValue.split(",");
                // 集合对象转列表
                List<String> valueList = Arrays.stream(stringList).toList();
                valueList.forEach(v -> {
                    // 文件名称、文件地址分裂处理
                    String[] items = v.split("\\|");
                    if (items.length == 2) {
                        v = StringUtils.format("%s|%s", items[0], CommonUtils.getFileURL(items[1]));
                    }
                });
                configItemInfoVO.setValueList(valueList);
            } else if ("ueditor".equals(itemType)) {
                // 富文本处理
                configItemInfoVO.setValue(CommonUtils.getContent(itemValue));
            }
        }

        // 返回结果
        return configItemInfoVO;
    }

    /**
     * 添加配置项
     *
     * @param configItemAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(ConfigItemAddDTO configItemAddDTO) {
        // 检查配置编码是否已存在
        if (checkExist(configItemAddDTO.getCode(), 0)) {
            return R.failed("配置编码已存在");
        }
        // 查询配置
        Config config = configService.getInfo(configItemAddDTO.getConfigId());
        if (StringUtils.isNull(config)) {
            return R.failed("配置不存在");
        }
        // 实例化对象
        ConfigItem configItem = new ConfigItem();
        // 属性拷贝
        BeanUtils.copyProperties(configItemAddDTO, configItem);
        // 配置编码
        configItem.setConfigCode(config.getCode());
        // 文件域名处理
        String value = configItem.getValue();
        if (StringUtils.isNotEmpty(value)) {
            // 正则判断参数值是否包含http(s)://
            Pattern HTTP_PATTERN = Pattern.compile("https?://");
            if (HTTP_PATTERN.matcher(value).find()) {
                configItem.setValue(value.replaceAll(AppConfig.getDomain(), ""));
            }
        }
        boolean result = save(configItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新配置项
     *
     * @param configItemUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(ConfigItemUpdateDTO configItemUpdateDTO) {
        // 根据ID查询信息
        ConfigItem configItem = getInfo(configItemUpdateDTO.getId());
        if (StringUtils.isNull(configItem)) {
            return R.failed("记录不存在");
        }
        // 检查配置编码是否已存在
        if (checkExist(configItemUpdateDTO.getCode(), configItem.getId())) {
            return R.failed("配置编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(configItemUpdateDTO, configItem);
        // 文件域名处理
        String value = configItem.getValue();
        if (StringUtils.isNotEmpty(value)) {
            // 正则判断参数值是否包含http(s)://
            Pattern HTTP_PATTERN = Pattern.compile("https?://");
            if (HTTP_PATTERN.matcher(value).find()) {
                configItem.setValue(value.replaceAll(AppConfig.getDomain(), ""));
            }
        }
        boolean result = updateById(configItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除配置项
     *
     * @param idList 配置项ID
     * @return 返回结果
     */
    @Override
    public R delete(List<Integer> idList) {
        // 删除ID判空
        if (StringUtils.isEmpty(idList)) {
            return R.failed("删除记录ID不存在");
        }
        // 批量删除
        boolean result = removeBatchByIds(idList);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 检查配置编码是否已存在
     *
     * @param code 配置项编码
     * @param id   配置项ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        ConfigItem configItem = getOne(new LambdaQueryWrapper<ConfigItem>()
                .eq(ConfigItem::getCode, code)
                .ne(id > 0, ConfigItem::getId, id)
                .eq(ConfigItem::getDelFlag, 0), false);
        return StringUtils.isNotNull(configItem);
    }

    /**
     * 根据编码查询配置数据
     *
     * @param code 配置编码
     * @return 返回结果
     */
    @Override
    public ConfigItem getConfigItem(String code) {
        ConfigItem configItem = getOne(new LambdaQueryWrapper<ConfigItem>()
                // 配置编码
                .eq(ConfigItem::getCode, code)
                .eq(ConfigItem::getDelFlag, 0), false);
        return configItem;
    }

    /**
     * 根据配置编码获取配置项列表
     *
     * @param code 配置编码
     * @return 返回结果
     */
    @Override
    public Map<String, String> getItemList(String code) {
        // 查询配置项列表
        List<ConfigItem> configItemList = list(new LambdaQueryWrapper<ConfigItem>()
                .eq(ConfigItem::getConfigCode, code)
                .eq(ConfigItem::getDelFlag, 0)
                .orderByAsc(ConfigItem::getSort));
        // 定义Map对象
        Map<String, String> configItemMap = new HashMap<>();
        // 遍历数据源
        configItemList.forEach(item -> {
            configItemMap.put(item.getCode(), item.getValue());
        });
        // 返回结果
        return configItemMap;
    }
}
