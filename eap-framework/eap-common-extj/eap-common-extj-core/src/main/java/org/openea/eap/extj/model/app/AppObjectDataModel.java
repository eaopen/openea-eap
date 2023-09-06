package org.openea.eap.extj.model.app;

import lombok.Data;

@Data
public class AppObjectDataModel {

    private String id;
    private boolean hasChildren;
    private String fullName;
    private String icon;
    private String urlAddress;
    private String parentId;
    private Integer type;
    private String propertyJson;
    private boolean isData;
    private String children;
    private String iconBackground;
    private String moduleId;

}
