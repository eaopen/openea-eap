package org.openea.eap.extj.base.model.read;

import lombok.Data;

import java.util.List;


@Data
public class ReadListVO {
    private String fileName;
    private String id;
    private List<ReadModel> children;
}
