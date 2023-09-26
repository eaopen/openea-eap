package org.openea.eap.extj.base;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import org.openea.eap.module.infra.dal.dataobject.file.FileDO;

@Data
public class FileInfo extends FileDO {

//    private String id;
    private String url;
//    private Long size;
    private String filename;  //name
    private String originalFilename;
    private String basePath;
//    private String path;
    private String ext;
    private String contentType;
    private String platform;
    private String thUrl;
    private String thFilename;
    private Long thSize;
    private String thContentType;
    private String objectId;
    private String objectType;
    private Dict attr;
//    private Date createTime;
}
