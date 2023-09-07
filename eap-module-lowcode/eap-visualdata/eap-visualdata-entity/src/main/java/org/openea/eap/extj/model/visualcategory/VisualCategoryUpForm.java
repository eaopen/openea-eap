package org.openea.eap.extj.model.visualcategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class VisualCategoryUpForm extends VisualCategoryCrForm {
    @Schema(description = "主键")
    private String id;
}
