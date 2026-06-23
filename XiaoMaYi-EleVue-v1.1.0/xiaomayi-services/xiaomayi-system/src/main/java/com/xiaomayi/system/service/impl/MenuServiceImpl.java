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
import com.xiaomayi.system.dto.menu.MenuAddDTO;
import com.xiaomayi.system.dto.menu.MenuListDTO;
import com.xiaomayi.system.dto.menu.MenuUpdateDTO;
import com.xiaomayi.system.entity.Menu;
import com.xiaomayi.system.mapper.MenuMapper;
import com.xiaomayi.system.mapper.RoleMenuMapper;
import com.xiaomayi.system.service.MenuService;
import com.xiaomayi.system.vo.menu.MenuInfoVO;
import com.xiaomayi.system.vo.menu.MenuListVO;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-03-23
 */
@Service
@AllArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    /**
     * 查询数据列表
     *
     * @param menuListDTO 查询条件
     * @return 返回结果
     */
    @Override
    public List<MenuListVO> getList(MenuListDTO menuListDTO) {
        List<Menu> menuList = list(new LambdaQueryWrapper<Menu>()
                // 菜单名称
                .like(StringUtils.isNotEmpty(menuListDTO.getName()), Menu::getName, menuListDTO.getName())
                .eq(Menu::getDelFlag, 0));
        // 实例化VO列表
        List<MenuListVO> menuListVOList = new ArrayList<>();
        if (StringUtils.isNotEmpty(menuList)) {
            for (Menu menu : menuList) {
                // 实例化VO对象
                MenuListVO menuListVO = new MenuListVO();
                BeanUtils.copyProperties(menu, menuListVO);
                menuListVOList.add(menuListVO);
            }
        }
        return menuListVOList;
    }

    /**
     * 根据ID查询信息
     *
     * @param id 菜单ID
     * @return 返回结果
     */
    @Override
    public Menu getInfo(Integer id) {
        Menu menu = getById(id);
        if (StringUtils.isNull(menu) || !menu.getDelFlag().equals(0)) {
            return null;
        }
        return menu;
    }

    /**
     * 根据ID查询详情
     *
     * @param id 菜单ID
     * @return 返回结果
     */
    @Override
    public MenuInfoVO getDetail(Integer id) {
        Menu menu = getInfo(id);
        if (StringUtils.isNull(menu)) {
            return null;
        }
        // 实例化VO
        MenuInfoVO menuInfoVO = new MenuInfoVO();
        BeanUtils.copyProperties(menu, menuInfoVO);
        return menuInfoVO;
    }

    /**
     * 添加菜单
     *
     * @param menuAddDTO 参数
     * @return 返回结果
     */
    @Override
    public R add(MenuAddDTO menuAddDTO) {
        // 实例化对象
        Menu menu = new Menu();
        // 属性拷贝
        BeanUtils.copyProperties(menuAddDTO, menu);
        boolean result = save(menu);
        if (!result) {
            return R.failed();
        }
        return R.ok(menu.getId());
    }

    /**
     * 更新菜单
     *
     * @param menuUpdateDTO 参数
     * @return 返回结果
     */
    @Override
    public R update(MenuUpdateDTO menuUpdateDTO) {
        // 根据ID查询信息
        Menu menu = getInfo(menuUpdateDTO.getId());
        if (StringUtils.isNull(menu)) {
            return R.failed("记录不存在");
        }
        // 属性拷贝
        BeanUtils.copyProperties(menuUpdateDTO, menu);
        boolean result = updateById(menu);
        if (!result) {
            return R.failed();
        }
        return R.ok(menu.getId());
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 返回结果
     */
    @Override
    public R delete(Integer id) {
        // 删除ID判空
        if (StringUtils.isNull(id) || id <= 0) {
            return R.failed("删除记录ID不存在");
        }
        // 查询菜单
        Menu menu = getInfo(id);
        if (StringUtils.isNull(menu)) {
            return R.failed("记录不存在");
        }
        // 判断是否存在子级
        long count = count(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, menu.getId())
                .eq(Menu::getDelFlag, 0));
        if (count > 0) {
            return R.failed("存在子级，无法删除");
        }
        // 删除菜单
        boolean result = removeById(id);
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 获取后台菜单列表
     *
     * @param userId 用户ID
     * @return 返回结果
     */
    @Override
    public List<MenuListVO> getMenus(Integer userId) {
        List<MenuListVO> menuList = null;
        if (userId.equals(1)) {
            menuList = getChildMenu(0);
        } else {
            // 获取用户权限菜单数据
            menuList = getMenusByUserId(userId, 0);
        }
        // 返回结果
        return menuList;
    }

    /**
     * 根据上级ID获取子级菜单
     *
     * @param parentId 上级ID
     * @return 返回结果
     */
    public List<MenuListVO> getChildMenu(Integer parentId) {
        // 获取菜单列表
        List<Menu> menuList = list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, parentId)
                // 菜单状态：0-显示 1-不显示
                .eq(Menu::getStatus, 0)
                // 是否可见：0-显示 1-隐藏
                .eq(Menu::getHide, 0)
                // 菜单类型：0-菜单 1-节点
                .eq(Menu::getType, 0)
                .eq(Menu::getDelFlag, 0)
                .orderByAsc(Menu::getSort));
        // 实例化菜单VO列表
        List<MenuListVO> menuListVOList = new ArrayList<>();
        // 查询结果判空
        if (!menuList.isEmpty()) {
            // 遍历菜单数据
            for (Menu menu : menuList) {
                MenuListVO menuListVO = new MenuListVO();
                BeanUtils.copyProperties(menu, menuListVO);
                // 获取子级菜单列表
                List<MenuListVO> childrenList = getChildMenu(menu.getId());
                menuListVO.setChildren(childrenList);
                // 加入列表
                menuListVOList.add(menuListVO);
            }
        }
        // 返回结果
        return menuListVOList;
    }

    /**
     * 根据用户ID获取子级数据源
     *
     * @param userId   用户ID
     * @param parentId 上级ID
     * @return 返回结果
     */
    private List<MenuListVO> getMenusByUserId(Integer userId, Integer parentId) {
        // 获取用户权限菜单数据
        List<MenuListVO> menuList = menuMapper.getMenusByUserId(userId, parentId);
        if (StringUtils.isNotEmpty(menuList)) {
            // 遍历菜单数据
            for (MenuListVO menuListVO : menuList) {
                // 获取子级菜单
                List<MenuListVO> childMenu = getMenusByUserId(userId, menuListVO.getId());
                // 设置子级数据
                menuListVO.setChildren(childMenu);
            }
        }
        // 返回结果
        return menuList;
    }

    /**
     * 根据用户ID获取权限节点
     *
     * @param userId 用户ID
     * @return 返回结果
     */
    @Override
    public List<String> getPermissions(Integer userId) {
        // 实例化权限节点列表
        List<String> permissions = new ArrayList<>();
        if (userId.equals(1)) {
            // 超级管理员
            List<Menu> menuList = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    // 菜单类型：0-菜单 1-节点
                    .eq(Menu::getType, 1)
                    .eq(Menu::getDelFlag, 0)
                    .orderByAsc(Menu::getSort));
            if (!menuList.isEmpty()) {
                // 获取菜单节点集合
                permissions = menuList.stream().map(Menu::getPermission).collect(Collectors.toList());
            }
        } else {
            // 查询非超管用户节点权限
            permissions = menuMapper.getPermissions(userId);
        }
        // 返回结果
        return permissions;
    }

    /**
     * 获取菜单列表
     *
     * @param tenantId 租户ID
     * @return 返回结果
     */
    @Override
    public List<Menu> getMenuList(Integer tenantId) {
        if (tenantId.equals(1)) {
            // 系统默认租户ID=1
            List<Menu> menuList = list(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getDelFlag, 0)
                    .orderByAsc(Menu::getId));
            return menuList;
        } else {
            // 普通租户
            List<Menu> menuList = roleMenuMapper.getTenantMenuList();
            return menuList;
        }
    }

    /**
     * 根据菜单路径查询菜单信息
     *
     * @param path 菜单路径
     * @return 返回结果
     */
    @Override
    public Menu getMenuByPath(String path) {
        Menu menu = getOne(new LambdaQueryWrapper<Menu>()
                        .eq(Menu::getPath, path)
                        .eq(Menu::getDelFlag, 0)
                , false);
        return menu;
    }

    /**
     * 根据父级ID删除菜单
     *
     * @param parentId 父级ID
     * @return 返回结果
     */
    @Override
    public R deleteByParentId(Integer parentId) {
        if (parentId == null) {
            return R.failed("父级ID不能为空");
        }
        if (parentId == 0) {
            return R.failed("父级ID不能为0");
        }
        // 根据条件删除
        boolean result = remove(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, parentId));
        if (!result) {
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 批量添加菜单
     *
     * @param menuList 菜单列表
     * @return 返回结果
     */
    @Override
    public R batchAddMenu(List<Menu> menuList) {
        if (Collections.isEmpty(menuList)) {
            return R.failed("菜单列表不能为空");
        }
        try {
            boolean result = saveBatch(menuList);
            if (!result) {
                return R.failed();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return R.ok();
    }
}
