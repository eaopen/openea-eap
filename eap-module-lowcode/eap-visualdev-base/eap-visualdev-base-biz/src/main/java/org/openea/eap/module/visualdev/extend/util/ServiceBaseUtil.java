package org.openea.eap.module.visualdev.extend.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.DbFieldModelBase;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * todo eap
 */

@Component
@DS("")
public class ServiceBaseUtil {
    public List<DbFieldModelBase> getDbTableModel(String linkId, String table) {
        return null;
    }

    public List<DbFieldModel> getFieldList(String dbLinkId, String table) {
        return null;
    }

    public void addField(DbTableFieldModel dbTableFieldModel) {
    }


    public DbLinkEntity getDbLink(String linkId) {
        return null;
    }

    public void createTable(List<DbTableFieldModel> dbTableList) {
    }
}
