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

package com.xiaomayi.admin.controller.demo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaomayi.admin.dto.MenuCreateVO;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.security.utils.SecurityUtils;
import com.xiaomayi.system.entity.City;
import com.xiaomayi.system.entity.Menu;
import com.xiaomayi.system.service.CityService;
import com.xiaomayi.system.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 菜单创建 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/menu2")
@AllArgsConstructor
public class Menu2Controller {

    private final MenuService menuService;
    private final CityService cityService;

    /**
     * 创建菜单
     *
     * @return 返回结果
     */
    @GetMapping("/createMenu")
    public R createMenu() {

        // 创建一级菜单
        List<MenuCreateVO> menuList = new ArrayList<>();

        // 控制面板
        MenuCreateVO menu0 = new MenuCreateVO();
        menu0.setName("控制面板");
        menu0.setIcon("el-icon-Bicycle");
        menu0.setPath("/dashboard");
        menu0.setType(0);
        menuList.add(menu0);

        // 二级菜单
        List<MenuCreateVO> menuList0 = new ArrayList<>();
        // 主控制台
        MenuCreateVO menu01 = new MenuCreateVO();
        menu01.setName("主控台");
        menu01.setIcon("el-icon-Bicycle");
        menu01.setPath("/console");
        menu01.setType(0);
        menuList0.add(menu01);
        // 监控面板
        MenuCreateVO menu02 = new MenuCreateVO();
        menu02.setName("监控页");
        menu02.setIcon("el-icon-Bicycle");
        menu02.setPath("/monitor");
        menu02.setType(0);
        menuList0.add(menu02);
        // 工作台
        MenuCreateVO menu03 = new MenuCreateVO();
        menu03.setName("工作台");
        menu03.setIcon("el-icon-Bicycle");
        menu03.setPath("/workplace");
        menu03.setType(0);
        menuList0.add(menu03);

        // 工作台
        MenuCreateVO menu04 = new MenuCreateVO();
        menu04.setName("我的消息");
        menu04.setIcon("el-icon-Bicycle");
        menu04.setPath("/message");
        menu04.setType(0);
        menuList0.add(menu04);

        // 加入二级菜单
        menu0.setChildList(menuList0);

        // 系统管理
        MenuCreateVO menu1 = new MenuCreateVO();
        menu1.setName("系统管理");
        menu1.setIcon("el-icon-Bicycle");
        menu1.setPath("/system");
        menu1.setType(0);
        menuList.add(menu1);

        // 二级菜单
        List<MenuCreateVO> menuList1 = new ArrayList<>();
        // 用户管理
        MenuCreateVO menu11 = new MenuCreateVO();
        menu11.setName("用户管理");
        menu11.setIcon("el-icon-Bicycle");
        menu11.setPath("/user");
        menu11.setType(0);
        menuList1.add(menu11);

        // 角色管理
        MenuCreateVO menu12 = new MenuCreateVO();
        menu12.setName("角色管理");
        menu12.setIcon("el-icon-Bicycle");
        menu12.setPath("/role");
        menu12.setType(0);
        menuList1.add(menu12);

        // 菜单管理
        MenuCreateVO menu13 = new MenuCreateVO();
        menu13.setName("菜单管理");
        menu13.setIcon("el-icon-Bicycle");
        menu13.setPath("/menu");
        menu13.setType(0);
        menuList1.add(menu13);

        // 部门管理
        MenuCreateVO menu14 = new MenuCreateVO();
        menu14.setName("部门管理");
        menu14.setIcon("el-icon-Bicycle");
        menu14.setPath("/dept");
        menu14.setType(0);
        menuList1.add(menu14);

        // 职级管理
        MenuCreateVO menu15 = new MenuCreateVO();
        menu15.setName("职级管理");
        menu15.setIcon("el-icon-Bicycle");
        menu15.setPath("/level");
        menu15.setType(0);
        menuList1.add(menu15);

        // 岗位管理
        MenuCreateVO menu16 = new MenuCreateVO();
        menu16.setName("岗位管理");
        menu16.setIcon("el-icon-Bicycle");
        menu16.setPath("/position");
        menu16.setType(0);
        menuList1.add(menu16);

        // 租户管理
        MenuCreateVO menu18 = new MenuCreateVO();
        menu18.setName("租户管理");
        menu18.setIcon("el-icon-Bicycle");
        menu18.setPath("/tenant");
        menu18.setType(0);
        menuList1.add(menu18);

        // 日志管理
        MenuCreateVO menu17 = new MenuCreateVO();
        menu17.setName("日志管理");
        menu17.setIcon("el-icon-Bicycle");
        menu17.setPath("/logger");
        menu17.setType(0);
        menu17.setChild(true);
        menuList1.add(menu17);
        // 加入二级菜单
        menu1.setChildList(menuList1);

        // 三级菜单
        List<MenuCreateVO> menuList2 = new ArrayList<>();
        // 登录日志
        MenuCreateVO menu171 = new MenuCreateVO();
        menu171.setName("登录日志");
        menu171.setIcon("el-icon-Bicycle");
        menu171.setPath("/loginLog");
        menu171.setType(0);
        menuList2.add(menu171);

        // 操作日志
        MenuCreateVO menu172 = new MenuCreateVO();
        menu172.setName("操作日志");
        menu172.setIcon("el-icon-Bicycle");
        menu172.setPath("/operLog");
        menu172.setType(0);
        menuList2.add(menu172);

        // 加入三级菜单
        menu17.setChildList(menuList2);

        // 数据管理
        MenuCreateVO menu2 = new MenuCreateVO();
        menu2.setName("数据管理");
        menu2.setIcon("el-icon-Bicycle");
        menu2.setPath("/data");
        menu2.setType(0);
        menuList.add(menu2);

        // 二级菜单
        List<MenuCreateVO> menuList3 = new ArrayList<>();
        // 参数管理
        MenuCreateVO menu21 = new MenuCreateVO();
        menu21.setName("参数管理");
        menu21.setIcon("el-icon-Bicycle");
        menu21.setPath("/param");
        menu21.setType(0);
        menuList3.add(menu21);

        // 配置管理
        MenuCreateVO menu22 = new MenuCreateVO();
        menu22.setName("配置管理");
        menu22.setIcon("el-icon-Bicycle");
        menu22.setPath("/config");
        menu22.setType(0);
        menuList3.add(menu22);

        // 字典管理
        MenuCreateVO menu23 = new MenuCreateVO();
        menu23.setName("字典管理");
        menu23.setIcon("el-icon-Bicycle");
        menu23.setPath("/dict");
        menu23.setType(0);
        menuList3.add(menu23);

        // 通知公告
        MenuCreateVO menu24 = new MenuCreateVO();
        menu24.setName("通知公告");
        menu24.setIcon("el-icon-Bicycle");
        menu24.setPath("/notice");
        menu24.setType(0);
        menuList3.add(menu24);

        // 行政区划
        MenuCreateVO menu25 = new MenuCreateVO();
        menu25.setName("行政区划");
        menu25.setIcon("el-icon-Bicycle");
        menu25.setPath("/city");
        menu25.setType(0);
        menuList3.add(menu25);

        // 消息管理
        MenuCreateVO menu26 = new MenuCreateVO();
        menu26.setName("消息管理");
        menu26.setIcon("el-icon-Bicycle");
        menu26.setPath("/message");
        menu26.setType(0);
        menuList3.add(menu26);

        // 加入二级菜单
        menu2.setChildList(menuList3);

        // 日志管理
        MenuCreateVO menu3 = new MenuCreateVO();
        menu3.setName("日志管理");
        menu3.setIcon("el-icon-Bicycle");
        menu3.setPath("/logger");
        menu3.setType(0);
        menuList.add(menu3);

        // 二级菜单
        List<MenuCreateVO> menuList4 = new ArrayList<>();
        // 短信日志
        MenuCreateVO menu31 = new MenuCreateVO();
        menu31.setName("短信日志");
        menu31.setIcon("el-icon-Bicycle");
        menu31.setPath("/smsLog");
        menu31.setType(0);
        menuList4.add(menu31);

        // 邮件日志
        MenuCreateVO menu32 = new MenuCreateVO();
        menu32.setName("邮件日志");
        menu32.setIcon("el-icon-Bicycle");
        menu32.setPath("/emailLog");
        menu32.setType(0);
        menuList4.add(menu32);

        // 文件日志
        MenuCreateVO menu33 = new MenuCreateVO();
        menu33.setName("文件日志");
        menu33.setIcon("el-icon-Bicycle");
        menu33.setPath("/fileLog");
        menu33.setType(0);
        menuList4.add(menu33);

        // 加入二级菜单
        menu3.setChildList(menuList4);

        // 文件管理
        MenuCreateVO menu4 = new MenuCreateVO();
        menu4.setName("文件管理");
        menu4.setIcon("el-icon-Bicycle");
        menu4.setPath("/file");
        menu4.setType(0);
        menuList.add(menu4);

        // 二级菜单
        List<MenuCreateVO> menuList5 = new ArrayList<>();

        // 文件模板
        MenuCreateVO menu42 = new MenuCreateVO();
        menu42.setName("文件模板");
        menu42.setIcon("el-icon-Bicycle");
        menu42.setPath("/fileTemplate");
        menu42.setType(0);
        menuList5.add(menu42);

        // 邮件模板
        MenuCreateVO menu43 = new MenuCreateVO();
        menu43.setName("邮件模板");
        menu43.setIcon("el-icon-Bicycle");
        menu43.setPath("/emailTemplate");
        menu43.setType(0);
        menuList5.add(menu43);

        // 短信模板
        MenuCreateVO menu44 = new MenuCreateVO();
        menu44.setName("短信模板");
        menu44.setIcon("el-icon-Bicycle");
        menu44.setPath("/smsTemplate");
        menu44.setType(0);
        menuList5.add(menu44);

        // 消息模板
        MenuCreateVO menu45 = new MenuCreateVO();
        menu45.setName("消息模板");
        menu45.setIcon("el-icon-Bicycle");
        menu45.setPath("/messageTemplate");
        menu45.setType(0);
        menuList5.add(menu45);

        // 加入二级菜单
        menu4.setChildList(menuList5);

        // 系统配置
        MenuCreateVO menu5 = new MenuCreateVO();
        menu5.setName("系统设置");
        menu5.setIcon("el-icon-Bicycle");
        menu5.setPath("/setting");
        menu5.setType(0);
        menuList.add(menu5);

        // 二级菜单
        List<MenuCreateVO> menuList6 = new ArrayList<>();
        // 个人设置
        MenuCreateVO menu51 = new MenuCreateVO();
        menu51.setName("个人设置");
        menu51.setIcon("el-icon-Bicycle");
        menu51.setPath("/profile");
        menu51.setType(0);
        menuList6.add(menu51);
        // 系统设置
        MenuCreateVO menu52 = new MenuCreateVO();
        menu52.setName("系统设置");
        menu52.setIcon("el-icon-Bicycle");
        menu52.setPath("/configweb");
        menu52.setType(0);
        menuList6.add(menu52);

        // 加入二级菜单
        menu5.setChildList(menuList6);

        // CMS管理
        MenuCreateVO menu9 = new MenuCreateVO();
        menu9.setName("资讯管理");
        menu9.setIcon("el-icon-Bicycle");
        menu9.setPath("/content");
        menu9.setType(0);
        menuList.add(menu9);

        // 二级菜单
        List<MenuCreateVO> menuList9 = new ArrayList<>();
        // 分类管理
        MenuCreateVO menu91 = new MenuCreateVO();
        menu91.setName("分类管理");
        menu91.setIcon("el-icon-Bicycle");
        menu91.setPath("/category");
        menu91.setType(0);
        menuList9.add(menu91);
        // 标签管理
        MenuCreateVO menu92 = new MenuCreateVO();
        menu92.setName("标签管理");
        menu92.setIcon("el-icon-Bicycle");
        menu92.setPath("/tag");
        menu92.setType(0);
        menuList9.add(menu92);
        // 文章管理
        MenuCreateVO menu93 = new MenuCreateVO();
        menu93.setName("文章管理");
        menu93.setIcon("el-icon-Bicycle");
        menu93.setPath("/article");
        menu93.setType(0);
        menuList9.add(menu93);
        // 友情链接
        MenuCreateVO menu94 = new MenuCreateVO();
        menu94.setName("友情链接");
        menu94.setIcon("el-icon-Bicycle");
        menu94.setPath("/link");
        menu94.setType(0);
        menuList9.add(menu94);
        // 布局管理
        MenuCreateVO menu95 = new MenuCreateVO();
        menu95.setName("布局管理");
        menu95.setIcon("el-icon-Bicycle");
        menu95.setPath("/layout");
        menu95.setType(0);
        menuList9.add(menu95);
        // 布局推荐
        MenuCreateVO menu96 = new MenuCreateVO();
        menu96.setName("布局推荐");
        menu96.setIcon("el-icon-Bicycle");
        menu96.setPath("/layoutItem");
        menu96.setType(0);
        menuList9.add(menu96);
        // 广告位管理
        MenuCreateVO menu97 = new MenuCreateVO();
        menu97.setName("广告位管理");
        menu97.setIcon("el-icon-Bicycle");
        menu97.setPath("/adSort");
        menu97.setType(0);
        menuList9.add(menu97);
        // 广告管理
        MenuCreateVO menu98 = new MenuCreateVO();
        menu98.setName("广告管理");
        menu98.setIcon("el-icon-Bicycle");
        menu98.setPath("/ad");
        menu98.setType(0);
        menuList9.add(menu98);

        // 加入二级菜单
        menu9.setChildList(menuList9);

        // 系统监控
        MenuCreateVO menu6 = new MenuCreateVO();
        menu6.setName("系统监控");
        menu6.setIcon("el-icon-Bicycle");
        menu6.setPath("/monitor");
        menu6.setType(0);
        menuList.add(menu6);

        // 二级菜单
        List<MenuCreateVO> menuList7 = new ArrayList<>();
        // 数据源管理
        MenuCreateVO menu61 = new MenuCreateVO();
        menu61.setName("数据源管理");
        menu61.setIcon("el-icon-Bicycle");
        menu61.setPath("/dataSource");
        menu61.setType(0);
        menuList7.add(menu61);

        // 定时任务
        MenuCreateVO menu62 = new MenuCreateVO();
        menu62.setName("定时任务");
        menu62.setIcon("el-icon-Bicycle");
        menu62.setPath("/job");
        menu62.setType(0);
        menuList7.add(menu62);

        // 在线用户
        MenuCreateVO menu63 = new MenuCreateVO();
        menu63.setName("在线用户");
        menu63.setIcon("el-icon-Bicycle");
        menu63.setPath("/online");
        menu63.setType(0);
        menuList7.add(menu63);

        // SQL监控
        MenuCreateVO menu64 = new MenuCreateVO();
        menu64.setName("SQL监控");
        menu64.setIcon("el-icon-Bicycle");
        menu64.setPath("/druid");
        menu64.setComponent("/druid");
        menu64.setType(0);
        menu64.setTarget(1);
        menuList7.add(menu64);

        // 性能监控
        MenuCreateVO menu65 = new MenuCreateVO();
        menu65.setName("性能监控");
        menu65.setIcon("el-icon-Bicycle");
        menu65.setPath("/server");
        menu65.setType(0);
        menuList7.add(menu65);

        // 监控管理
        MenuCreateVO menu68 = new MenuCreateVO();
        menu68.setName("监控管理");
        menu68.setIcon("el-icon-Bicycle");
        menu68.setPath("/admin");
        menu68.setComponent("/admin");
        menu68.setType(0);
        menu68.setTarget(1);
        menuList7.add(menu68);

        // 缓存监控
        MenuCreateVO menu66 = new MenuCreateVO();
        menu66.setName("缓存监控");
        menu66.setIcon("el-icon-Bicycle");
        menu66.setPath("/cache");
        menu66.setType(0);
        menuList7.add(menu66);

        // 缓存管理
        MenuCreateVO menu67 = new MenuCreateVO();
        menu67.setName("缓存管理");
        menu67.setIcon("el-icon-Bicycle");
        menu67.setPath("/caches");
        menu67.setType(0);
        menuList7.add(menu67);

        // 加入二级菜单
        menu6.setChildList(menuList7);

        // 开发工具
        MenuCreateVO menu7 = new MenuCreateVO();
        menu7.setName("开发工具");
        menu7.setIcon("el-icon-Bicycle");
        menu7.setPath("/tool");
        menu7.setType(0);
        menuList.add(menu7);

        // 二级菜单
        List<MenuCreateVO> menuList8 = new ArrayList<>();
//        // 模板文件
//        MenuCreateVO menu72 = new MenuCreateVO();
//        menu72.setName("模板文件");
//        menu72.setIcon("el-icon-Bicycle");
//        menu72.setPath("/template");
//        menu72.setType(0);
//        menuList8.add(menu72);
        // 代码工具
        MenuCreateVO menu71 = new MenuCreateVO();
        menu71.setName("代码工具");
        menu71.setIcon("el-icon-Bicycle");
        menu71.setPath("/generator");
        menu71.setType(0);
        menuList8.add(menu71);
        // 接口文档
        MenuCreateVO menu73 = new MenuCreateVO();
        menu73.setName("接口文档");
        menu73.setIcon("el-icon-Bicycle");
        menu73.setPath("/doc.html");
        menu73.setComponent("/doc.html");
        menu73.setType(0);
        menu73.setTarget(1);
        menuList8.add(menu73);

        // 加入二级菜单
        menu7.setChildList(menuList8);

        // 公共接口
        MenuCreateVO menu8 = new MenuCreateVO();
        menu8.setName("公共接口");
        menu8.setIcon("el-icon-Bicycle");
        menu8.setPath("/public");
        menu8.setType(0);
        menuList.add(menu8);

        // 二级菜单
        List<MenuCreateVO> menuList10 = new ArrayList<>();

        // 文件上传
        MenuCreateVO menu81 = new MenuCreateVO();
        menu81.setName("文件上传");
        menu81.setIcon("el-icon-Bicycle");
        menu81.setPath("/upload");
        menu81.setType(0);
        menu81.setHide(1);
        menuList10.add(menu81);

        // 加入二级菜单
        menu8.setChildList(menuList10);

        // 官方网址
        MenuCreateVO menu102 = new MenuCreateVO();
        menu102.setName("官方网址");
        menu102.setIcon("el-icon-Bicycle");
        menu102.setPath("/website");
        menu102.setComponent("https://www.xiaomayicloud.com");
        menu102.setType(0);
        menu102.setTarget(2);
        menuList.add(menu102);

        // 获取授权
        MenuCreateVO menu10 = new MenuCreateVO();
        menu10.setName("获取授权");
        menu10.setIcon("el-icon-Bicycle");
        menu10.setPath("/authorization");
        menu10.setComponent("https://www.xiaomayicloud.com/goods/detail/1");
        menu10.setType(0);
        menu10.setTarget(2);
        menuList.add(menu10);

        // 部署文档
        MenuCreateVO menu103 = new MenuCreateVO();
        menu103.setName("部署文档");
        menu103.setIcon("el-icon-Bicycle");
        menu103.setPath("/docs");
        menu103.setComponent("http://docs.elevue.xiaomayicloud.com");
        menu103.setType(0);
        menu103.setTarget(2);
        menuList.add(menu103);

        // 遍历数据源
        for (MenuCreateVO menuCreateVO : menuList) {
            // 一级菜单处理
            Menu menu = new Menu();
            BeanUtils.copyProperties(menuCreateVO, menu);
            menu.setCreateUser(SecurityUtils.getUsername());
            menu.setCreateTime(LocalDateTime.now());
            boolean saveRes = menuService.save(menu);
            if (!saveRes) {
                continue;
            }

            // 二级菜单处理
            List<MenuCreateVO> childList = menuCreateVO.getChildList();
            if (StringUtils.isNotEmpty(childList)) {
                // 遍历二级菜单
                for (MenuCreateVO createVO : childList) {
                    // 菜单名称
                    String menuName = createVO.getName().replace("管理", "");
                    // 模块名称
                    String moduleName = createVO.getPath().replace("/", "");

                    Menu menu30 = new Menu();
                    BeanUtils.copyProperties(createVO, menu30);
                    if (StringUtils.isNull(createVO.getTarget())) {
                        // 文件路径
                        menu30.setPath(menu.getPath() + menu30.getPath());
                        // 组件路径
                        menu30.setComponent(menu30.getPath() + "/index");
                    }
                    // 上级菜单ID
                    menu30.setParentId(menu.getId());
                    menu30.setCreateUser(SecurityUtils.getUsername());
                    menu30.setCreateTime(LocalDateTime.now());
                    menuService.save(menu30);

                    // 判断是否有子级
                    if (createVO.isChild()) {
                        // 二级菜单处理
                        List<MenuCreateVO> childList2 = createVO.getChildList();
                        // 遍历三级菜单
                        for (MenuCreateVO vo : childList2) {
                            // 菜单名称
                            String menuName2 = vo.getName().replace("管理", "");
                            // 模块名称
                            String moduleName2 = vo.getPath().replace("/", "");

                            Menu menu301 = new Menu();
                            BeanUtils.copyProperties(vo, menu301);
                            // 上级菜单ID
                            menu301.setParentId(menu30.getId());
                            // 文件路径
                            menu301.setPath(menu30.getPath() + menu301.getPath());
                            // 组件路径
                            menu301.setComponent(menu301.getPath());
                            menu301.setCreateUser(SecurityUtils.getUsername());
                            menu301.setCreateTime(LocalDateTime.now());
                            boolean result2 = menuService.save(menu301);
                            if (!result2) {
                                continue;
                            }
                            if (StringUtils.isNull(vo.getTarget())) {
                                createPermission(menuName2, moduleName2, menu301.getId());
                            }
                        }
                    } else {
                        if (StringUtils.isNull(createVO.getTarget()) && !"/dashboard".equals(menuCreateVO.getPath())) {
                            createPermission(menuName, moduleName, menu30.getId());
                        }
                    }
                }
            }
        }
        return R.ok();
    }

