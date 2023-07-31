package org.openea.eap.module.visualdev.base.model.read;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class ReadListVO {
    private String fileName;
    private String id;
    private List<ReadModel> children;
}
