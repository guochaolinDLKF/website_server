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
import com.xiaomayi.system.dto.example2.Example2AddDTO;
import com.xiaomayi.system.dto.example2.Example2ListDTO;
import com.xiaomayi.system.dto.example2.Example2PageDTO;
import com.xiaomayi.system.dto.example2.Example2UpdateDTO;
import com.xiaomayi.system.entity.Example2;
import com.xiaomayi.system.mapper.Example2Mapper;
import com.xiaomayi.system.service.Example2Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.system.vo.example2.Example2InfoVO;
import com.xiaomayi.system.vo.example2.Example2ListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 案例演示2 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2025-04-10
 */
@Service
public class Example2ServiceImpl extends ServiceImpl<Example2Mapper, Example2> implements Example2Service {

    /**
     * 查询分页列表
     *
     * @param example2PageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Example2> page(Example2PageDTO example2PageDTO) {
        // 分页设置
        Page<Example2> page = new Page<>(example2PageDTO.getPageNo(), example2PageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Example2> wrapper = new LambdaQueryWrapper<Example2>()
				// 案例名称
				.like(StringUtils.isNotEmpty(example2PageDTO.getName()), Example2::getName, example2PageDTO.getName())
				// 案例类型
				.eq(StringUtils.isNotNull(example2PageDTO.getType()) && example2PageDTO.getType() > 0, Example2::getType, example2PageDTO.getType())
				// 案例状态
				.eq(StringUtils.isNotNull(example2PageDTO.getStatus()) && example2PageDTO.getStatus() > 0, Example2::getStatus, example2PageDTO.getStatus())
				.eq(Example2::getDelFlag, 0)
                .orderByAsc(Example2::getId);
        // 查询分页数据
        Page<Example2> pageData = page(page, wrapper);
        pageData.convert(item -> {
            // 实例化VO对象
            Example2ListVO example2ListVO = new Example2ListVO();
            BeanUtils.copyProperties(item, example2ListVO);
            // 案例图片
            String image = example2ListVO.getImage();
            if (StringUtils.isNotEmpty(image)) {
                example2ListVO.setImage(CommonUtils.getFileURL(image));
            }
            return example2ListVO;
        });
        // 返回结果
        return pageData;
    }

	/**
     * 查询数据列表
     *
     * @param example2ListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<Example2ListVO> getList(Example2ListDTO example2ListDTO) {
        // 查询数据源
        List<Example2> example2List = list(new LambdaQueryWrapper<Example2>()
                // 案例名称
                .like(StringUtils.isNotEmpty(example2ListDTO.getName()), Example2::getName, example2ListDTO.getName())
                // 案例类型
                .eq(StringUtils.isNotNull(example2ListDTO.getType()) && example2ListDTO.getType() > 0, Example2::getType, example2ListDTO.getType())
                // 案例状态
                .eq(StringUtils.isNotNull(example2ListDTO.getStatus()) && example2ListDTO.getStatus() > 0, Example2::getStatus, example2ListDTO.getStatus())
                .eq(Example2::getDelFlag, 0)
                .orderByAsc(Example2::getId));
        // 实例化VO列表
        List<Example2ListVO> example2ListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(example2List)) {
            // 遍历数据源
            for (Example2 example2 : example2List) {
                // 实例化VO对象
                Example2ListVO example2ListVO = new Example2ListVO();
                BeanUtils.copyProperties(example2, example2ListVO);
                // 案例图片
                String image = example2ListVO.getImage();
                if (StringUtils.isNotEmpty(image)) {
                    example2ListVO.setImage(CommonUtils.getFileURL(image));
                }
                example2ListVOList.add(example2ListVO);
            }
        }
        return example2ListVOList;
    }

	/**
     * 根据ID查询信息
     *
     * @param id 案例演示2ID
     * @return 返回结果
     */
    @Override
    public Example2 getInfo(Integer id) {
        Example2 example2 = getById(id);
        if (StringUtils.isNull(example2) || !example2.getDelFlag().equals(0)) {
            return null;
        }
        return example2;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 案例演示2ID
     * @return 返回结果
     */
    @Override
    public Example2InfoVO getDetail(Integer id) {
        Example2 example2 = getInfo(id);
        if (StringUtils.isNull(example2)) {
            return null;
        }
        // 实例化VO
        Example2InfoVO example2InfoVO = new Example2InfoVO();
        BeanUtils.copyProperties(example2, example2InfoVO);
        // 案例图片
        String image = example2InfoVO.getImage();
        if (StringUtils.isNotEmpty(image)) {
            example2InfoVO.setImage(CommonUtils.getFileURL(image));
        }
        return example2InfoVO;
    }

	/**
     * 添加案例演示2
     *
     * @param example2AddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(Example2AddDTO example2AddDTO) {
        // 实例化对象
        Example2 example2 = new Example2();
        // 属性拷贝
        BeanUtils.copyProperties(example2AddDTO, example2);
        // 案例图片
        String image = example2AddDTO.getImage();
        if (StringUtils.isNotEmpty(image) && image.contains(AppConfig.getDomain())) {
            example2.setImage(image.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = save(example2);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新案例演示2
     *
     * @param example2UpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(Example2UpdateDTO example2UpdateDTO) {
        // 根据ID查询信息
        Example2 example2 = getInfo(example2UpdateDTO.getId());
        if (StringUtils.isNull(example2)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(example2UpdateDTO, example2);
        // 案例图片
        String image = example2UpdateDTO.getImage();
        if (StringUtils.isNotEmpty(image) && image.contains(AppConfig.getDomain())) {
            example2.setImage(image.replaceAll(AppConfig.getDomain(), ""));
        }
        boolean result = updateById(example2);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

	/**
     * 删除案例演示2
     *
     * @param id 案例演示2ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Example2 example2 = getInfo(id);
        if (StringUtils.isNull(example2)) {
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
     * 批量删除案例演示2
     *
     * @param idList 案例演示2ID
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
