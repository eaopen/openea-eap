package org.openea.eap.extj.base.model.dblink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.impl.DbOracle;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.JsonUtilEx;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.XSSEscape;

import java.util.Map;

@Data
public class DbLinkInfoVO extends DbLinkBaseForm {

    /**
     * 获取连接页面显示对象
     * @param entity 连接实体对象
     * @return 返回显示对象
     * @throws DataException ignore
     */
    public DbLinkInfoVO getDbLinkInfoVO(DbLinkEntity entity) throws DataException {
        DbLinkInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DbLinkInfoVO.class);
        vo.setServiceName(XSSEscape.escape(entity.getDbName()));
        vo.setTableSpace(XSSEscape.escape(entity.getDbTableSpace()));
        if(StringUtil.isNotEmpty(entity.getOracleParam())){
            Map<String, Object> oracleParam = JsonUtil.stringToMap(entity.getOracleParam());
            if(oracleParam.size() > 0){
                vo.setOracleLinkType(oracleParam.get(DbOracle.ORACLE_LINK_TYPE).toString());
                vo.setOracleRole(oracleParam.get(DbOracle.ORACLE_ROLE).toString());
                vo.setOracleService(oracleParam.get(DbOracle.ORACLE_SERVICE).toString());
            }
        }
        return vo;
    }

    @Schema(description = "主键")
    private String id;

}
