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
import com.xiaomayi.system.dto.param.ParamAddDTO;
import com.xiaomayi.system.dto.param.ParamPageDTO;
import com.xiaomayi.system.dto.param.ParamUpdateDTO;
import com.xiaomayi.system.entity.Param;
import com.xiaomayi.system.mapper.ParamMapper;
import com.xiaomayi.system.service.ParamService;
import com.xiaomayi.system.vo.param.ParamInfoVO;
import com.xiaomayi.system.vo.param.ParamListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 参数 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-14
 */
@Service
public class ParamServiceImpl extends ServiceImpl<ParamMapper, Param> implements ParamService {

    /**
     * 查询分页列表
     *
     * @param paramPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Param> page(ParamPageDTO paramPageDTO) {
        // 分页设置
        Page<Param> page = new Page<>(paramPageDTO.getPageNo(), paramPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Param> wrapper = new LambdaQueryWrapper<Param>()
                // 参数名称
                .like(StringUtils.isNotEmpty(paramPageDTO.getName()), Param::getName, paramPageDTO.getName())
                // 参数类型：0-系统 1-业务
                .eq(StringUtils.isNotNull(paramPageDTO.getType()), Param::getType, paramPageDTO.getType())
                // 参数状态：1-正常 2-禁用
                .eq(StringUtils.isNotNull(paramPageDTO.getStatus()) && paramPageDTO.getStatus() > 0, Param::getStatus, paramPageDTO.getStatus())
                .eq(Param::getDelFlag, 0)
                .orderByAsc(Param::getId);
        // 查询分页数据
        Page<Param> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            ParamListVO paramListVO = new ParamListVO();
            BeanUtils.copyProperties(item, paramListVO);
            return paramListVO;
        });
        return pageData;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 参数ID
     * @return 返回结果
     */
    @Override
    public Param getInfo(Integer id) {
        Param param = getById(id);
        if (StringUtils.isNull(param) || !param.getDelFlag().equals(0)) {
            return null;
        }
        return param;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 参数ID
     * @return 返回结果
     */
    @Override
    public ParamInfoVO getDetail(Integer id) {
        Param param = getInfo(id);
        if (StringUtils.isNull(param)) {
            return null;
        }
        // 实例化VO
        ParamInfoVO paramInfoVO = new ParamInfoVO();
        BeanUtils.copyProperties(param, paramInfoVO);
        return paramInfoVO;
    }

    /**
     * 添加参数
     *
     * @param paramAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(ParamAddDTO paramAddDTO) {
        // 检查参数编码是否已存在
        if (checkExist(paramAddDTO.getCode(), 0)) {
            return R.failed("参数编码已存在");
        }
        // 实例化对象
        Param param = new Param();
        // 属性拷贝
        BeanUtils.copyProperties(paramAddDTO, param);
        boolean result = save(param);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新参数
     *
     * @param paramUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(ParamUpdateDTO paramUpdateDTO) {
        // 根据ID查询信息
        Param param = getInfo(paramUpdateDTO.getId());
        if (StringUtils.isNull(param)) {
            return R.failed("记录不存在");
        }
        // 检查参数编码是否已存在
        if (checkExist(paramUpdateDTO.getCode(), paramUpdateDTO.getId())) {
            return R.failed("参数编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(paramUpdateDTO, param);
        boolean result = updateById(param);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除参数
     *
     * @param id 参数ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Param param = getInfo(id);
        if (StringUtils.isNull(param)) {
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
     * 批量删除参数
     *
     * @param idList 参数ID
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
     * 检查参数编码是否已存在
     *
     * @param code 参数编码
     * @param id   参数ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Param param = getOne(new LambdaQueryWrapper<Param>()
                .eq(Param::getCode, code)
                .ne(id > 0, Param::getId, id)
                .eq(Param::getDelFlag, 0), false);
        return StringUtils.isNotNull(param);
    }

    /**
     * 根据参数编码获取信息
     *
     * @param paramCode 参数编码
     * @return 返回结果
     */
    @Override
    public Param getParamInfo(String paramCode) {
        Param param = getOne(new LambdaQueryWrapper<Param>()
                .eq(Param::getCode, paramCode)
                .eq(Param::getDelFlag, 0), false);
        return param;
    }
}
