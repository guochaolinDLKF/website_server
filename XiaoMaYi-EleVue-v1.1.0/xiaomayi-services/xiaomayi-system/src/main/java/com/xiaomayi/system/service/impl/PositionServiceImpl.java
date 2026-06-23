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
import com.xiaomayi.system.dto.position.PositionAddDTO;
import com.xiaomayi.system.dto.position.PositionListDTO;
import com.xiaomayi.system.dto.position.PositionPageDTO;
import com.xiaomayi.system.dto.position.PositionUpdateDTO;
import com.xiaomayi.system.entity.Position;
import com.xiaomayi.system.mapper.PositionMapper;
import com.xiaomayi.system.service.PositionService;
import com.xiaomayi.system.vo.position.PositionInfoVO;
import com.xiaomayi.system.vo.position.PositionListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 岗位 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {

    /**
     * 查询分页列表
     *
     * @param positionPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Position> page(PositionPageDTO positionPageDTO) {
        // 分页设置
        Page<Position> page = new Page<>(positionPageDTO.getPageNo(), positionPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<Position>()
                // 岗位名称
                .like(StringUtils.isNotEmpty(positionPageDTO.getName()), Position::getName, positionPageDTO.getName())
                // 岗位状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(positionPageDTO.getStatus()) && positionPageDTO.getStatus() > 0, Position::getStatus, positionPageDTO.getStatus())
                .eq(Position::getDelFlag, 0)
                .orderByAsc(Position::getId);
        // 查询分页数据
        Page<Position> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            PositionListVO positionListVO = new PositionListVO();
            BeanUtils.copyProperties(item, positionListVO);
            return positionListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param positionListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<PositionListVO> getList(PositionListDTO positionListDTO) {
        // 岗位名称
        String name = positionListDTO.getName();
        // 岗位状态：1-正常 2-停用
        Integer status = positionListDTO.getStatus();
        // 查询数据源
        List<Position> positionList = list(new LambdaQueryWrapper<Position>()
                // 岗位名称
                .like(StringUtils.isNotEmpty(name), Position::getName, name)
                // 岗位状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(status) && status > 0, Position::getStatus, status)
                .eq(Position::getDelFlag, 0)
                .orderByAsc(Position::getId));
        // 实例化VO列表
        List<PositionListVO> positionListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(positionList)) {
            // 遍历数据源
            for (Position position : positionList) {
                // 实例化VO对象
                PositionListVO positionListVO = new PositionListVO();
                BeanUtils.copyProperties(position, positionListVO);
                positionListVOList.add(positionListVO);
            }
        }
        return positionListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 岗位ID
     * @return 返回结果
     */
    @Override
    public Position getInfo(Integer id) {
        Position position = getById(id);
        if (StringUtils.isNull(position) || !position.getDelFlag().equals(0)) {
            return null;
        }
        return position;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 岗位ID
     * @return 返回结果
     */
    @Override
    public PositionInfoVO getDetail(Integer id) {
        Position position = getInfo(id);
        if (StringUtils.isNull(position)) {
            return null;
        }
        // 实例化VO
        PositionInfoVO positionInfoVO = new PositionInfoVO();
        BeanUtils.copyProperties(position, positionInfoVO);
        return positionInfoVO;
    }

    /**
     * 添加岗位
     *
     * @param positionAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(PositionAddDTO positionAddDTO) {
        // 检查岗位名称是否已存在
        if (checkExist(positionAddDTO.getName(), 0)) {
            return R.failed("岗位名称已存在");
        }
        // 实例化对象
        Position position = new Position();
        // 属性拷贝
        BeanUtils.copyProperties(positionAddDTO, position);
        boolean result = save(position);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新岗位
     *
     * @param positionUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(PositionUpdateDTO positionUpdateDTO) {
        // 根据ID查询信息
        Position position = getInfo(positionUpdateDTO.getId());
        if (StringUtils.isNull(position)) {
            return R.failed("记录不存在");
        }
        // 检查岗位名称是否已存在
        if (checkExist(positionUpdateDTO.getName(), positionUpdateDTO.getId())) {
            return R.failed("岗位名称已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(positionUpdateDTO, position);
        boolean result = updateById(position);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除岗位
     *
     * @param idList 岗位ID
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
     * 检查岗位名称是否已存在
     *
     * @param name 岗位名称
     * @param id   岗位ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String name, Integer id) {
        Position position = getOne(new LambdaQueryWrapper<Position>()
                .eq(Position::getName, name)
                .ne(id > 0, Position::getId, id)
                .eq(Position::getDelFlag, 0), false);
        return StringUtils.isNotNull(position);
    }
}
