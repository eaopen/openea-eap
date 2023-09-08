package org.openea.eap.extj.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 打印模板-数据传输对象
 *
 * 
 */
@Data
public class PrintDevFormDTO {

    @Schema(description = "主键_id")
    private String id;

    @NotBlank(message = "必填")
    @Schema(description = "名称")
    private String fullName;

    @NotBlank(message = "必填")
    @Schema(description = "编码",required = true)
    private String enCode;

    @NotBlank(message = "必填")
    @Schema(description = "分类")
    private String category;

    @NotNull(message = "必填")
    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "描述")
    private String description;

    @NotNull(message = "必填")
    @Schema(description = "排序码")
    private Long sortCode;

    @NotNull(message = "必填")
    @Schema(description = "有效标志")
    private Integer enabledMark;

    @Schema(description = "创建时间")
    private Long creatorTime;

    @Schema(description = "创建用户_id")
    private String creatorUserId;

    @Schema(description = "修改时间")
    private Long lastModifyTime;

    @Schema(description = "修改用户_id")
    private String lastModifyUserId;

    @Schema(description = "删除标志")
    private Integer deleteMark;

    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;

    @Schema(description = "删除用户_id")
    private String deleteUserId;

    @NotBlank(message = "必填")
    @Schema(description = "连接数据_id")
    private String dbLinkId;

    @Schema(description = "sql语句")
    private String sqlTemplate;

    @Schema(description = "左侧字段")
    private String leftFields;

    @Schema(description = "打印模板")
    private String printTemplate;

    @Schema(description = "纸张参数")
    private String pageParam;


}
