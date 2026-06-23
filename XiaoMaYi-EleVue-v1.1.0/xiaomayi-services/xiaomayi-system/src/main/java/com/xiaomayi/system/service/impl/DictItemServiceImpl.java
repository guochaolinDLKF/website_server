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
import com.xiaomayi.core.constant.CacheConstant;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.dictitem.DictItemAddDTO;
import com.xiaomayi.system.dto.dictitem.DictItemListDTO;
import com.xiaomayi.system.dto.dictitem.DictItemPageDTO;
import com.xiaomayi.system.dto.dictitem.DictItemUpdateDTO;
import com.xiaomayi.system.entity.Dict;
import com.xiaomayi.system.entity.DictItem;
import com.xiaomayi.system.mapper.DictItemMapper;
import com.xiaomayi.system.service.DictItemService;
import com.xiaomayi.system.service.DictService;
import com.xiaomayi.system.vo.dictitem.DictItemInfoVO;
import com.xiaomayi.system.vo.dictitem.DictItemListVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 字典数据 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
@AllArgsConstructor
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    private final DictService dictService;

    /**
     * 查询分页列表
     *
     * @param dictItemPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<DictItem> page(DictItemPageDTO dictItemPageDTO) {
        // 分页设置
        Page<DictItem> page = new Page<>(dictItemPageDTO.getPageNo(), dictItemPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<DictItem> wrapper = new LambdaQueryWrapper<DictItem>()
                // 字典ID
                .eq(DictItem::getDictId, dictItemPageDTO.getDictId())
                // 字典项名称
                .like(StringUtils.isNotEmpty(dictItemPageDTO.getName()), DictItem::getName, dictItemPageDTO.getName())
                .eq(DictItem::getDelFlag, 0)
                .orderByAsc(DictItem::getId);
        // 查询分页数据
        Page<DictItem> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            DictItemListVO dictItemListVO = new DictItemListVO();
            BeanUtils.copyProperties(item, dictItemListVO);
            return dictItemListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param dictItemListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<DictItemListVO> getList(DictItemListDTO dictItemListDTO) {
        // 查询数据源
        List<DictItem> dictItemList = list(new LambdaQueryWrapper<DictItem>()
                // 字典项名称
                .like(StringUtils.isNotEmpty(dictItemListDTO.getName()), DictItem::getName, dictItemListDTO.getName())
                .eq(DictItem::getDelFlag, 0)
                .orderByAsc(DictItem::getId));
        // 实例化VO列表
        List<DictItemListVO> dictItemListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(dictItemList)) {
            // 遍历数据源
            for (DictItem dictItem : dictItemList) {
                // 实例化VO对象
                DictItemListVO dictItemListVO = new DictItemListVO();
                BeanUtils.copyProperties(dictItem, dictItemListVO);
                dictItemListVOList.add(dictItemListVO);
            }
        }
        return dictItemListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 字典数据ID
     * @return 返回结果
     */
    @Override
    public DictItem getInfo(Integer id) {
        DictItem dictItem = getById(id);
        if (StringUtils.isNull(dictItem) || !dictItem.getDelFlag().equals(0)) {
            return null;
        }
        return dictItem;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 字典数据ID
     * @return 返回结果
     */
    @Override
    public DictItemInfoVO getDetail(Integer id) {
        DictItem dictItem = getInfo(id);
        if (StringUtils.isNull(dictItem)) {
            return null;
        }
        // 实例化VO
        DictItemInfoVO dictItemInfoVO = new DictItemInfoVO();
        BeanUtils.copyProperties(dictItem, dictItemInfoVO);
        return dictItemInfoVO;
    }

    /**
     * 添加字典数据
     *
     * @param dictItemAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(DictItemAddDTO dictItemAddDTO) {
        // 查询字典
        Dict dict = dictService.getInfo(dictItemAddDTO.getDictId());
        if (StringUtils.isNull(dict)) {
            return R.failed("字典不存在");
        }
        // 实例化对象
        DictItem dictItem = new DictItem();
        // 属性拷贝
        BeanUtils.copyProperties(dictItemAddDTO, dictItem);
        // 字典编码
        dictItem.setDictCode(dict.getCode());
        boolean result = save(dictItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新字典数据
     *
     * @param dictItemUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(DictItemUpdateDTO dictItemUpdateDTO) {
        // 根据ID查询信息
        DictItem dictItem = getInfo(dictItemUpdateDTO.getId());
        if (StringUtils.isNull(dictItem)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(dictItemUpdateDTO, dictItem);
        boolean result = updateById(dictItem);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除字典数据
     *
     * @param idList 字典数据ID
     * @return 返回结果
     */
    @CacheEvict(value = CacheConstant.SYS_DICT_KEY, allEntries = true)
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
     * 根据编码获取字典数据列表
     *
     * @param dictCode 字典编码
     * @return 返回结果
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_KEY, key = "#dictCode", unless = "#result.isEmpty()")
    public List<DictItem> getDictItemList(String dictCode) {
        List<DictItem> dictItemList = list(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getDictCode, dictCode)
                .eq(DictItem::getDelFlag, 0)
                .orderByAsc(DictItem::getSort));
        return dictItemList;
    }
}
