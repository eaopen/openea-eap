package org.openea.eap.extj.extend.model.province;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AtlasPropModel {
    private Integer adcode;
    private String name;
    private List<BigDecimal> center;
    private List<BigDecimal> centroid;
    private Integer childrenNum;
    private String level;
    private List<Integer> acroutes;
    private Object parent;
}
