// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2022~2024 小蚂蚁团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁团队 团队荣誉出品
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

import com.xiaomayi.admin.entity.TestModel;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.system.entity.DictItem;
import com.xiaomayi.system.utils.DictResolver;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 小蚂蚁团队
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/test/dict")
@AllArgsConstructor
public class TDictController {


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

    @GetMapping("/demo")
    public R demo() {
        List<TestModel> list = new ArrayList<>();
        TestModel t1 = new TestModel();
        t1.setPassword("123456");
        t1.setEmail("zzz@163.com");
        t1.setPhone("137654879451");
        t1.setFixPhone("0453-4785462");
        t1.setBankCard("622648754896457");
        t1.setIdCard("245874563214578965");
        t1.setName("张王钊");
        t1.setAddress("北京市昌平区xxx街道xxx小区1-1-101");
        t1.setHeadStr("测试头部脱敏");
        t1.setTailStr("测试尾部脱敏");
        t1.setMiddleStr("测试中间脱敏");
        t1.setHeadTailStr("测试头尾脱敏");
        t1.setAllStr("测试全部脱敏");
        t1.setNoneStr("测试不脱敏");

        // 加入列表
        list.add(t1);

        TestModel t2 = new TestModel();
        t2.setPassword("iscas123");
        t2.setEmail("xwg@sina.com");
        t2.setPhone("18547896547");
        t2.setFixPhone("010-62268795");
        t2.setBankCard("622648754896487");
        t2.setIdCard("100412547865478947");
        t2.setName("李二麻子");
        t2.setAddress("新疆省克拉玛依市xxx街道xxx小区1-1-101");
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

}
