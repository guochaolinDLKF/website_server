<div align="center">
    <p align="center">
        <img src="./upload/demo/logo.png" height="150" alt="logo"/>
    </p>
</div>

<h4 align="center">小蚂蚁云单体Vue3+ElementPlus前后端分离版</h4>

<p align="center">     
    <p align="center">
        <a href='https://gitee.com/xiaomayicloud/XiaoMaYi-EleVue/stargazers'>
            <img src='https://gitee.com/xiaomayicloud/XiaoMaYi-EleVue/badge/star.svg?theme=gray' alt='star'></img>
        </a>
        <a href='https://gitee.com/xiaomayicloud/XiaoMaYi-EleVue/members'>
            <img src='https://gitee.com/xiaomayicloud/XiaoMaYi-EleVue/badge/fork.svg?theme=gray' alt='fork'></img>
        </a>
    </p>
</p>

#### 项目介绍
基于SpringBoot3、SpringSecurity、MybatisPlus、Vue3、TypeScript、Vite、ElementPlus、MySQL等技术栈实现的单体前后端分离后台管理系统；后端基于Java语言采用SpringBoot3、SpringSecurity、MybatisPlus、MySQL等主流技术栈，前端基于Vue3、TypeScript、Vite等技术栈实现，采用ElementPlus前端UI框架，基于目前Vue前后端分离主流设计思想，为了实现精细化分工，模块化、组件化开发模式，目前采用完全前后端分离架构实现，前端应用通过API调用后端服务的方式实现数据交互；整套系统拥有完善的RBAC权限架构体系，权限颗粒度精细至按钮级别，支持多主题切换模式，多端兼容手机客户端、PAD平板、PC电脑等终端设备，提升了用户使用体验；同时为了简化开发，本身集成了基础模块，如用户模块、角色模块、菜单模块、部门模块、岗位模块、职级模块、日志模块、租户模块、字典模块、配置模块、行政区划、任务调度等基础功能模块； 为了支持个别企业和开发者多租户功能需求，在设计之初已重点设计并支持多租户功能；为了实现项目快速开发，官方自定义研发了整套代码生成器功能，可以根据单个规范的数据表结构一键生成模块的后端文件代码和前端模块代码；为了高度适配企业、政府和开发者定制化项目的需求，目前单体前后端分离架构发行了多个版本，根据实际需求按需选择即可，软件框架本身已集成了权限架构体系和基础功能模块，极大的提高了开发效率，降低了项目研发成本以及节省了人力。

#### 软件架构

+ 采用 `模块化`、`插件化` 设计，实现核心类库按需引入；
+ 采用 `Vue3`、`TypeScript`、`ElementPlus`、`Vite` 等技术实现完全前后端分离；
+ 采用 `MybatisPlus`、`hutool`、`fastjson2` 等主流开源框架；
+ 采用 `Json Web Token` 、`redis` 缓存技术等令牌生成机制；
+ 采用 `AOP` 切面技术实现日志及权限访问控制，颗粒度至按钮节点级别；
+ 采用 `Validator` 验证器，实现 `DTO` 参数严格校验；
+ 采用 `SpringDoc`、`knife4j` 实现在线文档查看；
+ 采用 `redis` 缓存技术记录登录信息，实现在线强制退出机制；
+ 采用 `mybatis-plus-generator` 实现代码生成器，一键生成模块文件；
+ 采用 `MybatisPlus` 实现多租户数据隔离策略；
+ 集成 `阿里短信`、`邮件` 等核心功能，实现业务解耦；
+ 集成 `在线监控` 技术，实现应用健康度、性能在线监控；
+ 集成 `Druid` 数据库连接池，用于管理数据库连接；

#### 软件信息

