package org.openea.eap.extj.form.util;

/**
 * 流程设计
 *
 *
 */
public enum TableFeildsEnum {
    FID("f_id" , "主键", "50","varchar" ,true,"NOT NULL",false),
    TENANTID("f_tenantid" , "租户id", "50","varchar" ,false,"NULL",false),
    VERSION("f_version" , "乐观锁", "20","int" ,false,"NULL",false),
    FLOWTASKID("f_flowtaskid" , "流程任务主键", "50","varchar" ,false,"NULL",false),
    FLOWID("f_flowid" , "流程id", "50","varchar" ,false,"NULL",false),
    DELETEMARK("f_deletemark" , "删除标志", "20","int" ,false,"NULL",false);


    TableFeildsEnum(String field, String comment, String length, String dataType, Boolean isPrimaryKey, String nullSign, Boolean isAutoIncrement) {
        this.field = field;
        this.comment = comment;
        this.length = length;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.nullSign = nullSign;
        this.isAutoIncrement = isAutoIncrement;

    }

    /**
     * 字段名
     */
    protected String field;

    /**
     * 注释
     */
    protected String comment;

    /**
     * 数据长度 (n,m) 或 (n)
     */
    protected String length;

    /**
     * 数据类型
     */
    protected String dataType;

    /**
     * 是否主键
     */
    protected Boolean isPrimaryKey;

    /**
     * 是否非空
     * （允空非空及0与1较容易混淆，故使用标识传作参数）
     */
    protected String nullSign;

    /**
     * 是否自增
     */
    protected Boolean isAutoIncrement;


    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getNullSign() {
        return nullSign;
    }

    public void setNullSign(String nullSign) {
        this.nullSign = nullSign;
    }

    public Boolean getAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
