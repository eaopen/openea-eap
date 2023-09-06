package org.openea.eap.extj.model.login;

import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class PermissionModel {
    private String modelId;
    private String moduleName;
    private List<PermissionVO> button;
    private List<PermissionVO> column;
    private List<PermissionVO> resource;
    private List<PermissionVO> form;
}
