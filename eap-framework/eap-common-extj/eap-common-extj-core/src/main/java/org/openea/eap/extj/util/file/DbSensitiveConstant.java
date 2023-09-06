package org.openea.eap.extj.util.file;

import lombok.Data;

/**
 * 数据库敏感词
 *
 * 
 */
@Data
public class DbSensitiveConstant {

    /**
     * 数据库敏感词
     * INSERT,DELETE,UPDATE 不为敏感字
     */
    public static final String SENSITIVE = "CREATE,UNIQUE,CHECK,DEFAULT,DROP,INDEX,ALTER,TABLE,VIEW";

    /**
     * 数据库敏感词
     * INSERT,DELETE,UPDATE 不为敏感字
     */
    public static final String FILE_SENSITIVE = "<,>,/,\\\\,:,#,|,;";

}
