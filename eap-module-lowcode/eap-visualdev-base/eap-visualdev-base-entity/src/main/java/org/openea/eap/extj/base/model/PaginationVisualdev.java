package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
@Schema(description = "列表查询参数")
public class PaginationVisualdev extends Pagination {
   @Schema(description = "类型")
   private Integer type=1;
   @Schema(description = "关键字")
   private String keyword="";
   /**
    *0-在线开发(无表)，1-表单设计(有表)
    */
   @Schema(description = "模型分类：0-在线开发(无表)，1-表单设计(有表)")
   private String  model="0";
   /**
    * 分类
    */
   @Schema(description = "类别：字典分类")
   private String category;
}
