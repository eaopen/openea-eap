package org.openea.eap.module.system.dal.dataobject.language;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;


@TableName("sys_i18n_data")
@KeySequence("sys_i18n_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class I18nJsonDataDO extends BaseDO {

    @TableId
    private Long id;
    protected String module; //备用
    protected String key;
    protected String name;
    protected String json;
    protected String remark;
}
