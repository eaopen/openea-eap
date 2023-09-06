package org.openea.eap.extj.model.visualJson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursionForm {
    private List<FieLdsModel> list;
    private List<TableModel> tableModelList;

}
