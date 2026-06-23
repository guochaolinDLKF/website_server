package com.xiaomayi.generator.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.xiaomayi.core.config.DbConfig;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.SpringUtils;
import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.generator.config.GeneratorConfig;
import com.xiaomayi.system.dto.menu.MenuAddDTO;
import com.xiaomayi.system.dto.menu.MenuUpdateDTO;
import com.xiaomayi.system.entity.Menu;
import com.xiaomayi.system.service.MenuService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Types;
import java.util.*;

/**
 * <p>
 * 代码工具
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2023-05-28
 */
@Component
public class CodeGenerator {

    /**
     * 生成代码
     *
     * @param tableName 数据表名
     */
    public static void Generation(String tableName, String tableAnnotation) {
        System.out.println("开始生成");

        // 设置数据库链接
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s?&useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai",
                StringUtils.isNotEmpty(DbConfig.getDbHost()) ? DbConfig.getDbHost() : "127.0.0.1",
                StringUtils.isNotEmpty(DbConfig.getDbPort()) ? DbConfig.getDbPort() : "3306",
                StringUtils.isNotEmpty(DbConfig.getDbName()) ? DbConfig.getDbName() : "xiaomayi.elevue"
        );
        // 设置数据源
        DataSourceConfig.Builder dataSourceConfig = new DataSourceConfig.Builder(
                dbUrl,
                StringUtils.isNotEmpty(DbConfig.getUsername()) ? DbConfig.getUsername() : "root",
                StringUtils.isNotEmpty(DbConfig.getPassword()) ? DbConfig.getPassword() : "root"
        );

        // 作者名称
        String author = StringUtils.isNotEmpty(GeneratorConfig.getAuthor()) ? GeneratorConfig.getAuthor() : "小蚂蚁云团队";
        // 模块名称
        String serviceModuleName = StringUtils.isNotEmpty(GeneratorConfig.getServiceModelName()) ? GeneratorConfig.getServiceModelName() : "xiaomayi-services/xiaomayi-system";
        // 模块文件报名
        String servicePackageName = StringUtils.isNotEmpty(GeneratorConfig.getServicePackageName()) ? GeneratorConfig.getServicePackageName() : "com.xiaomayi.system";
        // 是否去除文件前缀
        boolean removePrefix = StringUtils.isNotNull(GeneratorConfig.getRemovePrefix()) ? GeneratorConfig.getRemovePrefix() : true;
        // 数据表前缀
        String[] tablePrefix = StringUtils.isNotEmpty(GeneratorConfig.getTablePrefix()) ? GeneratorConfig.getTablePrefix().split(",") : new String[]{"sys_"};

        // 第一步：生成服务层模块文件
        generateService(dataSourceConfig, author, serviceModuleName, servicePackageName, tablePrefix, tableName);

        // 模块名称
        String moduleModuleName = StringUtils.isNotEmpty(GeneratorConfig.getModuleModelName()) ? GeneratorConfig.getModuleModelName() : "xiaomayi-modules/xiaomayi-admin";
        // 模块文件报名
        String modulePackageName = StringUtils.isNotEmpty(GeneratorConfig.getModulePackageName()) ? GeneratorConfig.getModulePackageName() : "com.xiaomayi.admin";

        // 第二步：生成应用层模块文件
        generateModule(dataSourceConfig, author, moduleModuleName, modulePackageName, servicePackageName, tablePrefix, tableName);

