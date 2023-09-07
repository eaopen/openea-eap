package org.openea.eap.extj.extend.model.province;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProvinceListTreeVO {
    @Schema(description = "子集集合")
    List<ProvinceListTreeVO> children;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "上级id")
    private String parentId;
    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "是否有子集相反")
    private Boolean isLeaf;
    @Schema(description = "是否有子集")
    private Boolean hasChildren;
    @Schema(description = "排序码")
    private long sortCode;
    private String atlasCenter;
    private BigDecimal centerLong;
    private BigDecimal centerLat;
}
