package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.database.sql.util.SqlFrameUtil;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SqlFrameBase {
    String getSqlFrame();

    String getDbEncode();

    String name();

    default void setStructParams(String table, DbStruct dbStruct, List<String> list) {
    }

    default PrepSqlDTO getPrepSqlDto(DbSourceOrDbLink dataSourceMod, String table) {
        List<String> list = new ArrayList();
        TenantDataSourceUtil.initDataSourceTenantDbName(dataSourceMod);
        this.setStructParams(table, dataSourceMod.getDbStruct(), list);
        return (new PrepSqlDTO(this.getSqlFrame(), list)).withConn((DbLinkEntity)dataSourceMod);
    }

    default String getOutSql(String... params) throws DataException {
        return this.getOutSqlByDb((String)null, params);
    }

    default String getOutSqlByDb(String dbEncode, String... params) throws DataException {
        SqlComEnum sqlComEnum = null;
        SqlFrameBase sqlFrameBase = null;

        DbBase dbBase;
        try {
            dbBase = DbTypeUtil.getEncodeDb(dbEncode);
        } catch (Exception var10) {
            dbBase = null;
        }

        if (this instanceof SqlComEnum) {
            sqlComEnum = (SqlComEnum)this;
            if (dbBase != null) {
                sqlFrameBase = sqlComEnum.getSqlFrameEnum(dbEncode);
            }

            if (sqlFrameBase == null) {
                sqlFrameBase = sqlComEnum.getBaseSqlEnum();
            }
        } else {
            if (dbBase != null) {
                throw new DataException("请使用SqlComEnum来做引用");
            }

            SqlComEnum[] var6 = SqlComEnum.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                SqlComEnum conEnum = var6[var8];
                sqlFrameBase = conEnum.getSqlFrameEnum(this);
                if (sqlFrameBase != null) {
                    sqlComEnum = conEnum;
                    break;
                }
            }

            if (sqlFrameBase == null) {
                throw new DataException("此枚举SQL框架未被引用");
            }
        }

        return SqlFrameUtil.outSqlCommon(sqlFrameBase, sqlComEnum.getFrameParamList(), params);
    }

    default String createIncrement(String sqlFrame, Map<String, String> paramsMap) {
        if (StringUtil.isNotEmpty((String)paramsMap.get("[AUTO_INCREMENT]"))) {
            sqlFrame = sqlFrame.replace("[[NOT] [NULL]]", "").replace("[<DEFAULT> {defaultValue}]", "");
        }

        return sqlFrame;
    }

    default String createIndex() {
        return "";
    }
}
