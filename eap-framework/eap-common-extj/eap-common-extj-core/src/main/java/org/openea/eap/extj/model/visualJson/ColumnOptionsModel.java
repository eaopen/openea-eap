package org.openea.eap.extj.model.visualJson;

import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.fields.slot.SlotModel;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class ColumnOptionsModel {
	private Boolean clearable;
	private String prefixIcon;
	private Boolean showPassword;
	private ConfigModel config;
	private Boolean readonly;
	private String vModel;
	private Boolean disabled;
	private String placeholder;
	private Boolean showWordLimit;
	private SlotModel slot;
	private String suffixIcon;
}
