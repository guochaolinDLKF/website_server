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
import com.xiaomayi.system.dto.city.CityAddDTO;
import com.xiaomayi.system.dto.city.CityListDTO;
import com.xiaomayi.system.dto.city.CityUpdateDTO;
import com.xiaomayi.system.entity.City;
import com.xiaomayi.system.mapper.CityMapper;
import com.xiaomayi.system.service.CityService;
import com.xiaomayi.system.vo.city.CityInfoVO;
import com.xiaomayi.system.vo.city.CityListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-12
 */
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    /**
     * 查询数据列表
     *
     * @param cityListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<CityListVO> getList(CityListDTO cityListDTO) {
        // 城市名称
        String cityName = cityListDTO.getName();
        // 上级ID
        Integer pid = cityListDTO.getPid();
        if (StringUtils.isNull(pid) || pid <= 0) {
            pid = 0;
        }
        // 查询城市列表
        List<City> cityList = list(new LambdaQueryWrapper<City>()
                // 城市名称
                .like(StringUtils.isNotEmpty(cityName), City::getName, cityName)
                // 上级ID
                .eq(City::getPid, pid)
                .eq(City::getDelFlag, 0)
                .orderByAsc(City::getId));
        // 实例化VO列表
        List<CityListVO> cityListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cityList)) {
            for (City city : cityList) {
                // 实例化VO对象
                CityListVO cityListVO = new CityListVO();
                BeanUtils.copyProperties(city, cityListVO);
                // 加入列表
                cityListVOList.add(cityListVO);
            }
        }
        return cityListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id ID
     * @return 返回结果
     */
    @Override
    public City getInfo(Integer id) {
        City city = getById(id);
        if (StringUtils.isNull(city) || !city.getDelFlag().equals(0)) {
            return null;
        }
        return city;
    }

    /**
     * 根据ID查询详情
     *
     * @param id ID
     * @return 返回结果
     */
    @Override
    public CityInfoVO getDetail(Integer id) {
        City city = getInfo(id);
        if (StringUtils.isNull(city)) {
            return null;
        }
        // 实例化VO
        CityInfoVO cityInfoVO = new CityInfoVO();
        BeanUtils.copyProperties(city, cityInfoVO);

        // 获取上级城市名称
        Integer pid = cityInfoVO.getPid();
        if (StringUtils.isNotNull(pid) && pid > 0) {
            City pCity = getInfo(pid);
            if (StringUtils.isNotNull(pCity)) {
                cityInfoVO.setParentName(pCity.getName());
            }
        }

        // 返回结果
        return cityInfoVO;
    }

    /**
     * 添加
     *
     * @param cityAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(CityAddDTO cityAddDTO) {
        // 检查行政编码是否已存在
        if (checkExist(cityAddDTO.getAreaCode(), 0)) {
            return R.failed("行政编码已存在");
        }
        // 实例化对象
        City city = new City();
        // 属性拷贝
        BeanUtils.copyProperties(cityAddDTO, city);
        boolean result = save(city);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 更新
     *
     * @param cityUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(CityUpdateDTO cityUpdateDTO) {
        // 根据ID查询信息
        City city = getInfo(cityUpdateDTO.getId());
        if (StringUtils.isNull(city)) {
            return R.failed("记录不存在");
        }
        // 检查行政编码是否已存在
        if (checkExist(cityUpdateDTO.getAreaCode(), cityUpdateDTO.getId())) {
            return R.failed("行政编码已存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(cityUpdateDTO, city);
        boolean result = updateById(city);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 删除
     *
     * @param id 城市ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 删除ID判空
        if (StringUtils.isNull(id) || id <= 0) {
            return R.failed("删除记录ID不存在");
        }
        // 查询部门信息
        City city = getInfo(id);
        if (StringUtils.isNull(city)) {
            return R.failed("记录不存在");
        }
        // 判断是否存在子级
        long count = count(new LambdaQueryWrapper<City>()
                .eq(City::getPid, id)
                .eq(City::getDelFlag, 0));
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

    /**
     * 检查行政编码是否已存在
     *
     * @param areaCode 行政编码
     * @param id       城市ID
     * @return 返回结果
     */
    @Override
    public boolean checkExist(String areaCode, Integer id) {
        City city = getOne(new LambdaQueryWrapper<City>()
                .eq(City::getAreaCode, areaCode)
                .ne(id > 0, City::getId, id)
                .eq(City::getDelFlag, 0), false);
        return StringUtils.isNotNull(city);
    }

    /**
     * 根据编码获取城市列表
     *
     * @param areaCode 行政编码
     * @return 返回结果
     */
    @Override
    public List<City> getCityList(String areaCode) {
        List<City> cityList = list(new LambdaQueryWrapper<City>()
                .eq(City::getParentCode, areaCode)
                .eq(City::getDelFlag, 0)
                .orderByAsc(City::getId));
        return cityList;
    }

    /**
     * 根据行政编码获取城市信息
     *
     * @param areaCode 行政编码
     * @return 返回结果
     */
    @Override
    public City getCityInfo(String areaCode) {
        City city = getOne(new LambdaQueryWrapper<City>()
                .eq(City::getAreaCode, areaCode)
                .eq(City::getDelFlag, 0), false);
        return city;
    }

    /**
     * 获取行政编码获取名称
     *
     * @param areaCode  行政编码
     * @param delimiter 拼接符
     * @return 返回结果
     */
    @Override
    public String getCityNames(String areaCode, String delimiter) {
        List<String> nameList = new ArrayList<>();
        while (!"0".equals(areaCode)) {
            City city = getCityInfo(areaCode);
            if (StringUtils.isNotNull(city)) {
                nameList.add(city.getName());
                areaCode = city.getParentCode();
            } else {
                areaCode = "0";
            }
        }
        // 使用集合工具实现数组翻转
        Collections.reverse(nameList);
        return StringUtils.join(nameList.toArray(), delimiter);
    }
}