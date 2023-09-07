package org.openea.eap.extj.permission.model.userrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class UserRelationForm {
   @Schema(description = "对象类型")
   private String objectType;
   @Schema(description = "用户id")
   private List<String> userIds;
}
