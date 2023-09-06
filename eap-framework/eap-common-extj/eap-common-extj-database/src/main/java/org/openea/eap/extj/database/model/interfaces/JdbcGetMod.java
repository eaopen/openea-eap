package org.openea.eap.extj.database.model.interfaces;

import org.openea.eap.extj.database.model.dto.ModelDTO;

import java.sql.SQLException;

/**
 * 数据模板接口
 *
 * 
 */
public interface JdbcGetMod {

     /**
      * 设置自定义模板接口
      * @param modelDTO 模板相关参数
      * @throws SQLException ignore
      */
     void setMod(ModelDTO modelDTO) throws SQLException;

}
