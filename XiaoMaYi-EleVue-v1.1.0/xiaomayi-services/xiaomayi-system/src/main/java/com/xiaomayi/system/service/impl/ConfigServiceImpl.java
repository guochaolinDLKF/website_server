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
import com.xiaomayi.system.dto.config.ConfigAddDTO;
import com.xiaomayi.system.dto.config.ConfigListDTO;
import com.xiaomayi.system.dto.config.ConfigPageDTO;
import com.xiaomayi.system.dto.config.ConfigUpdateDTO;
import com.xiaomayi.system.entity.Config;
import com.xiaomayi.system.mapper.ConfigMapper;
import com.xiaomayi.system.service.ConfigService;
import com.xiaomayi.system.vo.config.ConfigInfoVO;
import com.xiaomayi.system.vo.config.ConfigListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 配置 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    /**
     * 查询分页列表
     *
     * @param configPageDTO 查询条件
     * @return 返回结果
     */
    @Override
    public Page<Config> page(ConfigPageDTO configPageDTO) {
        // 分页设置
        Page<Config> page = new Page<>(configPageDTO.getPageNo(), configPageDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<Config>()
                // 配置名称
                .like(StringUtils.isNotEmpty(configPageDTO.getName()), Config::getName, configPageDTO.getName())
                .eq(Config::getDelFlag, 0)
                .orderByAsc(Config::getId);
        // 查询分页数据
        Page<Config> pageData = page(page, wrapper);
        // 遍历数据源
        pageData.convert(item -> {
            // 实例化VO对象
            ConfigListVO configListVO = new ConfigListVO();
            BeanUtils.copyProperties(item, configListVO);
            return configListVO;
        });
        // 返回结果
        return pageData;
    }

    /**
     * 查询数据列表
     *
     * @param configListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<ConfigListVO> getList(ConfigListDTO configListDTO) {
        // 查询数据列表
        List<Config> configList = list(new LambdaQueryWrapper<Config>()
                // 配置名称
                .like(StringUtils.isNotEmpty(configListDTO.getName()), Config::getName, configListDTO.getName())
                .eq(Config::getDelFlag, 0)
                .orderByAsc(Config::getId));
        // 实例化VO列表
        List<ConfigListVO> configListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(configList)) {
            // 遍历数据源
            for (Config config : configList) {
                // 实例化VO对象
                ConfigListVO configListVO = new ConfigListVO();
                BeanUtils.copyProperties(config, configListVO);
                configListVOList.add(configListVO);
            }
        }
        return configListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 配置ID
     * @return 返回结果
     */
    @Override
    public Config getInfo(Integer id) {
        Config config = getById(id);
        if (StringUtils.isNull(config) || !config.getDelFlag().equals(0)) {
            return null;
        }
        return config;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 配置ID
     * @return 返回结果
     */
    @Override
    public ConfigInfoVO getDetail(Integer id) {
        Config config = getInfo(id);
        if (StringUtils.isNull(config)) {
            return null;
        }
        // 实例化VO
        ConfigInfoVO configInfoVO = new ConfigInfoVO();
        BeanUtils.copyProperties(config, configInfoVO);
        return configInfoVO;
    }

    /**
     * 添加配置
     *
     * @param configAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(ConfigAddDTO configAddDTO) {
        // 检查配置名称是否已存在
        if (checkExist(configAddDTO.getCode(), 0)) {
            return R.failed("配置编码已存在");
        }
        // 实例化对象
        Config config = new Config();
        // 属性拷贝
        BeanUtils.copyProperties(configAddDTO, config);
        boolean result = save(config);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新配置
     *
     * @param configUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(ConfigUpdateDTO configUpdateDTO) {
        // 根据ID查询信息
        Config config = getInfo(configUpdateDTO.getId());
        if (StringUtils.isNull(config)) {
            return R.failed("记录不存在");
        }
        // 检查配置名称是否已存在
        if (checkExist(configUpdateDTO.getCode(), configUpdateDTO.getId())) {
            return R.failed("配置编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(configUpdateDTO, config);
        boolean result = updateById(config);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除配置
     *
     * @param idList 配置ID
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
     * 检查配置编码是否已存在
     *
     * @param code 配置编码
     * @param id   配置ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String code, Integer id) {
        Config config = getOne(new LambdaQueryWrapper<Config>()
                .eq(Config::getCode, code)
                .ne(id > 0, Config::getId, id)
                .eq(Config::getDelFlag, 0), false);
        return StringUtils.isNotNull(config);
    }
}
