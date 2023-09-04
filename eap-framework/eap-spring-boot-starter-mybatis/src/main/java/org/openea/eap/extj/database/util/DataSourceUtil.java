package org.openea.eap.extj.database.util;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
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
        if(StringUtil.isEmpty(this.userName)){
            return DbTypeUtil.getEncodeDb(this.dbType).getDbaUsername();
        }else {
            return this.userName;
        }
    }

    public String getAutoPassword() throws DataException {
        if(StringUtil.isEmpty(this.password)){
            return getAutoUsername();
        }else {
            return this.password;
        }
    }

    /**
     * -- 这里的参数dataSourceUtil是spring托管的全局唯一变量，
     * New对象防止数据源互串，防止Bean覆盖
     * 获取数据源参数传参对象
     * 注意：此处方法不能命名为 get开头的名字，
     * 会出现copy bean转换时候的错误
     */
    public DbLinkEntity init(){
        return init(null);
    }

    /**
     * 多租户：获取数据源参数传参对象
     * @param dbName 库名
     * @return ignore
     */
    public DbLinkEntity init(String dbName){
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        BeanUtils.copyProperties(this, dbLinkEntity);
        if(StringUtil.isNotEmpty(dbName)){
            dbLinkEntity.setDbName(dbName);
        }
        return dbLinkEntity;
    }

    /**
     * 数据基础结构信息
     */
    public DbStruct getDbStruct(){
        DbStruct dbStruct = new DbStruct();
        // 用户名对应 (当数据库为Oracle与DM的时，schema默认与用户同名)
        dbStruct.setUserName(getUserName());
        dbStruct.setOracleDbSchema(getUserName());
        dbStruct.setDmDbSchema(getUserName());
        // 表空间
        dbStruct.setDbTableSpace(getDbTableSpace());
        //库名
        dbStruct.setMysqlDbName(getDbName());
        dbStruct.setSqlServerDbName(getDbName());
        dbStruct.setKingBaseDbName(getDbName());
        dbStruct.setPostGreDbName(getDbName());
        // 模式
        if(StringUtil.isNotEmpty(getDbSchema())){
            dbStruct.setOracleDbSchema(getDbSchema());
            dbStruct.setDmDbSchema(getDbSchema());
            dbStruct.setSqlServerDbSchema(getDbSchema());
            dbStruct.setKingBaseDbSchema(getDbSchema());
            dbStruct.setPostGreDbSchema(getDbSchema());
        }
        dbStruct.setOracleParam(getOracleParam());
        return dbStruct;
    }

    public DataSourceUtil() {
    }


    public static String getDbNameFromJdbcUrl(String jdbcUrl) {
        String url = jdbcUrl;
        if(jdbcUrl.contains("?")){
            url = jdbcUrl.split("\\?")[0];
        }
        String[] parts = url.split("/");
        if (parts.length > 0) {
            String dbName = parts[parts.length - 1];
            return dbName;
        }
        return null;
    }


}
