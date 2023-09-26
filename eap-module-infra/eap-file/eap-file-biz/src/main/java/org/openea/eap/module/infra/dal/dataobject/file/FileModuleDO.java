package org.openea.eap.module.infra.dal.dataobject.file;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;


@TableName(value = "infra_file_module", autoResultMap = true)
@KeySequence("infra_file_module_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileModuleDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    private Long id;

    /**
     * 模块名
     */
    private String code;

    /**
     * 模块名
     */
    private String name;


    /**
     * 描述
     */
    private String description;


    /**
     * 路径前缀，可为空
     *
     * 本地目录配置通常为模块编码code，用于区别不同模块
     */
    private String prefix;

    /**
     * 权限配置JSON
     */
    private String rightsJson;

    /**
     * 水印配置json
     */
    private String watermarkJson;

    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    private Long configId;

}
