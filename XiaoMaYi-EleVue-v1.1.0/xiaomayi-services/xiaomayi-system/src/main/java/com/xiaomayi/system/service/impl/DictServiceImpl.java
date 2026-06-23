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
import com.xiaomayi.system.dto.dict.DictAddDTO;
import com.xiaomayi.system.dto.dict.DictListDTO;
import com.xiaomayi.system.dto.dict.DictPageDTO;
import com.xiaomayi.system.dto.dict.DictUpdateDTO;
import com.xiaomayi.system.entity.Dict;
import com.xiaomayi.system.mapper.DictMapper;
import com.xiaomayi.system.service.DictService;
import com.xiaomayi.system.vo.dict.DictInfoVO;
import com.xiaomayi.system.vo.dict.DictListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 字典 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 查询分页列表
     *
     * @param dictPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Dict> page(DictPageDTO dictPageDTO) {
        // 分页设置
        Page<Dict> page = new Page<>(dictPageDTO.getPageNo(), dictPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<Dict>()
                // 字典名称
                .like(StringUtils.isNotEmpty(dictPageDTO.getName()), Dict::getName, dictPageDTO.getName())
                .eq(Dict::getDelFlag, 0)
                .orderByDesc(Dict::getId);
        // 查询分页数据
        Page<Dict> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            DictListVO dictListVO = new DictListVO();
            BeanUtils.copyProperties(item, dictListVO);
            return dictListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param dictListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<DictListVO> getList(DictListDTO dictListDTO) {
        // 查询数据源
        List<Dict> dictList = list(new LambdaQueryWrapper<Dict>()
                // 字典名称
                .like(StringUtils.isNotEmpty(dictListDTO.getName()), Dict::getName, dictListDTO.getName())
                .eq(Dict::getDelFlag, 0)
                .orderByAsc(Dict::getId));
        // 实例化VO列表
        List<DictListVO> dictListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(dictList)) {
            // 遍历数据源
            for (Dict dict : dictList) {
                // 实例化VO对象
                DictListVO dictListVO = new DictListVO();
                BeanUtils.copyProperties(dict, dictListVO);
                dictListVOList.add(dictListVO);
            }
        }
        return dictListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 字典ID
     * @return 返回结果
     */
    @Override
    public Dict getInfo(Integer id) {
        Dict dict = getById(id);
        if (StringUtils.isNull(dict) || !dict.getDelFlag().equals(0)) {
            return null;
        }
        return dict;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 字典ID
     * @return 返回结果
     */
    @Override
    public DictInfoVO getDetail(Integer id) {
        Dict dict = getInfo(id);
        if (StringUtils.isNull(dict)) {
            return null;
        }
        // 实例化VO
        DictInfoVO dictInfoVO = new DictInfoVO();
        BeanUtils.copyProperties(dict, dictInfoVO);
        return dictInfoVO;
    }

    /**
     * 添加字典
     *
     * @param dictAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(DictAddDTO dictAddDTO) {
        // 检查字典编码是否已存在
        if (checkExist(dictAddDTO.getCode(), 0)) {
            return R.failed("字典编码已存在");
        }
        // 实例化对象
        Dict dict = new Dict();
        // 属性拷贝
        BeanUtils.copyProperties(dictAddDTO, dict);
        boolean result = save(dict);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新字典
     *
     * @param dictUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(DictUpdateDTO dictUpdateDTO) {
        // 根据ID查询信息
        Dict dict = getInfo(dictUpdateDTO.getId());
        if (StringUtils.isNull(dict)) {
            return R.failed("记录不存在");
        }
        // 检查字典编码是否已存在
        if (checkExist(dictUpdateDTO.getCode(), dict.getId())) {
            return R.failed("字典编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(dictUpdateDTO, dict);
        boolean result = updateById(dict);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除字典
     *
     * @param idList 字典ID
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
     * 检查字典编码是否已存在
     *
     * @param code 字典编码
     * @param id   字典ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Dict dict = getOne(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getCode, code)
                .ne(id > 0, Dict::getId, id)
                .eq(Dict::getDelFlag, 0), false);
        return StringUtils.isNotNull(dict);
    }

    /**
     * 刷新缓存,此处清除所有缓存
     *
     * @return 返回结果
     */
    @Override
    public R refreshCache() {
        return R.ok();
    }
}
