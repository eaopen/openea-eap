package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

import java.util.List;


@Data
public class DataInterfacePage extends Pagination {

    //远端接口id
    @Schema(description = "远端接口id")
    private String interfaceId;
    //保存字段
    @Schema(description = "保存字段")
    private String propsValue;
    //查询字段
    @Schema(description = "查询字段")
    private String relationField;
    //查询字段（多个）
    @Schema(description = "查询字段（多个）")
    private String columnOptions;
    //数据id
    @Schema(description = "数据id")
    private String id;

    @Schema(description = "id集合")
    private Object ids;

    @Schema(description = "参数集合")
    private List<DataInterfaceModel> paramList;

}
