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
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.datasource.DataSourceAddDTO;
import com.xiaomayi.system.dto.datasource.DataSourcePageDTO;
import com.xiaomayi.system.dto.datasource.DataSourceUpdateDTO;
import com.xiaomayi.system.entity.DataSource;
import com.xiaomayi.system.mapper.DataSourceMapper;
import com.xiaomayi.system.service.DataSourceService;
import com.xiaomayi.system.vo.datasource.DataSourceInfoVO;
import com.xiaomayi.system.vo.datasource.DataSourceListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 多数据源 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-18
 */
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    /**
     * 查询分页列表
     *
     * @param dataSourcePageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<DataSource> page(DataSourcePageDTO dataSourcePageDTO) {
        // 分页设置
        Page<DataSource> page = new Page<>(dataSourcePageDTO.getPageNo(), dataSourcePageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<DataSource>()
                // 数据源名称
                .like(StringUtils.isNotEmpty(dataSourcePageDTO.getName()), DataSource::getName, dataSourcePageDTO.getName())
                // 数据库类型
                .like(StringUtils.isNotNull(dataSourcePageDTO.getDbType()), DataSource::getDbType, dataSourcePageDTO.getDbType())
                // 数据库名称
                .eq(DataSource::getDelFlag, 0)
                .orderByAsc(DataSource::getId);
        // 查询分页数据
        Page<DataSource> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            DataSourceListVO dataSourceListVO = new DataSourceListVO();
            BeanUtils.copyProperties(item, dataSourceListVO);
            return dataSourceListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 多数据源ID
     * @return 返回结果
     */
    @Override
    public DataSource getInfo(Integer id) {
        DataSource dataSource = getById(id);
        if (StringUtils.isNull(dataSource) || !dataSource.getDelFlag().equals(0)) {
            return null;
        }
        return dataSource;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 多数据源ID
     * @return 返回结果
     */
    @Override
    public DataSourceInfoVO getDetail(Integer id) {
        DataSource dataSource = getInfo(id);
        if (StringUtils.isNull(dataSource)) {
            return null;
        }
        // 实例化VO
        DataSourceInfoVO dataSourceInfoVO = new DataSourceInfoVO();
        BeanUtils.copyProperties(dataSource, dataSourceInfoVO);
        return dataSourceInfoVO;
    }

    /**
     * 添加多数据源
     *
     * @param dataSourceAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(DataSourceAddDTO dataSourceAddDTO) {
        // 检查数据源编码是否已存在
        if (checkExist(dataSourceAddDTO.getCode(), 0)) {
            return R.failed("数据源编码已存在");
        }
        // 实例化对象
        DataSource dataSource = new DataSource();
        // 属性拷贝
        BeanUtils.copyProperties(dataSourceAddDTO, dataSource);
        boolean result = save(dataSource);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新多数据源
     *
     * @param dataSourceUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(DataSourceUpdateDTO dataSourceUpdateDTO) {
        // 根据ID查询信息
        DataSource dataSource = getInfo(dataSourceUpdateDTO.getId());
        if (StringUtils.isNull(dataSource)) {
            return R.failed("记录不存在");
        }
        // 检查数据源编码是否已存在
        if (checkExist(dataSourceUpdateDTO.getCode(), dataSourceUpdateDTO.getId())) {
            return R.failed("数据源编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(dataSourceUpdateDTO, dataSource);
        boolean result = updateById(dataSource);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除多数据源
     *
     * @param id 多数据源ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        DataSource dataSource = getInfo(id);
        if (StringUtils.isNull(dataSource)) {
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
     * 批量删除多数据源
     *
     * @param idList 多数据源ID
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

    /**
     * 检查数据源编码是否已存在
     *
     * @param code 数据源编码
     * @param id   数据源ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        DataSource dataSource = getOne(new LambdaQueryWrapper<DataSource>()
                .eq(DataSource::getCode, code)
                .ne(id > 0, DataSource::getId, id)
                .eq(DataSource::getDelFlag, 0), false);
        return StringUtils.isNotNull(dataSource);
    }
}
