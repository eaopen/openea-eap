package org.openea.eap.module.system.controller.admin.language.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 语言 Excel VO
 *
 * @author eap
 */
@Data
public class LangTypeExcelVO {

    @ExcelProperty("PK")
    private Long id;

    @ExcelProperty("key/别名")
    private String alias;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
