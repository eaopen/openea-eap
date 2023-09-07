package org.openea.eap.extj.base.model.read;

import lombok.Data;


@Data
public class ReadModel {
    private String folderName;
    private String fileName;
    private String fileContent;
    private String fileType;
    private String id;
}
