package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.entity.VisualConfigEntity;
import org.openea.eap.extj.entity.VisualEntity;
import lombok.Data;

/**
 * 大屏导出
 *
 *
 */
@Data
public class VisualModel {
    @Schema(description = "大屏基本信息")
    private VisualEntity entity;
    @Schema(description = "大屏配置信息")
    private VisualConfigEntity configEntity;
}
