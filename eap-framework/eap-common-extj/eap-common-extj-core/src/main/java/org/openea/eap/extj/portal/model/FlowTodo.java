package org.openea.eap.extj.portal.model;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlowTodo {
    private List<String> toBeReviewedType = new ArrayList<>();
    private List<String> flowDoneType = new ArrayList<>();
    private List<String> flowCirculateType = new ArrayList<>();
}
