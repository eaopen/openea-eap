package org.openea.eap.extj.database.util;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.openea.eap.extj.database.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "spring.datasource"
)
@Data
public class DataSourceUtil implements DbSourceOrDbLink {
    @TableField("F_DBTYPE")
    private String dbType;
    @TableField("F_HOST")
    private String host;
    @TableField("F_PORT")
    private Integer port;
    @TableField("F_SERVICENAME")
    private String dbName;
    @TableField("F_USERNAME")
    private String userName;
    @TableField("F_PASSWORD")
    private String password;
    @TableField("F_TABLESPACE")
    private String dbTableSpace;
    @TableField("F_DBSCHEMA")
    private String dbSchema;
    @TableField(
            exist = false
    )
    private String urlParams;
    @TableField(
            exist = false
    )
    private String url;
    @TableField(
            exist = false
    )
    private String prepareUrl;
    @TableField(
            exist = false
    )
    private String driver;
    @TableField("F_OracleParam")
    private String oracleParam;

    public String getAutoUsername() throws DataException {
        //return StringUtil.isEmpty(this.userName) ? DbTypeUtil.getEncodeDb(this.dbType).getDbaUsername() : this.userName;
        return "";
    }

    public String getAutoPassword() throws DataException {
        return StringUtil.isEmpty(this.password) ? this.getAutoUsername() : this.password;
    }


    @Override
    public DbStruct getDbStruct() {
        return null;
    }

    @Override
    public DbLinkEntity init() {
        return null;
    }

    @Override
    public DbLinkEntity init(String var1) {
        return null;
    }
}
