package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TableCreModels {

    private List<Map<String, Object>> jsonArray;
    private List<FormAllModel> formAllModel;
    private String table;
    private String linkId;
    private String fullName;
    private Boolean concurrency = false;
    private Integer primaryKey = 1;

}