    /**
     * 创建权限节点
     *
     * @param menuName   菜单名称
     * @param moduleName 模块名称
     * @param menuId     菜单ID
     */
    private void createPermission(String menuName, String moduleName, Integer menuId) {
        // 文档是外链，无需生成节点
        if ("document".equals(moduleName)) {
            return;
        }
        // 创建权限节点
        LinkedHashMap<String, String> permission = new LinkedHashMap<>();
        if (!(Arrays.asList(new String[]{"profile", "configWeb", "online", "druid", "server", "admin", "cache", "caches", "upload"})).contains(moduleName)) {
            permission.put(StrUtil.format("查询{}分页", menuName), "page");
            permission.put(StrUtil.format("查询{}列表", menuName), "list");
            permission.put(StrUtil.format("查询{}详情", menuName), "detail");
            permission.put(StrUtil.format("添加{}", menuName), "add");
            permission.put(StrUtil.format("更新{}", menuName), "update");

            // 设置状态
            if ((Arrays.asList(new String[]{"ad", "article", "link", "configItem", "level", "notice", "param", "position", "tenant", "user"})).contains(moduleName)) {
                permission.put("设置状态", "status");
            }

            permission.put(StrUtil.format("删除{}", menuName), "delete");
            permission.put(StrUtil.format("批量删除{}", menuName), "batchDelete");
        }
        if ((Arrays.asList(new String[]{"menu", "dept", "city"})).contains(moduleName)) {
            permission.put(StrUtil.format("添加子级{}", menuName), "addz");
            permission.put(StrUtil.format("全部展开{}", menuName), "expand");
            permission.put(StrUtil.format("全部折叠{}", menuName), "collapse");
        }
        if ((Arrays.asList(new String[]{"role"})).contains(moduleName)) {
            permission.put("获取权限", "getPermission");
            permission.put("设置权限", "savePermission");
        }
        if ((Arrays.asList(new String[]{"user"})).contains(moduleName)) {
            // 重置密码
            permission.put("重置密码", "resetPwd");
        }
        // 导入、导出
        if ((Arrays.asList(new String[]{"user", "level"})).contains(moduleName)) {
            permission.put(StrUtil.format("导入Excel", menuName), "import");
            permission.put(StrUtil.format("导出Excel", menuName), "export");
        }
        // 个人设置
        if ((Arrays.asList(new String[]{"profile"})).contains(moduleName)) {
            permission.put("获取设置", "index");
            permission.put("保存设置", "update");
        }
        // 系统设置
        if ((Arrays.asList(new String[]{"configWeb"})).contains(moduleName)) {
            permission.put("获取设置", "index");
            permission.put("保存设置", "save");
        }
        // 租户
        if ((Arrays.asList(new String[]{"tenant"})).contains(moduleName)) {
            permission.put("创建租户账号", "account");
        }
        // 字典
        if ((Arrays.asList(new String[]{"dict"})).contains(moduleName)) {
            permission.put("更新缓存", "cache");
        }
        // 定时任务
        if ((Arrays.asList(new String[]{"job"})).contains(moduleName)) {
            permission.put("查看日志", "jobLog");
            permission.put("执行一次", "runOnce");
            permission.put("暂停", "pause");
            permission.put("恢复", "resume");
        }
        // 在线用户
        if ((Arrays.asList(new String[]{"online"})).contains(moduleName)) {
            permission.put(StrUtil.format("查询{}", menuName), "list");
            permission.put(StrUtil.format("强退{}", menuName), "logout");
        }
        // SQL监控
        if ((Arrays.asList(new String[]{"druid"})).contains(moduleName)) {
            permission.put("查询SQL监控", "data");
        }
        // 性能监控
        if ((Arrays.asList(new String[]{"server"})).contains(moduleName)) {
            permission.put("查询性能监控", "info");
        }
        // 监控管理
        if ((Arrays.asList(new String[]{"admin"})).contains(moduleName)) {
            permission.put("查询监控管理", "admin");
        }
        // 缓存监控
        if ((Arrays.asList(new String[]{"cache"})).contains(moduleName)) {
            permission.put("查询缓存信息", "info");
        }
        // 缓存管理
        if ((Arrays.asList(new String[]{"caches"})).contains(moduleName)) {
            permission.put("查询缓存信息", "info");
            permission.put("查询缓存列表", "list");
            permission.put("查询缓存KEY列表", "key");
            permission.put("查询缓存值", "value");
        }
        // 代码生成
        if ((Arrays.asList(new String[]{"generator"})).contains(moduleName)) {
            permission.put("一键生成", "generator");
            permission.put("批量生成", "batchGenerator");
        }
        // 我的消息
        if ((Arrays.asList(new String[]{"message"})).contains(moduleName)) {
            permission.put("发送消息", "send");
            permission.put("设置已读", "read");
        }
        // 上传文件
        if ((Arrays.asList(new String[]{"upload"})).contains(moduleName)) {
            permission.put("单个文件上传", "file");
            permission.put("批量文件上传", "files");
        }

        // 排序号
        Integer sort = 1;
        // 权限节点遍历
        for (Map.Entry<String, String> entry : permission.entrySet()) {
            // KEY
            String key = entry.getKey();
            // Val
            String value = entry.getValue();
            // 创建节点
            Menu pmsMenu = new Menu();
            pmsMenu.setName(key);
            pmsMenu.setPath(StrUtil.format("/{}/{}", moduleName, value));
            pmsMenu.setParentId(menuId);
            pmsMenu.setPermission(StrUtil.format("sys:{}:{}", moduleName, value));
            pmsMenu.setType(1);
            pmsMenu.setSort(sort * 5);
            pmsMenu.setCreateUser(SecurityUtils.getUsername());
            pmsMenu.setCreateTime(LocalDateTime.now());
            boolean result = menuService.save(pmsMenu);
            if (!result) {
                continue;
            }
            // 计数器+1
            sort++;
        }
    }

    @GetMapping("/updateCity/{areaCode}")
    public List<City> updateCity(@PathVariable String areaCode) {
        for (int i = 0; i < 80; i++) {
            // 起始
            Integer startNUm = i * 10000;
            // 结束
            Integer endNUm = (i + 1) * 10000;
            List<City> cityList = cityService.list(new LambdaQueryWrapper<City>()
                    .between(City::getId, startNUm, endNUm)
                    .eq(City::getDelFlag, 0)
                    .orderByAsc(City::getId));
            if (StringUtils.isNotEmpty(cityList)) {
                for (City city : cityList) {
                    City city1 = cityService.getOne(new LambdaQueryWrapper<City>()
                            .eq(City::getAreaCode, city.getParentCode())
                            .eq(City::getDelFlag, 0), false);
                    if (StringUtils.isNull(city1)) {
                        continue;
                    }
                    city.setPid(city1.getId());
                    city.setUpdateUser(SecurityUtils.getUsername());
                    city.setUpdateTime(LocalDateTime.now());
                    cityService.updateById(city);
                }
            }
        }
        return null;
    }

}