+ 软件名称：小蚂蚁云单体Vue3+ElementPlus版
+ 软件作者：@小蚂蚁云团队
+ 软件协议：Apache-2.0
+ 官网网址：[https://www.xiaomayicloud.com](https://www.xiaomayicloud.com)
+ 文档网址：[http://docs.elevue.xiaomayicloud.com](http://docs.elevue.xiaomayicloud.com)
+ 演示地址：[http://manage.elevue.xiaomayicloud.com](http://manage.elevue.xiaomayicloud.com)

#### 功能模块

+ 主控制台：纯静态页面，提供给有需要的企业、开发者做定制化使用；
+ 用户管理：实现用户数据录入于管理，增强了账户安全；
+ 角色管理：基于RBAC权限架构体系，实现角色权限的管理；
+ 菜单管理：用于权限菜单数据管理以及菜单节点权限数据管理；
+ 部门管理：用于管理系统架构体系中的部门数据；
+ 岗位管理：针对用户岗位属性的数据进行统一维护管理；
+ 租户管理：主要对租户入住数据进行管理和租户账号开通管理；
+ 日志管理：用于登录日志、操作日志管理和维护；
+ 参数管理：用于对系统核心参数的定义和管理；
+ 配置管理：用于对动态配置参数的统一管理和设置；
+ 字典管理：用于对字典数据、字典项数据的管理为维护；
+ 通知公告：主要对通知、公告等站外、站内数据进行管理；
+ 行政区划：针对全国行政区划分数据进行管理和维护；
+ 消息管理：对系统消息模块进行查阅和管理；
+ 短信日志：主要用于记录发送短信的日志信息进行管理；
+ 邮件日志：主要用于记录邮件发送记录和数据维护管理；
+ 文件日志：主要用于上传图片、文件等资源附件日志记录进行管理；
+ 文件模板：主要对系统中使用的文件模板进行上传和管理；
+ 邮件模板：主要对系统使用的邮件模板文件进行管理；
+ 短信模板：主要对系统短信模板信息进行录入、维护和管理；
+ 消息模板：主要针对系统业务发送的消息内容进行统一管理；
+ 系统设置：针对系统的常规配置数据进行可视化管理维护；
+ CMS管理：主要用于网站相关的数据进行管理的模块；
+ 任务调度：主要用于对定时任务进行定义和统一调度管理；
+ 在线用户：主要用于在线用户的维护以及强制退出登录；
+ SQL监控：主要结合Druid数据库连接池技术实现数据库SQL监控管理；
+ 性能监控：用于监控应用CPU、内存、服务器、磁盘、虚拟机等信息；
+ 健康监控：主要用于对系统健康度和运行状态、性能进行监控；
+ 缓存监控：主要对系统缓存信息进行统计和监控查阅；
+ 代码生成：官方定制化模板生成器，用于简化单模块开发，提高效率；
+ 接口文档：用于查阅系统接口在线文档，查阅API地址、入参、出参等；

#### 其他版本

|            版本名称            | 说明 | 地址
|:--------------------------:|:----:| :----:
|  小蚂蚁云单体Vue3+ElementPlus版   | 基于SpringBoot3、SpringSecurity、MybatisPlus、Vue3、ElementPlus、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-EleVue
|   小蚂蚁云单体Vue3+AntDesign版    | 基于SpringBoot3、SpringSecurity、MybatisPlus、Vue3、AntDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-AntdVue
|    小蚂蚁云单体Vue3+NaiveUI版     | 基于SpringBoot3、SpringSecurity、MybatisPlus、Vue3、NaiveUI、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-NaiveVue
|   小蚂蚁云单体Vue3+ArcoDesign版   | 基于SpringBoot3、SpringSecurity、MybatisPlus、Vue3、ArcoDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-ArcoVue
| 小蚂蚁云微服务Nacos+ElementPlus版  | 基于SpringCloud、SpringSecurity、OAuth2、Nacos、Seata、MybatisPlus、Vue3、ElementPlus、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Nacos-EleVue
|  小蚂蚁云微服务Nacos+AntDesign版   | 基于SpringCloud、SpringSecurity、OAuth2、Nacos、Seata、MybatisPlus、Vue3、AntDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Nacos-AntdVue
|   小蚂蚁云微服务Nacos+NaiveUI版    | 基于SpringCloud、SpringSecurity、OAuth2、Nacos、Seata、MybatisPlus、Vue3、NaiveUI、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Nacos-NaiveVue
|  小蚂蚁云微服务Nacos+ArcoDesign版  | 基于SpringCloud、SpringSecurity、OAuth2、Nacos、Seata、MybatisPlus、Vue3、ArcoDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Nacos-ArcoVue
| 小蚂蚁云微服务Consul+ElementPlus版 | 基于SpringCloud、SpringSecurity、OAuth2、Consul、Seata、MybatisPlus、Vue3、ElementPlus、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Consul-EleVue
|  小蚂蚁云微服务Consul+AntDesign版   | 基于SpringCloud、SpringSecurity、OAuth2、Consul、Seata、MybatisPlus、Vue3、AntDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Consul-AntdVue
|   小蚂蚁云微服务Consul+NaiveUI版    | 基于SpringCloud、SpringSecurity、OAuth2、Consul、Seata、MybatisPlus、Vue3、NaiveUI、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Consul-NaiveVue
|  小蚂蚁云微服务Consul+ArcoDesign版  | 基于SpringCloud、SpringSecurity、OAuth2、Consul、Seata、MybatisPlus、Vue3、ArcoDesign、TypeScript、Vite、MySQL等技术栈 | https://gitee.com/xiaomayicloud/XiaoMaYi-Consul-ArcoVue

#### 模块预览

<table>
    <tr>
        <td><img src="./upload/demo/1.png"/></td>
        <td><img src="./upload/demo/2.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/3.png"/></td>
        <td><img src="./upload/demo/4.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/5.png"/></td>
        <td><img src="./upload/demo/6.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/7.png"/></td>
        <td><img src="./upload/demo/8.png"/></td>
    </tr>	 
    <tr>
        <td><img src="./upload/demo/9.png"/></td>
        <td><img src="./upload/demo/10.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/11.png"/></td>
        <td><img src="./upload/demo/12.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/13.png"/></td>
        <td><img src="./upload/demo/14.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/15.png"/></td>
        <td><img src="./upload/demo/16.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/17.png"/></td>
        <td><img src="./upload/demo/18.png"/></td>
    </tr>
	<tr>
        <td><img src="./upload/demo/19.png"/></td>
        <td><img src="./upload/demo/20.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/21.png"/></td>
        <td><img src="./upload/demo/22.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/23.png"/></td>
        <td><img src="./upload/demo/24.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/25.png"/></td>
        <td><img src="./upload/demo/26.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/27.png"/></td>
        <td><img src="./upload/demo/28.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/29.png"/></td>
        <td><img src="./upload/demo/30.png"/></td>
    </tr>
    <tr>
        <td><img src="./upload/demo/31.png"/></td>
        <td><img src="./upload/demo/32.png"/></td>
    </tr>
</table>

#### 开源协议

开源软件遵循 [Apache 2.0 协议](https://www.apache.org/licenses/LICENSE-2.0.html)。

#### 版权信息

软件产品版权和最终解释权归【小蚂蚁云开源团队】所有，商业版使用需授权，未授权禁止恶意传播和用于商业用途，否则将追究相关人的法律责任。

本软件框架禁止任何单位和个人用于任何违法、侵害他人合法利益等恶意项目使用，禁止用于任何违反我国法律法规的一切项目研发，任何单位和个人使用本软件框架用于产品研发而产生的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其造成的损失 (
包括但不限于直接、间接、附带或衍生的损失等)，本团队不承担任何法律责任。本软件框架只能用于公司和个人内部的法律所允许的合法合规的软件产品(非开源和非竞品)研发，详细声明内容请官方阅读《框架免责声明》附件；

本项目包含的第三方源码和二进制文件之版权信息另行标注。

版权所有 Copyright © 2020~2025 [xiaomayicloud.com](https://www.xiaomayicloud.com) All rights reserved。

更多细节参阅 [LICENSE](LICENSE)