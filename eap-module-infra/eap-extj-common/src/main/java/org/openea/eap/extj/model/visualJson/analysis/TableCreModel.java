package org.openea.eap.extj.model.visualJson.analysis;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

@Data
public class TableCreModel {

    private JSONArray jsonArray;
    private List<FormAllModel> formAllModel;
    private String table;
    private String linkId;

}
