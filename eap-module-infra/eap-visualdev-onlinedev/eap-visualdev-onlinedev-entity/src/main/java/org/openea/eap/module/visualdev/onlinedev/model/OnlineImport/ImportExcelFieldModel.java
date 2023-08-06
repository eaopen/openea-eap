package org.openea.eap.module.visualdev.onlinedev.model.OnlineImport;

import lombok.Data;

import java.util.List;

/**
 *

 */
@Data
public class ImportExcelFieldModel {
    private String tableField;
    private String field;
    private String fullName;
    private List<ImportExcelFieldModel> children;
}
