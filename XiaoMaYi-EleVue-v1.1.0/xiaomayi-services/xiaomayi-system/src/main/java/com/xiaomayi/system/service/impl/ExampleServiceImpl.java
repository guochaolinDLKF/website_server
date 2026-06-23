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
import com.xiaomayi.core.config.AppConfig;
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.example.ExampleAddDTO;
import com.xiaomayi.system.dto.example.ExampleListDTO;
import com.xiaomayi.system.dto.example.ExamplePageDTO;
import com.xiaomayi.system.dto.example.ExampleUpdateDTO;
import com.xiaomayi.system.entity.Example;
import com.xiaomayi.system.mapper.ExampleMapper;
import com.xiaomayi.system.service.ExampleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.system.vo.example.ExampleInfoVO;
import com.xiaomayi.system.vo.example.ExampleListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 案例演示1 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2025-04-10
 */
@Service
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example> implements ExampleService {

    /**
     * 查询分页列表
     *
     * @param examplePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Example> page(ExamplePageDTO examplePageDTO) {
        // 分页设置
        Page<Example> page = new Page<>(examplePageDTO.getPageNo(), examplePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Example> wrapper = new LambdaQueryWrapper<Example>()
				// 案例名称
				.like(StringUtils.isNotEmpty(examplePageDTO.getName()), Example::getName, examplePageDTO.getName())
				// 案例状态
				.eq(StringUtils.isNotNull(examplePageDTO.getStatus()) && examplePageDTO.getStatus() > 0, Example::getStatus, examplePageDTO.getStatus())
				.eq(Example::getDelFlag, 0)
                .orderByAsc(Example::getId);
        // 查询分页数据
        Page<Example> pageData = page(page, wrapper);
        pageData.convert(item -> {
            // 实例化VO对象
            ExampleListVO exampleListVO = new ExampleListVO();
            BeanUtils.copyProperties(item, exampleListVO);
            return exampleListVO;
        });
        // 返回结果
        return pageData;
    }

	/**
     * 查询数据列表
     *
     * @param exampleListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<ExampleListVO> getList(ExampleListDTO exampleListDTO) {
        // 查询数据源
        List<Example> exampleList = list(new LambdaQueryWrapper<Example>()
                // 案例名称
                .like(StringUtils.isNotEmpty(exampleListDTO.getName()), Example::getName, exampleListDTO.getName())
                // 案例状态
                .eq(StringUtils.isNotNull(exampleListDTO.getStatus()) && exampleListDTO.getStatus() > 0, Example::getStatus, exampleListDTO.getStatus())
                .eq(Example::getDelFlag, 0)
                .orderByAsc(Example::getId));
        // 实例化VO列表
        List<ExampleListVO> exampleListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(exampleList)) {
            // 遍历数据源
            for (Example example : exampleList) {
                // 实例化VO对象
                ExampleListVO exampleListVO = new ExampleListVO();
                BeanUtils.copyProperties(example, exampleListVO);
                exampleListVOList.add(exampleListVO);
            }
        }
        return exampleListVOList;
    }

	/**
     * 根据ID查询信息
     *
     * @param id 案例演示1ID
     * @return 返回结果
     */
    @Override
    public Example getInfo(Integer id) {
        Example example = getById(id);
        if (StringUtils.isNull(example) || !example.getDelFlag().equals(0)) {
            return null;
        }
        return example;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 案例演示1ID
     * @return 返回结果
     */
    @Override
    public ExampleInfoVO getDetail(Integer id) {
        Example example = getInfo(id);
        if (StringUtils.isNull(example)) {
            return null;
        }
        // 实例化VO
        ExampleInfoVO exampleInfoVO = new ExampleInfoVO();
        BeanUtils.copyProperties(example, exampleInfoVO);
        return exampleInfoVO;
    }

	/**
     * 添加案例演示1
     *
     * @param exampleAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(ExampleAddDTO exampleAddDTO) {
        // 实例化对象
        Example example = new Example();
        // 属性拷贝
        BeanUtils.copyProperties(exampleAddDTO, example);
        boolean result = save(example);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新案例演示1
     *
     * @param exampleUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(ExampleUpdateDTO exampleUpdateDTO) {
        // 根据ID查询信息
        Example example = getInfo(exampleUpdateDTO.getId());
        if (StringUtils.isNull(example)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(exampleUpdateDTO, example);
        boolean result = updateById(example);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

	/**
     * 删除案例演示1
     *
     * @param id 案例演示1ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Example example = getInfo(id);
        if (StringUtils.isNull(example)) {
            return R.failed("记录不存在");
        }
        // 删除
        boolean result = removeById(id);
        if (!result) {
            return R.failed();
        }
        // 返回结果
        return R.ok();
    }


    /**
     * 批量删除案例演示1
     *
     * @param idList 案例演示1ID
     * @return 返回结果
     */
    @Override
    public R batchDelete(List<Integer> idList) {
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

}
