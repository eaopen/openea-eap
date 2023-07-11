package org.openea.eap.module.system.dal.dataobject.language;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;

/**
 * 国际化语言类型
 *
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

    @TableId
    private Long id;
    /**
     * 别名，iso name
     */
    protected String key;
    /**
     * 名称
     */
    protected String name;
    /**
     * 备注
     */
    protected String remark;

}
