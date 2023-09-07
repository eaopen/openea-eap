package org.openea.eap.extj.portal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 */
@Data
public class PortalReleaseVO {

   @Schema(description = "pc发布标识")
   Integer pcIsRelease;
   @Schema(description = "app发布标识")
   Integer appIsRelease;

}
