package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.TableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TableModels {

    private List<TableModel> table = new ArrayList();
    private List<Map<String, Object>> jsonArray = new ArrayList();
}
