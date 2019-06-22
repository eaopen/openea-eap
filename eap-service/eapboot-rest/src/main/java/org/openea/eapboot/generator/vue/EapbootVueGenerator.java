package org.openea.eapboot.generator.vue;

import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.generator.bean.Field;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author nikou
 */
@Slf4j
@RestController
@Api(description = "Vue代码生成")
@RequestMapping(value = "/eapboot/generate")
public class EapbootVueGenerator {

    @RequestMapping(value = "/table/{vueName}/{rowNum}", method = RequestMethod.POST)
    @ApiOperation(value = "增删改表格生成")
    public Result<Object> generateTable(@PathVariable String vueName,
                                        @PathVariable Integer rowNum,
                                        @RequestBody List<Field> fields) throws IOException {

        String result = generate("table.btl", vueName, rowNum, fields);
        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/tree/{vueName}/{rowNum}", method = RequestMethod.POST)
    @ApiOperation(value = "树形结构生成")
    public Result<Object> generateTree(@PathVariable String vueName,
                                       @PathVariable Integer rowNum,
                                       @RequestBody List<Field> fields) throws IOException {

        String result = generate("tree.btl", vueName, rowNum, fields);
        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/getEntityData/{path}", method = RequestMethod.GET)
    @ApiOperation(value = "通过实体类生成Vue代码Json数据")
    public Result<Object> getEntityData(@PathVariable String path) {

        String result = "";
        try {
            result = gengerateEntityData(path);
        } catch (Exception e) {
            return new ResultUtil<Object>().setErrorMsg("实体类文件不存在");
        }
        return new ResultUtil<Object>().setData(result);
    }

    public String generate(String template, String vueName, Integer rowNum, List<Field> fields) throws IOException {

        // 模板路径
        String root = System.getProperty("user.dir")+"/src/main/java/org/openea/eapboot/generator/vue";
        FileResourceLoader resourceLoader = new FileResourceLoader(root,"utf-8");
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);

        Template tableTemplate = gt.getTemplate(template);
        // 排序
        Collections.sort(fields, Comparator.comparing(Field::getSortOrder));
        // 绑定变量
        tableTemplate.binding("vueName", vueName);
        tableTemplate.binding("fields", fields);
        // 判断有无upload和日期范围搜索
        Boolean upload = false;
        for(Field f:fields){
            if("upload".equals(f.getType())){
                upload = true;
            }
        }
        tableTemplate.binding("upload", upload);
        if("table.btl".equals(template)){
            // 判断有无upload和日期范围搜索
            Boolean daterangeSearch = false;
            for(Field f:fields){
                if(f.getSearchable()&&"daterange".equals(f.getSearchType())){
                    daterangeSearch = true;
                }
            }
            tableTemplate.binding("daterangeSearch", daterangeSearch);
            // 统计搜索栏个数 判断是否隐藏搜索栏
            Boolean hideSearch = false;
            List<Field> firstTwo = new ArrayList<>();
            List<Field> rest = new ArrayList<>();
            Integer count = 0;
            for(Field f:fields){
                if(f.getSearchable()){
                    count++;
                    if(count<=2){
                        firstTwo.add(f);
                    }else{
                        rest.add(f);
                    }
                }
            }
            if(count>=4){
                hideSearch = true;
                tableTemplate.binding("firstTwo", firstTwo);
                tableTemplate.binding("rest", rest);
            }
            tableTemplate.binding("searchSize", count);
            tableTemplate.binding("hideSearch", hideSearch);
            // 获取默认排序字段
            String defaultSort = "", defaultSortType = "";
            for(Field f:fields){
                if(f.getDefaultSort()){
                    defaultSort = f.getField();
                    defaultSortType = f.getDefaultSortType();
                    break;
                }
            }
            tableTemplate.binding("defaultSort", defaultSort);
            tableTemplate.binding("defaultSortType", defaultSortType);
        }
        // 一行几列
        tableTemplate.binding("rowNum", rowNum);
        if(rowNum==1){
            tableTemplate.binding("modalWidth", 500);
            tableTemplate.binding("width", "100%");
            tableTemplate.binding("editWidth", "100%");
            tableTemplate.binding("itemWidth", "");
            tableTemplate.binding("span", "9");
        }else if(rowNum==2){
            tableTemplate.binding("modalWidth", 770);
            tableTemplate.binding("width", "250px");
            tableTemplate.binding("editWidth", "250px");
            tableTemplate.binding("itemWidth", "350px");
            tableTemplate.binding("span", "17");
        }else if(rowNum==3){
            tableTemplate.binding("modalWidth", 980);
            tableTemplate.binding("width", "200px");
            tableTemplate.binding("editWidth", "200px");
            tableTemplate.binding("itemWidth", "300px");
            tableTemplate.binding("span", "17");
        }else if(rowNum==4){
            tableTemplate.binding("modalWidth", 1130);
            tableTemplate.binding("width", "160px");
            tableTemplate.binding("editWidth", "160px");
            tableTemplate.binding("itemWidth", "260px");
            tableTemplate.binding("span", "17");
        }else{
            throw new EapbootException("rowNum仅支持数字1-4");
        }
        // 生成代码
        String result = tableTemplate.render();
        System.out.println(result);
        return result;
    }

    public String gengerateEntityData(String path) throws Exception {

        Class c = Class.forName(path);

        Object obj = c.newInstance();

        String start = "{\n" +
                "    \"data\": [";
        String end = "\n    ]\n" +
                "}";
        String field_all = "";
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            java.lang.reflect.Field field = fields[j];
            field.setAccessible(true);
            // 字段名
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            // 白名单
            if("serialVersionUID".equals(fieldName)||"actBusinessId".equals(fieldName)||"applyUser".equals(fieldName)
                    ||"routeName".equals(fieldName)||"procInstId".equals(fieldName)||"applyTime".equals(fieldName)
                    ||"status".equals(fieldName)||"result".equals(fieldName)){
                continue;
            }

            // 获得字段注解
            ApiModelProperty myFieldAnnotation = field.getAnnotation(ApiModelProperty.class);
            String fieldName_CN = fieldName;
            if (myFieldAnnotation != null) {
                fieldName_CN = myFieldAnnotation.value();
            }
            fieldName_CN = (fieldName_CN == null || fieldName_CN == "") ? fieldName : fieldName_CN;

            String type = "text";
            String searchType = "text";
            // 日期字段特殊处理,其他一律按 字符串界面处理
            if (fieldType == "java.lang.Date" || fieldType == "java.util.Date" || fieldType == "Date") {
                type = "date";
                searchType = "daterange";
            }
            String field_json = "\n        {\n" +
                    "            \"sortOrder\": " + j + ",\n" +
                    "            \"field\": \"" + fieldName + "\",\n" +
                    "            \"name\": \"" + fieldName_CN + "\",\n" +
                    "            \"level\": \"2\",\n" +
                    "            \"tableShow\": true,\n" +
                    "            \"editable\": true,\n" +
                    "            \"type\": \"" + type + "\",\n" +
                    "            \"searchType\": \"" + searchType + "\",\n" +
                    "            \"searchLevel\": \"2\",\n" +
                    "            \"validate\": false,\n" +
                    "            \"searchable\": true,\n" +
                    "            \"sortable\": false,\n" +
                    "            \"defaultSort\": false,\n" +
                    "            \"defaultSortType\": \"desc\"\n" +
                    "        }";
            String splitChar = field_all == "" ? "" : ",";
            field_all = field_all + splitChar + field_json;
        }
        String json = start + field_all + end;
        return json;
    }
}
