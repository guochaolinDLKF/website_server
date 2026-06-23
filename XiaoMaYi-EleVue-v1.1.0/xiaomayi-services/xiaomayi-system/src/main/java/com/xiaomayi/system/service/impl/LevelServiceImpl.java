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
import com.xiaomayi.core.utils.CommonUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.excel.utils.ExcelUtil;
import com.xiaomayi.system.dto.level.LevelAddDTO;
import com.xiaomayi.system.dto.level.LevelListDTO;
import com.xiaomayi.system.dto.level.LevelPageDTO;
import com.xiaomayi.system.dto.level.LevelUpdateDTO;
import com.xiaomayi.system.entity.Level;
import com.xiaomayi.system.mapper.LevelMapper;
import com.xiaomayi.system.service.LevelService;
import com.xiaomayi.system.vo.level.LevelExcelVO;
import com.xiaomayi.system.vo.level.LevelInfoVO;
import com.xiaomayi.system.vo.level.LevelListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 职级 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements LevelService {

    /**
     * 查询分页列表
     *
     * @param levelPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Level> page(LevelPageDTO levelPageDTO) {
        // 分页设置
        Page<Level> page = new Page<>(levelPageDTO.getPageNo(), levelPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Level> wrapper = new LambdaQueryWrapper<Level>()
                // 职级名称
                .like(StringUtils.isNotEmpty(levelPageDTO.getName()), Level::getName, levelPageDTO.getName())
                // 职级状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(levelPageDTO.getStatus()) && levelPageDTO.getStatus() > 0, Level::getStatus, levelPageDTO.getStatus())
                .eq(Level::getDelFlag, 0)
                .orderByAsc(Level::getId);
        // 查询分页数据
        Page<Level> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            LevelListVO levelListVO = new LevelListVO();
            BeanUtils.copyProperties(item, levelListVO);
            return levelListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param levelListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<LevelListVO> getList(LevelListDTO levelListDTO) {
        // 职级名称
        String name = levelListDTO.getName();
        // 职级状态：1-正常 2-停用
        Integer status = levelListDTO.getStatus();
        // 查询数据源
        List<Level> levelList = list(new LambdaQueryWrapper<Level>()
                // 职级名称
                .like(StringUtils.isNotEmpty(name), Level::getName, name)
                // 职级状态：1-正常 2-停用
                .eq(StringUtils.isNotNull(status) && status > 0, Level::getStatus, status)
                .eq(Level::getDelFlag, 0)
                .orderByAsc(Level::getId));
        // 实例化VO列表
        List<LevelListVO> levelListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(levelList)) {
            // 遍历数据源
            for (Level level : levelList) {
                // 实例化VO对象
                LevelListVO levelListVO = new LevelListVO();
                BeanUtils.copyProperties(level, levelListVO);
                levelListVOList.add(levelListVO);
            }
        }
        return levelListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 职级ID
     * @return 返回结果
     */
    @Override
    public Level getInfo(Integer id) {
        Level level = getById(id);
        if (StringUtils.isNull(level) || !level.getDelFlag().equals(0)) {
            return null;
        }
        return level;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 职级ID
     * @return 返回结果
     */
    @Override
    public LevelInfoVO getDetail(Integer id) {
        Level level = getInfo(id);
        if (StringUtils.isNull(level)) {
            return null;
        }
        // 实例化VO
        LevelInfoVO levelInfoVO = new LevelInfoVO();
        BeanUtils.copyProperties(level, levelInfoVO);
        return levelInfoVO;
    }

    /**
     * 添加职级
     *
     * @param levelAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(LevelAddDTO levelAddDTO) {
        // 检查职级名称是否已存在
        if (checkExist(levelAddDTO.getName(), 0)) {
            return R.failed("职级名称已存在");
        }
        // 实例化对象
        Level level = new Level();
        // 属性拷贝
        BeanUtils.copyProperties(levelAddDTO, level);
        boolean result = save(level);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新职级
     *
     * @param levelUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(LevelUpdateDTO levelUpdateDTO) {
        // 根据ID查询信息
        Level level = getInfo(levelUpdateDTO.getId());
        if (StringUtils.isNull(level)) {
            return R.failed("记录不存在");
        }
        // 检查职级名称是否已存在
        if (checkExist(levelUpdateDTO.getName(), levelUpdateDTO.getId())) {
            return R.failed("职级名称已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(levelUpdateDTO, level);
        boolean result = updateById(level);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除职级
     *
     * @param id 任务ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 根据ID查询信息
        Level level = getInfo(id);
        if (StringUtils.isNull(level)) {
            return R.failed("记录不存在");
        }
        // 删除记录
        boolean result = removeById(level);
        if (!result) {
            return R.failed();
        }
        // 返回结果
        return R.ok();
    }

    /**
     * 删除职级
     *
     * @param idList 职级ID
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
     * 检查职级名称是否已存在
     *
     * @param name 职级名称
     * @param id   职级ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String name, Integer id) {
        Level level = getOne(new LambdaQueryWrapper<Level>()
                .eq(Level::getName, name)
                .ne(id > 0, Level::getId, id)
                .eq(Level::getDelFlag, 0), false);
        return StringUtils.isNotNull(level);
    }

    /**
     * 导入Excel
     *
     * @param levelExcelVOList 数据源
     * @return 返回结果
     */
    @Override
    public R importLevel(List<LevelExcelVO> levelExcelVOList) {
        // 数据源判空
        if (StringUtils.isEmpty(levelExcelVOList)) {
            return R.failed("导入数据源不能为空");
        }
        // 计数器
        int totalNum = 0;
        // 遍历数据源
        for (LevelExcelVO levelExcelVO : levelExcelVOList) {
            Level level = new Level();
            BeanUtils.copyProperties(levelExcelVO, level);
            // 置空主键
            level.setId(null);
            // 插入数据
            boolean result = save(level);
            if (!result) {
                continue;
            }
            // 计数器+1
            totalNum += 1;
        }
        return R.ok(null, String.format("本次共计导入【%d】条数据", totalNum));
    }

    /**
     * 导出Excel
     *
     * @param levelListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public R exportLevel(LevelListDTO levelListDTO) {
        // 实例化Excel导出VO
        List<LevelExcelVO> levelExcelVOList = new ArrayList<>();
        // 获取直接列表
        List<LevelListVO> levelList = getList(levelListDTO);
        if (StringUtils.isNotEmpty(levelList)) {
            // 遍历职级列表
            for (LevelListVO levelListVO : levelList) {
                LevelExcelVO levelExcelVO = new LevelExcelVO();
                BeanUtils.copyProperties(levelListVO, levelExcelVO);
                // 加入列表
                levelExcelVOList.add(levelExcelVO);
            }
        }
        // 导出Excel
        ExcelUtil<LevelExcelVO> excelUtil = new ExcelUtil<>(LevelExcelVO.class);
        String filePath = excelUtil.exportExcel(levelExcelVOList, "职级数据");
        // 返回结果
        return R.ok(CommonUtils.getFileURL("/" + filePath));
    }
}
