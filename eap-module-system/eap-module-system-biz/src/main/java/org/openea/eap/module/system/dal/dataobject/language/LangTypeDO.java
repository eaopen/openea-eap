package org.openea.eap.module.system.dal.dataobject.language;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;

/**
 * 语言 DO
 *
 * @author eap
 */
@TableName("sys_lang_type")
@KeySequence("sys_lang_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LangTypeDO extends BaseDO {

    /**
     * PK
     */
    @TableId
    private Long id;
    /**
     * key/别名
     */
    private String alias;
    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;

}
