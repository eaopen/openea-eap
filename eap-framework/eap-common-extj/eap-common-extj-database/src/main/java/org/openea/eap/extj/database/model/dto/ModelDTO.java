package org.openea.eap.extj.database.model.dto;

import org.openea.eap.extj.database.source.DbBase;
import lombok.Data;

import java.sql.ResultSet;

/**
 * 自定义模板参数对象
 *
 *
 */
@Data
public class ModelDTO {

    public ModelDTO(ResultSet resultSet, String dbEncode){
        this.resultSet = resultSet;
        this.dbEncode = dbEncode;
    }

    public ModelDTO(ResultSet resultSet, DbBase dbBase){
        this.resultSet = resultSet;
    }

    /**
     * 结果集
     */
    private ResultSet resultSet;

    /**
     * 数据基类
     */
    private String dbEncode;

}
