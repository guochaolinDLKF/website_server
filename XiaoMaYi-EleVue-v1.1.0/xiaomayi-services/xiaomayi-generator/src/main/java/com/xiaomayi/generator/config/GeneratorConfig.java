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

package com.xiaomayi.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * <p>
 * 代码工具配置类
 * </p>
 * 特别注意：依赖非静态set方法，切记不然无法读取值
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-24
 */
@Configuration
@ConfigurationProperties(prefix = "generator")
public class GeneratorConfig {

    /**
     * 软件作者
     */
    private static String author;

    /**
     * 服务层路径
     */
    private static String serviceModelName;

    /**
     * 服务层包名
     */
    private static String servicePackageName;

    /**
     * 应用层路径
     */
    private static String moduleModelName;

    /**
     * 应用层路径包名
     */
    private static String modulePackageName;

    /**
     * 生成文件是否移除前缀，默认：false
     */
    private static boolean removePrefix;

    /**
     * 数据表前缀
     */
    private static String tablePrefix;

    public static String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        GeneratorConfig.author = author;
    }

    public static String getServiceModelName() {
        return serviceModelName;
    }

    public void setServiceModelName(String serviceModelName) {
        GeneratorConfig.serviceModelName = serviceModelName;
    }

    public static String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        GeneratorConfig.servicePackageName = servicePackageName;
    }

    public static String getModuleModelName() {
        return moduleModelName;
    }

    public void setModuleModelName(String moduleModelName) {
        GeneratorConfig.moduleModelName = moduleModelName;
    }

    public static String getModulePackageName() {
        return modulePackageName;
    }

    public void setModulePackageName(String modulePackageName) {
        GeneratorConfig.modulePackageName = modulePackageName;
    }

    public static boolean getRemovePrefix() {
        return removePrefix;
    }

    public void setRemovePrefix(boolean removePrefix) {
        GeneratorConfig.removePrefix = removePrefix;
    }

    public static String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        GeneratorConfig.tablePrefix = tablePrefix;
    }
}
