package org.openea.eap.module.visualdev.base.model;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class ExportSelectedModel {
    private String tableField;
    private String field;
    private String label;
    private List<ExportSelectedModel> selectedModelList;
}
