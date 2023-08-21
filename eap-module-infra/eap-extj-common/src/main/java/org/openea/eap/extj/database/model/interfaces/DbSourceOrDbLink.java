package org.openea.eap.extj.database.model.interfaces;

import org.openea.eap.extj.database.entity.DbLinkEntity;
import org.openea.eap.extj.database.sql.model.DbStruct;

public interface DbSourceOrDbLink {
    DbStruct getDbStruct();

    DbLinkEntity init();

    DbLinkEntity init(String var1);
}
