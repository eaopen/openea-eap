package org.openea.eap.extj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileListVO implements Serializable {

    @Schema(
            description = "主键id"
    )
    private String fileId;
    @Schema(
            description = "文件名称"
    )
    private String fileName;
    @Schema(
            description = "文件大小"
    )
    private String fileSize;
    @Schema(
            description = "修改时间"
    )
    private String fileTime;
    @Schema(
            description = "文件类型"
    )
    private String fileType;
}
