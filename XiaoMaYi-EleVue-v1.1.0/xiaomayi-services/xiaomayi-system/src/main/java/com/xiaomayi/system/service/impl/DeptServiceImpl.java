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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.system.dto.dept.DeptAddDTO;
import com.xiaomayi.system.dto.dept.DeptListDTO;
import com.xiaomayi.system.dto.dept.DeptUpdateDTO;
import com.xiaomayi.system.entity.Dept;
import com.xiaomayi.system.mapper.DeptMapper;
import com.xiaomayi.system.service.DeptService;
import com.xiaomayi.system.vo.dept.DeptInfoVO;
import com.xiaomayi.system.vo.dept.DeptListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    /**
     * 查询数据列表
     *
     * @param deptListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<DeptListVO> getList(DeptListDTO deptListDTO) {
        // 查询数据源
        List<Dept> deptList = list(new LambdaQueryWrapper<Dept>()
                // 部门名称
                .like(StringUtils.isNotEmpty(deptListDTO.getName()), Dept::getName, deptListDTO.getName())
                .eq(Dept::getDelFlag, 0)
                .orderByAsc(Dept::getId));
        // 实例化对象VO列表
        List<DeptListVO> deptListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(deptList)) {
            // 遍历数据源
            for (Dept dept : deptList) {
                DeptListVO deptListVO = new DeptListVO();
                BeanUtils.copyProperties(dept, deptListVO);
                deptListVOList.add(deptListVO);
            }
        }
        return deptListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 部门ID
     * @return 返回结果
     */
    @Override
    public Dept getInfo(Integer id) {
        Dept dept = getById(id);
        if (StringUtils.isNull(dept) || !dept.getDelFlag().equals(0)) {
            return null;
        }
        return dept;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 部门ID
     * @return 返回结果
     */
    @Override
    public DeptInfoVO getDetail(Integer id) {
        Dept dept = getInfo(id);
        if (StringUtils.isNull(dept)) {
            return null;
        }
        // 实例化VO
        DeptInfoVO deptInfoVO = new DeptInfoVO();
        BeanUtils.copyProperties(dept, deptInfoVO);
        return deptInfoVO;
    }

    /**
     * 添加部门
     *
     * @param deptAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(DeptAddDTO deptAddDTO) {
        // 实例化对象
        Dept dept = new Dept();
        // 属性拷贝
        BeanUtils.copyProperties(deptAddDTO, dept);
        boolean result = save(dept);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新部门
     *
     * @param deptUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(DeptUpdateDTO deptUpdateDTO) {
        // 根据ID查询信息
        Dept dept = getInfo(deptUpdateDTO.getId());
        if (StringUtils.isNull(dept)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(deptUpdateDTO, dept);
        boolean result = updateById(dept);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 删除ID判空
        if (StringUtils.isNull(id) || id <= 0) {
            return R.failed("删除记录ID不存在");
        }
        // 查询部门信息
        Dept dept = getInfo(id);
        if (StringUtils.isNull(dept)) {
            return R.failed("记录不存在");
        }
        // 判断是否存在子级
        long count = count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, id)
                .eq(Dept::getDelFlag, 0));
        if (count > 0) {
            return R.failed("存在子级，无法删除");
        }
        // 删除记录
        boolean result = removeById(id);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

}
