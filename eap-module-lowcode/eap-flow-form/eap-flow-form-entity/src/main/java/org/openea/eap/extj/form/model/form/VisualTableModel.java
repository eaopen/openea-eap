package org.openea.eap.extj.form.model.form;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;

import java.util.ArrayList;
import java.util.List;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisualTableModel {
    private  JSONArray jsonArray;
    private List<FormAllModel> formAllModel=new ArrayList<>();
    private String table;
    private String linkId;
    private String fullName;
    private boolean concurrency = false;
    private Integer primaryKey = 1;
    //逻辑删除
    private Boolean logicalDelete = false;
}
