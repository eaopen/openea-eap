package org.openea.eap.extj.base.model.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploaderVO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "请求接口")
    private String url;
    @Schema(description = "预览文件id")
    private String fileVersionId;
    @Schema(description = "文件大小")
    private Long fileSize;
    @Schema(description = "文件后缀")
    private String fileExtension;
}