        // 第三步：创建模块菜单权限节点
        generatePermission(tableName, tableAnnotation, tablePrefix, removePrefix);
    }

    /**
     * 生成服务层模块文件
     *
     * @param dataSourceConfig 数据源
     * @param author           作者名称
     * @param moduleName       模块名称
     * @param packageName      模板文件包名
     * @param tablePrefix      数据表前缀
     * @param tableName        数据表名
     */
    private static void generateService(
            DataSourceConfig.Builder dataSourceConfig,
            String author,
            String moduleName,
            String packageName,
            String[] tablePrefix,
            String... tableName
    ) {
        System.out.println("========================== 服务层生成开始 ==========================");
        // 生成模块文件
        FastAutoGenerator.create(dataSourceConfig)
                .globalConfig(builder -> {
                    // 设置作者
                    builder.author(author)
                            // 启用springdoc, 默认值:false
                            .enableSpringdoc()
                            // 禁止打开输出目录 默认值:true
                            .disableOpenDir()
                            // 注释日期
                            .commentDate("yyyy-MM-dd")
                            // 定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .dateType(DateType.TIME_PACK)
                            // 指定输出目录
                            .outputDir(System.getProperty("user.dir") + String.format("/%s/src/main/java", moduleName));
                })
                // 数据库字段的类型转换
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT || typeCode == Types.TINYINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent(packageName)
                            .entity("entity")
                            .mapper("mapper")
                            // 关键配置：指定 XML 文件输出到 resources/mapper
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + String.format("/%s/src/main/resources/mapper", moduleName)));
                })
                .strategyConfig(builder -> {
                    //设置要生成的表名
                    builder.addInclude(tableName)
                            // 表前缀过滤
                            .addTablePrefix(tablePrefix)

                            // 1. Entity策略配置
                            .entityBuilder()
                            .enableFileOverride()
                            // 启用lombok
                            .enableLombok()
                            .disableSerialVersionUID()
                            // 链式
                            .enableChainModel()
                            // 开启boolean类型字段移除is前缀
//                            .enableRemoveIsPrefix()
//                            // 开启生成实体时生成的字段注解
//                            .enableTableFieldAnnotation()
                            // 乐观锁数据库字段
                            .versionColumnName("version")
                            // 乐观锁实体类名称
                            .versionPropertyName("version")
                            // 逻辑删除数据库中字段名
                            .logicDeleteColumnName("del_flag")
                            // 逻辑删除实体类中的字段名
                            .logicDeletePropertyName("delFlag")
                            // 命名策略,数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                            .naming(NamingStrategy.underline_to_camel)
                            // 表字段映射实体属性命名规则：默认null，不指定按照naming执行
                            .columnNaming(NamingStrategy.underline_to_camel)
                            // 添加全局主键类型
                            .idType(IdType.AUTO)
                            // 格式化实体名称，%s取消首字母I
                            .formatFileName("%s")
//                            .formatFileName("%sEntity")
//                            .superClass("com.example.db.BaseEntity")
//                            .addSuperEntityColumns("id", "create_time", "update_time")
                            // 表字段填充
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            // 表字段填充
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
//                            .enableColumnConstant()
//                            // MPlus中启用ActiveRecord模式，生成的实体类会继承activerecord.Model类，直接进行数据库操作
//                            .enableActiveRecord();
//                            .disable()

                            // 2. Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride()
                            // 开启mapper注解
                            .enableMapperAnnotation()
                            // 生成基本的resultMap,启用xml文件中的BaseResultMap 生成
                            .enableBaseResultMap()
                            // 生成基本的SQL片段,启用xml文件中的BaseColumnList
                            .enableBaseColumnList()
                            // 格式化Mapper文件名称
                            .formatMapperFileName("%sMapper")
                            // // 格式化Xml文件名称
                            .formatXmlFileName("%sMapper")
//                            // 禁用生成mapper
//                            .disable()

                            // 3. Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride()
                            // 格式化Service文件名称
                            .formatServiceFileName("%sService")
                            // 格式化Service实现类文件名称
                            .formatServiceImplFileName("%sServiceImpl")
//                            // 禁用生成service
//                            .disable()

                            // 4. Controller策略配置
                            .controllerBuilder()
                            // 开启文件重写覆盖
                            .enableFileOverride()
                            // 开启驼峰转连字符
                            .enableHyphenStyle()
                            // 格式化Controller文件名称
                            .formatFileName("%sController")
                            // 开启生成@RestController控制器
                            .enableRestStyle()
                            // 禁用生成controller
                            .disable();
                })
                .templateConfig(builder -> {
                    builder
                            // 禁用所有默认模板
                            //.disable(TemplateType.values())
                            // 禁用Controller层的代码生成
                            .disable(TemplateType.CONTROLLER)
                            // 禁用Service接口的代码生成
                            .disable(TemplateType.SERVICE)
                            // 禁用Service实现类的代码生成
                            .disable(TemplateType.SERVICE_IMPL)
                            // 禁用Mapper接口的代码生成
                            .disable(TemplateType.MAPPER)
                            // 禁用XML映射文件的代码生成
                            .disable(TemplateType.XML)
                            // 禁用实体类的默认代码生成
                            .disable(TemplateType.ENTITY)
                            // 指定使用自定义模板来生成实体对象
                            .entity("/templates/entity.java")
                            .service("/templates/service.java")
                            .serviceImpl("/templates/serviceImpl.java")
                            .mapper("/templates/mapper.java")
                            .xml("/templates/mapper.xml");
                    ;
                })
                // 注入配置（自定义DTO/VO模板注入）
                .injectionConfig(builder -> {
                    // 自定义DTO、VO模板文件
                    Map<String, String> customFile = new HashMap<>();
                    // 自定义DTO模板文件
                    customFile.put("dto||AddDTO|添加DTO", "templates/dto.java.ftl");
                    customFile.put("dto||UpdateDTO|更新DTO", "templates/dto.java.ftl");
                    customFile.put("dto||PageDTO|分页查询DTO", "templates/dto.java.ftl");
                    customFile.put("dto||ListDTO|列表查询DTO", "templates/dto.java.ftl");
                    // 自定义VO模板文件
                    customFile.put("vo||ListVO|列表VO", "templates/vo.java.ftl");
                    customFile.put("vo||InfoVO|信息VO", "templates/vo.java.ftl");
                    // 自定义前端模板
                    customFile.put("|xiaomayi-ui\\src\\views\\tool|index|vue", "templates/ui/index.vue.ftl");
                    customFile.put("|xiaomayi-ui\\src\\views\\tool|edit|vue", "templates/ui/edit.vue.ftl");
                    customFile.put("|xiaomayi-ui\\src\\views\\tool|columns|ts", "templates/ui/columns.ts.ftl");
                    customFile.put("|xiaomayi-ui\\src\\views\\tool|querySchemas|ts", "templates/ui/querySchemas.ts.ftl");
                    customFile.put("|xiaomayi-ui\\src\\api\\tool|api|ts", "templates/ui/api.ts.ftl");
                    // 自定义模板
                    builder.customFile(customFile);
                })
                // 输出文件前预处理
                .injectionConfig(builder -> builder.beforeOutputFile(
                        (tableInfo, objectMap) -> {
                            String entityName = tableInfo.getEntityName();
                            Map<String, Object> packageMap = (Map) objectMap.get("package");
                            // 定义一个变量，方便在模板中引用
                            objectMap.put("packageName", packageMap.get("Parent"));
                            // 模块描述
                            String tableComment = tableInfo.getComment();
                            if (StringUtils.isNotEmpty(tableComment)) {
                                tableComment = tableComment
                                        .replace("表", "")
                                        .replace("管理", "");
                            }
                            objectMap.put("comment", tableComment);
                        }
                ))
                .templateEngine(new FreemarkerTemplateEngine() {
                    @Override
                    public Map<String, Object> getObjectMap(ConfigBuilder config, TableInfo tableInfo) {
                        // 获取原始对象映射
                        Map<String, Object> objectMap = super.getObjectMap(config, tableInfo);

                        // 模块描述
                        String tableComment = tableInfo.getComment();
                        if (StringUtils.isNotEmpty(tableComment)) {
                            tableComment = tableComment
                                    .replace("表", "")
                                    .replace("管理", "");
                        }
                        objectMap.put("comment", tableComment);
                        tableInfo.setComment(tableComment);

                        // 遍历数据表字段
                        for (TableField field : tableInfo.getFields()) {
                            // 字段描述
                            String comment = field.getComment();
                            if (StringUtils.isEmpty(comment)) {
                                continue;
                            }
                            // 删除标识直接跳过
                            if ("delFlag".equals(field.getPropertyName())) {
                                continue;
                            }
                            if ((comment.contains("：") || comment.contains(":")) && comment.contains("-")) {
                                // 字段注释分裂处理
                                String[] strings = comment.replace("：", ":").split(":");
                                field.setComment(strings[0]);
                                // 字段注释值分裂处理
                                String[] paramList = strings[1].split(" ");
                                // 字段解析值数组小于2时直接跳过
                                if (paramList.length < 2) {
                                    continue;
                                }
                                // 字段注释解析并重组
                                Map<String, Object> map = new HashMap<>();
                                for (String s : paramList) {
                                    String[] item = s.split("-");
                                    map.put(item[0], item[1]);
                                }
                                field.setCustomMap(map);
                            }
                        }
                        return objectMap;
                    }

                    /**
                     * 输出自定义文件
                     * @param customFiles 自定义文件模板
                     * @param tableInfo 数据表信息
                     * @param objectMap 数据对象
                     */
                    @Override
                    protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
                        // 实体类名称
                        String entityName = tableInfo.getEntityName();
                        // 获取父类包名路径
                        String filePath = this.getPathInfo(OutputFile.parent);
                        // 遍历自定义文件对象
                        customFiles.forEach(item -> {
                            // 模板文件KEY处理
                            String[] fileItem = item.getFileName().split("\\|");
                            // 目标文件夹
                            String fileDir = fileItem[0];
                            // 文件名前缀
                            String filePrefix = fileItem[1];
                            // 文件名后缀
                            String fileSuffix = fileItem[2];
                            // 描述
                            String fileComment = fileItem[3];

                            // 文件路径
                            String pathName = "";
                            if ("vue".equals(fileComment) || "ts".equals(fileComment)) {
                                // 文件存储路径
                                String tplPath = System.getProperty("user.dir") + File.separator + filePrefix + File.separator;
                                if ("api".equals(fileSuffix)) {
                                    pathName = tplPath + entityName.toLowerCase() + "." + fileComment;
                                } else {
                                    pathName = tplPath + entityName.toLowerCase() + File.separator + fileSuffix + "." + fileComment;
                                }
                            } else {
                                // 输出路径
                                pathName = filePath + File.separator + fileDir.toLowerCase() + File.separator;
                                // 设置存放目录
                                pathName = pathName + entityName.toLowerCase() + File.separator;
                                // 设置文件名和文件后缀
                                pathName = pathName + filePrefix + entityName + fileSuffix + ".java";
                            }

                            // 自定义模板参数
                            objectMap.put("modelPrefix", filePrefix);
                            objectMap.put("modelSuffix", fileSuffix);
                            objectMap.put("fileComment", fileComment);
                            // 输出模板文件
                            this.outputFile(new File(pathName), objectMap, item.getTemplatePath(), true);
                        });
                    }
                })
                .execute();
        System.out.println("========================== 服务层生成结束 ==========================");
    }

    /**
     * 生成应用层模块文件
     *
     * @param dataSourceConfig 数据源
     * @param author           作者名称
     * @param moduleName       模块名称
     * @param packageName      模板文件包名
     * @param tablePrefix      数据表前缀
     * @param tableName        数据表名
     */
    private static void generateModule(
            DataSourceConfig.Builder dataSourceConfig,
            String author,
            String moduleName,
            String packageName,
            String servicePackageName,
            String[] tablePrefix,
            String... tableName
    ) {
        System.out.println("========================== 应用层生成开始 ==========================");
        // 生成模块文件
        FastAutoGenerator.create(dataSourceConfig)
                .globalConfig(builder -> {
                    builder.author(author)
                            .outputDir(System.getProperty("user.dir") + String.format("/%s/src/main/java", moduleName))
                            .disableOpenDir();
                })
                .packageConfig(builder -> {
                    builder.parent(packageName);
                })
                .strategyConfig(builder -> {
                    //设置要生成的表名
                    builder.addInclude(tableName)
                            // 表前缀过滤
                            .addTablePrefix(tablePrefix)

                            // 1. Entity策略配置
                            .entityBuilder()
                            .enableFileOverride()
                            // 启用lombok
                            .enableLombok()
                            .disableSerialVersionUID()
                            // 链式
                            .enableChainModel()
                            // 开启boolean类型字段移除is前缀
//                            .enableRemoveIsPrefix()
                            // 开启生成实体时生成的字段注解
                            .enableTableFieldAnnotation()
                            // 乐观锁数据库字段
                            .versionColumnName("version")
                            // 乐观锁实体类名称
                            .versionPropertyName("version")
                            // 逻辑删除数据库中字段名
                            .logicDeleteColumnName("del_flag")
                            // 逻辑删除实体类中的字段名
                            .logicDeletePropertyName("delFlag")
                            // 命名策略,数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                            .naming(NamingStrategy.underline_to_camel)
                            // 表字段映射实体属性命名规则：默认null，不指定按照naming执行
                            .columnNaming(NamingStrategy.underline_to_camel)
                            // 添加全局主键类型
                            .idType(IdType.AUTO)
                            // 格式化实体名称，%s取消首字母I
                            .formatFileName("%s")
//                            .formatFileName("%sEntity")
//                            .superClass("com.example.db.BaseEntity")
//                            .addSuperEntityColumns("id", "create_time", "update_time")
                            // 表字段填充
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            // 表字段填充
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
//                            .enableColumnConstant()
//                            // MPlus中启用ActiveRecord模式，生成的实体类会继承activerecord.Model类，直接进行数据库操作
//                            .enableActiveRecord();
                            .disable()

                            // 2. Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride()
                            // 开启mapper注解
                            .enableMapperAnnotation()
                            // 生成基本的resultMap,启用xml文件中的BaseResultMap 生成
                            .enableBaseResultMap()
                            // 生成基本的SQL片段,启用xml文件中的BaseColumnList
                            .enableBaseColumnList()
                            // 格式化Mapper文件名称
                            .formatMapperFileName("%sMapper")
                            // // 格式化Xml文件名称
                            .formatXmlFileName("%sMapper")
                            // 禁用生成mapper
                            .disable()

                            // 3. Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride()
                            // 格式化Service文件名称
                            .formatServiceFileName("%sService")
                            // 格式化Service实现类文件名称
                            .formatServiceImplFileName("%sServiceImpl")
                            // 禁用生成service
                            .disable()

                            // 4. Controller策略配置
                            .controllerBuilder()
                            // 开启文件重写覆盖
                            .enableFileOverride()
                            // 开启驼峰转连字符
                            .enableHyphenStyle()
                            // 格式化Controller文件名称
                            .formatFileName("%sController")
                            // 开启生成@RestController控制器
                            .enableRestStyle()
                            //禁用生成controller
                            .disable();
                })
                // 注入配置（自定义DTO/VO模板注入）
                .injectionConfig(builder -> {
                    // 自定义DTO、VO模板文件
                    Map<String, String> customFile = new HashMap<>();
                    // 自定义模块控制器模板文件
                    customFile.put("controller||Controller|模块控制器", "templates/modules/controller.java.ftl");
                    // 自定义模板
                    builder.customFile(customFile);
                })
                // 输出文件前预处理
                .injectionConfig(builder -> builder.beforeOutputFile(
                        (tableInfo, objectMap) -> {
                            String entityName = tableInfo.getEntityName();
                            Map<String, Object> packageMap = (Map) objectMap.get("package");
                            // 模块包名处理
                            String[] packages = servicePackageName.split("\\.");
                            String itemName = packages[packages.length - 1];
                            // 定义一个变量，方便在模板中引用
                            objectMap.put("packageName", packageMap.get("Parent").toString()
                                    .replace("admin", itemName));
                            // 模块描述
                            String tableComment = tableInfo.getComment();
                            if (StringUtils.isNotEmpty(tableComment)) {
                                tableComment = tableComment
                                        .replace("表", "")
                                        .replace("管理", "");
                            }
                            objectMap.put("comment", tableComment);
                        }
                ))
                .templateEngine(new FreemarkerTemplateEngine() {
                    @Override
                    public Map<String, Object> getObjectMap(ConfigBuilder config, TableInfo tableInfo) {
                        // Map对象
                        Map<String, Object> objectMap = super.getObjectMap(config, tableInfo);
                        // 实体类名称
                        String entityName = tableInfo.getEntityName();
                        // 获取父类包名
                        String otherPath = ((Map<String, Object>) objectMap.get("package")).get("Parent").toString();
                        // TODO... 自定义模板参数
                        // 模块描述
                        String tableComment = tableInfo.getComment();
                        if (StringUtils.isNotEmpty(tableComment)) {
                            tableComment = tableComment
                                    .replace("表", "")
                                    .replace("管理", "");
                        }
                        objectMap.put("comment", tableComment);
                        tableInfo.setComment(tableComment);
                        return objectMap;
                    }

                    /**
                     * 输出自定义文件
                     *
                     * @param customFiles 自定义文件模板
                     * @param tableInfo   数据表信息
                     * @param objectMap   数据对象
                     */
                    @Override
                    protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
                        // 实体类名称
                        String entityName = tableInfo.getEntityName();
                        // 获取父类包名路径
                        String filePath = this.getPathInfo(OutputFile.parent);
                        // 遍历自定义文件对象
                        customFiles.forEach(item -> {
                            // 模板文件KEY处理
                            String[] fileItem = item.getFileName().split("\\|");
                            // 目标文件夹
                            String fileDir = fileItem[0];
                            // 文件名前缀
                            String filePrefix = fileItem[1];
                            // 文件名后缀
                            String fileSuffix = fileItem[2];
                            // 描述
                            String fileComment = fileItem[3];

                            // 输出路径
                            String pathName = filePath + File.separator + fileDir.toLowerCase() + File.separator + filePrefix + entityName + fileSuffix + ".java";

                            // 自定义模板参数
                            objectMap.put("modelPrefix", filePrefix);
                            objectMap.put("modelSuffix", fileSuffix);
                            objectMap.put("fileComment", fileComment);
                            // 输出模板文件
                            this.outputFile(new File(pathName), objectMap, item.getTemplatePath(), true);
                        });
                    }
                })
                .execute();
        System.out.println("========================== 应用层生成结束 ==========================");
    }

    /**
     * 创建模块菜单和权限节点
     *
     * @param tableName       数据表名
     * @param tableAnnotation 数据表描述
     * @param tablePrefix     数据表前缀
     * @param removePrefix    移除数据表前缀
     */
    private static void generatePermission(
            String tableName,
            String tableAnnotation,
            String[] tablePrefix,
            boolean removePrefix
    ) {
        System.out.println("========================== 权限菜单创建开始 ==========================");
        // 是否替换前缀
        if (removePrefix) {
            // 遍历前缀并替换
            for (String prefix : tablePrefix) {
                tableName = tableName.replace(prefix, "");
            }
        }

        // 实体类名
        String entityName = StringUtils.replaceUnderLineAndUpperCase(tableName);
        if (StringUtils.isEmpty(entityName)) {
            return;
        }

        // 数据表描述
        tableAnnotation = tableAnnotation.replace("表", "")
                .replace("管理", "");

        // 获取菜单Bean对象
        MenuService menuService = SpringUtils.getBean(MenuService.class);
        // 根据菜单路径查询菜单信息
        Menu entity = menuService.getMenuByPath(String.format("/tool/%s", entityName.toLowerCase()));
        R result = null;
        if (StringUtils.isNotNull(entity)) {
            // 更新
            entity.setName(tableAnnotation);
            entity.setPath(String.format("/tool/%s", entityName.toLowerCase()));
            entity.setComponent(String.format("/tool/%s/index", entityName.toLowerCase()));
            entity.setPermission(String.format("sys:%s:index", entityName.toLowerCase()));

            // 类型转换
            MenuUpdateDTO menuUpdateDTO = new MenuUpdateDTO();
            BeanUtils.copyProperties(entity, menuUpdateDTO);
            result = menuService.update(menuUpdateDTO);
        } else {
            // 创建
            entity = new Menu();
            entity.setName(tableAnnotation);
            entity.setIcon("el-icon-Bicycle");
            entity.setIcon2("AlertOutlined");
            entity.setPath(String.format("/tool/%s", entityName.toLowerCase()));
            entity.setComponent(String.format("/tool/%s/index", entityName.toLowerCase()));
            entity.setParentId(322);
            entity.setType(0);
            entity.setStatus(0);
            entity.setHide(0);
            entity.setSort(5);
            entity.setTarget(0);

            // 类型转换
            MenuAddDTO menuAddDTO = new MenuAddDTO();
            BeanUtils.copyProperties(entity, menuAddDTO);
            result = menuService.add(menuAddDTO);
        }
        if (!R.isOK(result)) {
            return;
        }
        // 菜单ID
        Integer menuId = Integer.valueOf(result.getData().toString());
        // 删除菜单子级权限节点
        menuService.deleteByParentId(menuId);
        // 创建或更新权限节点
        String[] strings = entity.getPath().split("/");
        // 模块名称
        String moduleName = strings[strings.length - 1];
        // 目标标题
        String moduleTitle = entity.getName().replace("管理", "");
        // 遍历权限节点
        Integer[] permissionList = new Integer[]{1, 5, 10, 15, 20, 25, 30, 35};
        // 菜单权限列表
        List<Menu> menuList = new ArrayList<>();
        for (Integer item : permissionList) {
            // 创建对象
            Menu menu = new Menu();
            menu.setParentId(menuId);
            menu.setType(1);
            menu.setStatus(0);
            menu.setHide(0);
            menu.setSort(item);
            menu.setTarget(entity.getTarget());
            if (item.equals(1)) {
                // 查询分页
                menu.setName(String.format("查询%s分页", moduleTitle));
                menu.setPath(String.format("/%s/index", moduleName));
                menu.setPermission(String.format("sys:%s:index", moduleName));
            } else if (item.equals(5)) {
                // 查询列表
                menu.setName(String.format("查询%s列表", moduleTitle));
                menu.setPath(String.format("/%s/list", moduleName));
                menu.setPermission(String.format("sys:%s:list", moduleName));
            } else if (item.equals(10)) {
                // 查询列表
                menu.setName(String.format("查询%s详情", moduleTitle));
                menu.setPath(String.format("/%s/detail", moduleName));
                menu.setPermission(String.format("sys:%s:detail", moduleName));
            } else if (item.equals(15)) {
                // 添加
                menu.setName(String.format("添加%s", moduleTitle));
                menu.setPath(String.format("/%s/add", moduleName));
                menu.setPermission(String.format("sys:%s:add", moduleName));
            } else if (item.equals(20)) {
                // 修改
                menu.setName(String.format("更新%s", moduleTitle));
                menu.setPath(String.format("/%s/update", moduleName));
                menu.setPermission(String.format("sys:%s:update", moduleName));
            } else if (item.equals(25)) {
                // 设置状态
                menu.setName("设置状态");
                menu.setPath(String.format("/%s/status", moduleName));
                menu.setPermission(String.format("sys:%s:status", moduleName));
            } else if (item.equals(30)) {
                // 删除
                menu.setName(String.format("删除%s", moduleTitle));
                menu.setPath(String.format("/%s/delete", moduleName));
                menu.setPermission(String.format("sys:%s:delete", moduleName));
            } else if (item.equals(35)) {
                // 批量删除
                menu.setName("批量删除");
                menu.setPath(String.format("/%s/batchDelete", moduleName));
                menu.setPermission(String.format("sys:%s:batchDelete", moduleName));
            }
            // 加入列表
            menuList.add(menu);
        }
        // 批量插入权限节点数据
        menuService.batchAddMenu(menuList);

        System.out.println("========================== 权限菜单创建结束 ==========================");
    }

}
