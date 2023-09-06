package org.openea.eap.extj.model.app;

import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class AppUserVO {
    private AppInfoModel userInfo;
    private List<AppMenuModel> menuList;
    private List<AppFlowFormModel> flowFormList;
    private List<AppDataModel> appDataList;
}
