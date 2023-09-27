package org.openea.eap.file.doc.dal.dataobject.doc;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;

/**
 * 文档模板
 *
 */
@TableName("infra_doc_template")
@KeySequence("infra_doc_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplate  extends BaseDO {

    /**
     * 编号，数据库自增
     */
    private Long id;

    // 分类

    // 模版间关系，文档处理中多个模板

}
