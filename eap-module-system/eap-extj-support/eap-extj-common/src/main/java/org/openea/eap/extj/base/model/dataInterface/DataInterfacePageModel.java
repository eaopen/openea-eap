package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "分页模型")
public class DataInterfacePageModel implements Serializable {
    @Schema(description = "总计SQL")
    private String countSql;
    @Schema(description = "详情SQL")
    private String echoSql;
    @Schema(description = "详情路径")
    private String echoPath;
    @Schema(description = "详情方法")
    private String echoReqMethod;
    @Schema(description = "详情请求参数")
    private List<DataInterfaceModel> echoReqParameters;
    @Schema(description = "详情请求头")
    private List<PageParamModel> echoReqHeaders;
    @Schema(description = "分页参数")
    private List<PageParamModel> pageParameters;
    @Schema(description = "详情参数")
    private List<PageParamModel> echoParameters;
}
