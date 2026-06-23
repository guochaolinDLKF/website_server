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

package com.xiaomayi.system.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.xiaomayi.core.utils.StreamUtils;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.entity.DictItem;
import com.xiaomayi.system.service.DictItemService;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典解析工具类
 * 字典工具命名规则，借鉴Spring官方MultipartResolver的命名方式，因此此处命名为：DictResolver
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
public class DictResolver {

    /**
     * 根据字典编码获取字典数据
     *
     * @param dictCode 字典编码
     * @return 返回结果
     */
    public List<DictItem> getDictItemsByCode(String dictCode) {
        // 参数判空
        Assert.isTrue(StringUtils.isNotBlank(dictCode), "字典编码不能为空");
        // 获取字典数据Bean对象
        DictItemService dictItemService = SpringUtil.getBean(DictItemService.class);
        // 查询字典数据
        return dictItemService.getDictItemList(dictCode);
    }

    /**
     * 根据字典编码、字典项值查询字典项名称
     *
     * @param dictCode  字典编码
     * @param itemValue 字典项值
     * @return 返回结果
     */
    public String getDictItemName(String dictCode, String itemValue) {
        // 根据字典编码、字典项值查询字典数据
        DictItem dictItem = getDictItemByItemValue(dictCode, itemValue);
        // 查询结果判空
        if (StringUtils.isNull(dictItem)) {
            return StringPool.EMPTY;
        }
        // 返回结果
        return dictItem.getName();
    }

    /**
     * 根据字典编码、字典项值获取字典数据
     *
     * @param dictCode  字典编码
     * @param itemValue 字典项值
     * @return 返回结果
     */
    public DictItem getDictItemByItemValue(String dictCode, String itemValue) {
        // 根据字典编码获取字典数据列表
        List<DictItem> dictItemList = getDictItemsByCode(dictCode);
        // 查询结果判空
        if (CollectionUtils.isEmpty(dictItemList)) {
            return null;
        }
        // 从集合中根据条件筛选指定数据，去第一个元素
        return dictItemList.stream().filter(item -> itemValue.equals(item.getValue())).findFirst().orElse(null);
    }

    /**
     * 根据字典编码、字典项名称获取字典项值
     *
     * @param dictCode 字典编码
     * @param itemName 字典名称
     * @return 返回结果
     */
    public String getDictItemValue(String dictCode, String itemName) {
        // 根据字典编码、字典项名称获取字典数据
        DictItem dictItem = getDictItemByItemName(dictCode, itemName);
        // 查询结果判空
        if (StringUtils.isNull(dictItem)) {
            return StringPool.EMPTY;
        }
        // 返回结果
        return dictItem.getValue();
    }

    /**
     * 根据字典编码、字典项名称获取字典数据
     *
     * @param dictCode 字典编码
     * @param itemName 字典项名称
     * @return 返回结果
     */
    public DictItem getDictItemByItemName(String dictCode, String itemName) {
        // 根据字典编码获取字典数据列表
        List<DictItem> dictItemList = getDictItemsByCode(dictCode);
        if (CollectionUtils.isEmpty(dictItemList)) {
            return null;
        }
        // 从集合中根据条件筛选指定数据，去第一个元素
        return dictItemList.stream().filter(item -> itemName.equals(item.getName())).findFirst().orElse(null);
    }

    /**
     * 获取字典标签、值列表
     *
     * @param dictCode 字典编码
     * @return 返回结果
     */
    public Map<String, String> getDictList(String dictCode) {
        // 根据字典编码获取字典项列表
        List<DictItem> dictItemList = DictResolver.getDictItemsByCode(dictCode);
        if (StringUtils.isEmpty(dictItemList)) {
            return null;
        }
        // List列表转Map对象
        return StreamUtils.toMap(dictItemList, DictItem::getValue, DictItem::getName);
    }
}
