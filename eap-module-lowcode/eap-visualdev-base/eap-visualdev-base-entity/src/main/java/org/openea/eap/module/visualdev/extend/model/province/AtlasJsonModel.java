package org.openea.eap.module.visualdev.extend.model.province;

import lombok.Data;

import java.util.List;

/**
 * 流程设计
 *
 */
@Data
public class AtlasJsonModel {
    private String type;
    private List<AtlasFeaturesModel> features;
}

