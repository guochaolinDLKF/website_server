package com.xiaomayi.admin.controller.demo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaomayi.admin.entity.DataModel;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.ratelimiter.annotation.RateLimiter;
import com.xiaomayi.system.entity.DictItem;
import com.xiaomayi.system.entity.User;
import com.xiaomayi.system.service.UserService;
import com.xiaomayi.system.utils.DictResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 测试案例 前端控制器
 * 特别备注：此文件非项目本身有效文件，仅仅是工程师编写测试案例使用，留个备份未删除
 * 实际项目使用时请删除此文件，以免造成其他影响
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {

    private final UserService userService;

    /**
     * 全局限流
     *
     * @param value 参数值
     * @return 返回结果
     */
    @RateLimiter(count = 2, time = 10)
    @GetMapping("/ratelimiter")
    public R test(String value) {
        return R.ok("操作成功", value);
    }

    /**
     * 权限节点控制测试
     *
     * @return 返回结果
     */
    @GetMapping("/permission")
//    @PreAuthorize("hasAuthority('sys:demo:index')")
//    // 满足其一即可访问资源
//    @PreAuthorize("hasAnyAuthority('test','admin','sys:demo:index')")
//    @PreAuthorize("hasRole('admin')")
//    @PreAuthorize("hasAnyRole('test','ADMIN')")
//    @PreAuthorize("@pms.hasAuthority('sys:demo:index')")
    public R hello() {
        return R.ok();
    }

    /**
     * 根据字典编码获取字典数据
     *
     * @param dictCode 字典编码
     * @return 返回结果
     */
    @GetMapping("/getDictItemsByCode/{dictCode}")
    public R getDictItemsByCode(@PathVariable String dictCode) {
        // 根据字典编码获取字典数据
        List<DictItem> dictItemList = DictResolver.getDictItemsByCode(dictCode);
        System.out.println(dictItemList);

        // 根据字典编码、字典项值查询字典项名称
        String itemName = DictResolver.getDictItemName(dictCode, "1");
        System.out.println(itemName);

        // 根据字典编码、字典项值获取字典数据
        DictItem dictItem = DictResolver.getDictItemByItemValue(dictCode, "1");
        System.out.println(dictItem);

        // 根据字典编码、字典项名称获取字典项值
        String itemValue = DictResolver.getDictItemValue(dictCode, "男");
        System.out.println(itemValue);

        // 根据字典编码、字典项名称获取字典数据
        DictItem dictItem1 = DictResolver.getDictItemByItemName(dictCode, "男");
        System.out.println(dictItem1);

        return R.ok();
    }

    /**
     * 数据脱敏验证案例
     *
     * @return 返回结果
     */
    @GetMapping("/sensitive")
    public R sensitive() {
        List<DataModel> list = new ArrayList<>();
        DataModel t1 = new DataModel();
        t1.setPassword("123456");
        t1.setEmail("xxx@163.com");
        t1.setPhone("18000000001");
        t1.setFixPhone("0928-1234567");
        t1.setBankCard("123456789012345");
        t1.setIdCard("52000000000000000X");
        t1.setName("张三");
        t1.setAddress("中国北京中国北京中国北京中国北京中国北京中国北京");
        t1.setHeadStr("测试头部脱敏");
        t1.setTailStr("测试尾部脱敏");
        t1.setMiddleStr("测试中间脱敏");
        t1.setHeadTailStr("测试头尾脱敏");
        t1.setAllStr("测试全部脱敏");
        t1.setNoneStr("测试不脱敏");

        // 加入列表
        list.add(t1);

        DataModel t2 = new DataModel();
        t2.setPassword("123456");
        t2.setEmail("xxx@163.com");
        t2.setPhone("18000000001");
        t2.setFixPhone("0928-1234567");
        t2.setBankCard("123456789012345");
        t2.setIdCard("52000000000000000X");
        t2.setName("张三");
        t2.setAddress("中国北京中国北京中国北京中国北京中国北京");
        t2.setHeadStr("测试头部脱敏");
        t2.setTailStr("测试尾部脱敏");
        t2.setMiddleStr("测试中间脱敏");
        t2.setHeadTailStr("测试头尾脱敏");
        t2.setAllStr("测试全部脱敏");
        t2.setNoneStr("测试不脱敏");
        // 加入列表
        list.add(t2);

        return R.ok(list);
    }

    /* 方法注解 */
    @GetMapping("/list")
    public List<User> list() {
        List<User> list = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getDelFlag, 0)
                .eq(User::getId, 1));

        // 切换从库获取
//        DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE.name());

        List<User> list2 = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getDelFlag, 0)
                .eq(User::getId, 1));

        // 切回主数据源
//        DataSourceContextHolder.clearDataSourceType();

        User user = userService.getById(1);

        return list;
    }

    /* 方法注解 */
    @GetMapping("/list2")
//    @Transactional(value = "masterTransaction", rollbackFor = Exception.class)
    //切换数据源后,查询所有用户
//    @DataSource(value = DataSourceType.SLAVE)
    public List<User> list2() {
        List<User> list = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getDelFlag, 0)
                .eq(User::getId, 1));
        return list;
    }

}
