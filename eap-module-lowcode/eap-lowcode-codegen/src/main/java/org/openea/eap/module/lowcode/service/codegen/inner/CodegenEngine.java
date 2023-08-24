package org.openea.eap.module.lowcode.service.codegen.inner;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.engine.velocity.VelocityEngine;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.openea.eap.framework.common.exception.util.ServiceExceptionUtil;
import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.framework.common.pojo.PageParam;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.util.collection.CollectionUtils;
import org.openea.eap.framework.common.util.date.DateUtils;
import org.openea.eap.framework.common.util.date.LocalDateTimeUtils;
import org.openea.eap.framework.common.util.object.ObjectUtils;
import org.openea.eap.framework.excel.core.annotations.DictFormat;
import org.openea.eap.framework.excel.core.convert.DictConvert;
import org.openea.eap.framework.excel.core.util.ExcelUtils;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.framework.operatelog.core.annotations.OperateLog;
import org.openea.eap.framework.operatelog.core.enums.OperateTypeEnum;
import org.openea.eap.module.lowcode.dal.dataobject.codegen.CodegenColumnDO;
import org.openea.eap.module.lowcode.dal.dataobject.codegen.CodegenTableDO;
import org.openea.eap.module.lowcode.enums.codegen.CodegenFrontTypeEnum;
import org.openea.eap.module.lowcode.enums.codegen.CodegenSceneEnum;
import org.openea.eap.module.lowcode.framework.codegen.config.CodegenProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成的引擎，用于具体生成代码
 * 目前基于 {@link org.apache.velocity.app.Velocity} 模板引擎实现
 *
 * 考虑到 Java 模板引擎的框架非常多，Freemarker、Velocity、Thymeleaf 等等，所以我们采用 hutool 封装的 {@link cn.hutool.extra.template.Template} 抽象
 *
 */
@Component
public class CodegenEngine {

    /**
     * 后端的模板配置
     *
     * key：模板在 resources 的地址
     * value：生成的路径
     */
    private static final Map<String, String> SERVER_TEMPLATES = MapUtil.<String, String>builder(new LinkedHashMap<>()) // 有序
            // Java module-biz Main
            .put(javaTemplatePath("controller/vo/baseVO"), javaModuleImplVOFilePath("BaseVO"))
            .put(javaTemplatePath("controller/vo/createReqVO"), javaModuleImplVOFilePath("CreateReqVO"))
            .put(javaTemplatePath("controller/vo/pageReqVO"), javaModuleImplVOFilePath("PageReqVO"))
            .put(javaTemplatePath("controller/vo/respVO"), javaModuleImplVOFilePath("RespVO"))
            .put(javaTemplatePath("controller/vo/updateReqVO"), javaModuleImplVOFilePath("UpdateReqVO"))
            .put(javaTemplatePath("controller/vo/exportReqVO"), javaModuleImplVOFilePath("ExportReqVO"))
            .put(javaTemplatePath("controller/vo/excelVO"), javaModuleImplVOFilePath("ExcelVO"))
            .put(javaTemplatePath("controller/controller"), javaModuleImplControllerFilePath())
            .put(javaTemplatePath("convert/convert"),
                    javaModuleImplMainFilePath("convert/${table.businessName}/${table.className}Convert"))
            .put(javaTemplatePath("dal/do"),
                    javaModuleImplMainFilePath("dal/dataobject/${table.businessName}/${table.className}DO"))
            .put(javaTemplatePath("dal/mapper"),
                    javaModuleImplMainFilePath("dal/mysql/${table.businessName}/${table.className}Mapper"))
            .put(javaTemplatePath("dal/mapper.xml"), mapperXmlFilePath())
            .put(javaTemplatePath("service/serviceImpl"),
                    javaModuleImplMainFilePath("service/${table.businessName}/${table.className}ServiceImpl"))
            .put(javaTemplatePath("service/service"),
                    javaModuleImplMainFilePath("service/${table.businessName}/${table.className}Service"))
            // Java module-biz Test
            .put(javaTemplatePath("test/serviceTest"),
                    javaModuleImplTestFilePath("service/${table.businessName}/${table.className}ServiceImplTest"))
            // Java module-api Main
            .put(javaTemplatePath("enums/errorcode"), javaModuleApiMainFilePath("enums/ErrorCodeConstants_手动操作"))
            // SQL
            .put("codegen/sql/sql.vm", "sql/sql.sql")
            .put("codegen/sql/h2.vm", "sql/h2.sql")
            .build();

