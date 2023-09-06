package org.openea.eap.extj.database.model;

import org.openea.eap.extj.database.enums.TenantDbSchema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 *
 * BaseTenant模型
 */
@Data
@Accessors(chain = true)
public class TenantVO implements Serializable {

    private String dbName;
    /**
     * 数据源模式
     */
    private TenantDbSchema dataSchema;

    /**
     * 配置连接
     */
    private List<TenantLinkModel> linkList;

    public boolean isDefault(){
        return TenantDbSchema.DEFAULT.equals(dataSchema);
    }

}
