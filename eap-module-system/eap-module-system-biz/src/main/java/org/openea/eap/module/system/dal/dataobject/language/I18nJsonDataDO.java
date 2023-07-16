package org.openea.eap.module.system.dal.dataobject.language;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;

/**
 * 翻译 DO
 *
 * @author eap
 */
@TableName("sys_i18n_data")
@KeySequence("sys_i18n_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class I18nJsonDataDO extends BaseDO {

    /**
     * PK
     */
    @TableId
    private Long id;
    /**
     * 模块，可选
     */
    private String module;
    /**
     * key/别名
     */
    private String alias;
    /**
     * 名称
     */
    private String name;
    /**
     * 多语言设置json
     */
    @JsonFormat
    private String json;
    /**
     * 备注
     */
    private String remark;

}