    /**
     * 后端的配置模版
     *
     * key1：UI 模版的类型 {@link CodegenFrontTypeEnum#getType()}
     * key2：模板在 resources 的地址
     * value：生成的路径
     */
    private static final Table<Integer, String, String> FRONT_TEMPLATES = ImmutableTable.<Integer, String, String>builder()
            // Vue2 标准模版
            .put(CodegenFrontTypeEnum.VUE2.getType(), vueTemplatePath("views/index.vue"),
                    vueFilePath("views/${table.moduleName}/${classNameVar}/index.vue"))
            .put(CodegenFrontTypeEnum.VUE2.getType(), vueTemplatePath("api/api.js"),
                    vueFilePath("api/${table.moduleName}/${classNameVar}.js"))
            // Vue3 标准模版
            .put(CodegenFrontTypeEnum.VUE3.getType(), vue3TemplatePath("views/index.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/index.vue"))
            .put(CodegenFrontTypeEnum.VUE3.getType(), vue3TemplatePath("views/form.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/${simpleClassName}Form.vue"))
            .put(CodegenFrontTypeEnum.VUE3.getType(), vue3TemplatePath("api/api.ts"),
                    vue3FilePath("api/${table.moduleName}/${classNameVar}/index.ts"))
            // Vue3 Schema 模版
            .put(CodegenFrontTypeEnum.VUE3_SCHEMA.getType(), vue3SchemaTemplatePath("views/data.ts"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/${classNameVar}.data.ts"))
            .put(CodegenFrontTypeEnum.VUE3_SCHEMA.getType(), vue3SchemaTemplatePath("views/index.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/index.vue"))
            .put(CodegenFrontTypeEnum.VUE3_SCHEMA.getType(), vue3SchemaTemplatePath("views/form.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/${simpleClassName}Form.vue"))
            .put(CodegenFrontTypeEnum.VUE3_SCHEMA.getType(), vue3SchemaTemplatePath("api/api.ts"),
                    vue3FilePath("api/${table.moduleName}/${classNameVar}/index.ts"))
            // Vue3 vben 模版
            .put(CodegenFrontTypeEnum.VUE3_VBEN.getType(), vue3VbenTemplatePath("views/data.ts"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/${classNameVar}.data.ts"))
            .put(CodegenFrontTypeEnum.VUE3_VBEN.getType(), vue3VbenTemplatePath("views/index.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/index.vue"))
            .put(CodegenFrontTypeEnum.VUE3_VBEN.getType(), vue3VbenTemplatePath("views/form.vue"),
                    vue3FilePath("views/${table.moduleName}/${classNameVar}/${simpleClassName}Modal.vue"))
            .put(CodegenFrontTypeEnum.VUE3_VBEN.getType(), vue3VbenTemplatePath("api/api.ts"),
                    vue3FilePath("api/${table.moduleName}/${classNameVar}/index.ts"))
            .build();

    @Resource
    private CodegenProperties codegenProperties;

    /**
     * 模板引擎，由 hutool 实现
     */
    private final TemplateEngine templateEngine;
    /**
     * 全局通用变量映射
     */
    private final Map<String, Object> globalBindingMap = new HashMap<>();

    public CodegenEngine() {
        // 初始化 TemplateEngine 属性
        TemplateConfig config = new TemplateConfig();
        config.setResourceMode(TemplateConfig.ResourceMode.CLASSPATH);
        this.templateEngine = new VelocityEngine(config);
    }

    @PostConstruct
    private void initGlobalBindingMap() {
        // 全局配置
        globalBindingMap.put("basePackage", codegenProperties.getBasePackage());
        globalBindingMap.put("baseFrameworkPackage", codegenProperties.getBasePackage()
                + '.' + "framework"); // 用于后续获取测试类的 package 地址
        // 全局 Java Bean
        globalBindingMap.put("CommonResultClassName", CommonResult.class.getName());
        globalBindingMap.put("PageResultClassName", PageResult.class.getName());
        // VO 类，独有字段
        globalBindingMap.put("PageParamClassName", PageParam.class.getName());
        globalBindingMap.put("DictFormatClassName", DictFormat.class.getName());
        // DO 类，独有字段
        globalBindingMap.put("BaseDOClassName", BaseDO.class.getName());
        globalBindingMap.put("baseDOFields", CodegenBuilder.BASE_DO_FIELDS);
        globalBindingMap.put("QueryWrapperClassName", LambdaQueryWrapperX.class.getName());
        globalBindingMap.put("BaseMapperClassName", BaseMapperX.class.getName());
        // Util 工具类
        globalBindingMap.put("ServiceExceptionUtilClassName", ServiceExceptionUtil.class.getName());
        globalBindingMap.put("DateUtilsClassName", DateUtils.class.getName());
        globalBindingMap.put("ExcelUtilsClassName", ExcelUtils.class.getName());
        globalBindingMap.put("LocalDateTimeUtilsClassName", LocalDateTimeUtils.class.getName());
        globalBindingMap.put("ObjectUtilsClassName", ObjectUtils.class.getName());
        globalBindingMap.put("DictConvertClassName", DictConvert.class.getName());
        globalBindingMap.put("OperateLogClassName", OperateLog.class.getName());
        globalBindingMap.put("OperateTypeEnumClassName", OperateTypeEnum.class.getName());
    }

    public Map<String, String> execute(CodegenTableDO table, List<CodegenColumnDO> columns) {
        // 创建 bindingMap
        Map<String, Object> bindingMap = new HashMap<>(globalBindingMap);
        bindingMap.put("table", table);
        bindingMap.put("columns", columns);
        bindingMap.put("primaryColumn", CollectionUtils.findFirst(columns, CodegenColumnDO::getPrimaryKey)); // 主键字段
        bindingMap.put("sceneEnum", CodegenSceneEnum.valueOf(table.getScene()));

        // className 相关
        // 去掉指定前缀，将 TestDictType 转换成 DictType. 因为在 create 等方法后，不需要带上 Test 前缀
        String simpleClassName = CharSequenceUtil.removePrefix(table.getClassName(), CharSequenceUtil.upperFirst(table.getModuleName()));
        bindingMap.put("simpleClassName", simpleClassName);
        bindingMap.put("simpleClassName_underlineCase", CharSequenceUtil.toUnderlineCase(simpleClassName)); // 将 DictType 转换成 dict_type
        bindingMap.put("classNameVar", CharSequenceUtil.lowerFirst(simpleClassName)); // 将 DictType 转换成 dictType，用于变量
        // 将 DictType 转换成 dict-type
        String simpleClassNameStrikeCase = CharSequenceUtil.toSymbolCase(simpleClassName, '-');
        bindingMap.put("simpleClassName_strikeCase", simpleClassNameStrikeCase);
        // permission 前缀
        bindingMap.put("permissionPrefix", table.getModuleName() + ":" + simpleClassNameStrikeCase);

        // 执行生成
        Map<String, String> templates = getTemplates(table.getFrontType());
        Map<String, String> result = Maps.newLinkedHashMapWithExpectedSize(templates.size()); // 有序
        templates.forEach((vmPath, filePath) -> {
            filePath = formatFilePath(filePath, bindingMap);
            String content = templateEngine.getTemplate(vmPath).render(bindingMap);
            // 去除字段后面多余的 , 逗号
            content = content.replaceAll(",\n}", "\n}").replaceAll(",\n  }", "\n  }");
            result.put(filePath, content);
        });
        return result;
    }

    private Map<String, String> getTemplates(Integer frontType) {
        Map<String, String> templates = new LinkedHashMap<>();
        templates.putAll(SERVER_TEMPLATES);
        templates.putAll(FRONT_TEMPLATES.row(frontType));
        return templates;
    }

    private String formatFilePath(String filePath, Map<String, Object> bindingMap) {
        filePath = StrUtil.replace(filePath, "${basePackage}",
                MapUtil.getStr(bindingMap, "basePackage").replaceAll("\\.", "/"));
        filePath = StrUtil.replace(filePath, "${classNameVar}",
                MapUtil.getStr(bindingMap, "classNameVar"));
        filePath = StrUtil.replace(filePath, "${simpleClassName}",
                MapUtil.getStr(bindingMap, "simpleClassName"));
        // sceneEnum 包含的字段
        CodegenSceneEnum sceneEnum = (CodegenSceneEnum) bindingMap.get("sceneEnum");
        filePath = StrUtil.replace(filePath, "${sceneEnum.prefixClass}", sceneEnum.getPrefixClass());
        filePath = StrUtil.replace(filePath, "${sceneEnum.basePackage}", sceneEnum.getBasePackage());
        // table 包含的字段
        CodegenTableDO table = (CodegenTableDO) bindingMap.get("table");
        filePath = StrUtil.replace(filePath, "${table.moduleName}", table.getModuleName());
        filePath = StrUtil.replace(filePath, "${table.businessName}", table.getBusinessName());
        filePath = StrUtil.replace(filePath, "${table.className}", table.getClassName());
        return filePath;
    }

    private static String javaTemplatePath(String path) {
        return "codegen/java/" + path + ".vm";
    }

    private static String javaModuleImplVOFilePath(String path) {
        return javaModuleFilePath("controller/${sceneEnum.basePackage}/${table.businessName}/" +
                "vo/${sceneEnum.prefixClass}${table.className}" + path, "biz", "main");
    }

    private static String javaModuleImplControllerFilePath() {
        return javaModuleFilePath("controller/${sceneEnum.basePackage}/${table.businessName}/" +
                "${sceneEnum.prefixClass}${table.className}Controller", "biz", "main");
    }

    private static String javaModuleImplMainFilePath(String path) {
        return javaModuleFilePath(path, "biz", "main");
    }

    private static String javaModuleApiMainFilePath(String path) {
        return javaModuleFilePath(path, "api", "main");
    }

    private static String javaModuleImplTestFilePath(String path) {
        return javaModuleFilePath(path, "biz", "test");
    }

    private static String javaModuleFilePath(String path, String module, String src) {
        return "eap-module-${table.moduleName}/" + // 顶级模块
                "eap-module-${table.moduleName}-" + module + "/" + // 子模块
                "src/" + src + "/java/${basePackage}/module/${table.moduleName}/" + path + ".java";
    }

    private static String mapperXmlFilePath() {
        return "eap-module-${table.moduleName}/" + // 顶级模块
                "eap-module-${table.moduleName}-biz/" + // 子模块
                "src/main/resources/mapper/${table.businessName}/${table.className}Mapper.xml";
    }

    private static String vueTemplatePath(String path) {
        return "codegen/vue/" + path + ".vm";
    }

    private static String vueFilePath(String path) {
        return "eap-ui-${sceneEnum.basePackage}/" + // 顶级目录
                "src/" + path;
    }

    private static String vue3TemplatePath(String path) {
        return "codegen/vue3/" + path + ".vm";
    }

    private static String vue3FilePath(String path) {
        return "eap-ui-${sceneEnum.basePackage}-vue3/" + // 顶级目录
                "src/" + path;
    }

    private static String vue3SchemaTemplatePath(String path) {
        return "codegen/vue3_schema/" + path + ".vm";
    }

    private static String vue3VbenTemplatePath(String path) {
        return "codegen/vue3_vben/" + path + ".vm";
    }
}
