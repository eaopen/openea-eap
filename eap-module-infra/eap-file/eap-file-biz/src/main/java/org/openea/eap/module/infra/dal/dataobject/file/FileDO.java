package org.openea.eap.module.infra.dal.dataobject.file;

import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 文件表
 * 每次文件上传，都会记录一条记录到该表中
 *
 */
@TableName("infra_file")
@KeySequence("infra_file_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    private Long id;

    /**
     * 模块
     * 不同模块采用不同存储策略和权限策略
     */
    private Long moduleId;

    /**
     * 4位年份
     * 创建时间年份，预留可能的分区字段
     */
    private Integer year;

    /**
     * 配置编号
     *
     * 关联 {@link FileConfigDO#getId()}
     */
    private Long configId;
    /**
     * 文件名
     */
    private String name;
    /**
     * 路径，本地相对路径，含文件名
     */
    private String path;
    /**
     * 访问地址, web访问路径
     */
    private String url;
    /**
     * 文件的 MIME 类型，例如 "application/octet-stream"
     */
    private String type;
    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 权限配置JSON
     */
    private String rightsJson;

    /**
     * 文件版本
     */
    private String fileVersion;

    /**
     * 上一个版本文件ID
     */
    private Long oldVersionFileId;

    /**
     * 文件内容md5
     */
    private String fileMd5;

    /**
     * 关联ID
     * 可能是业务ID或者历史迁移数据ID
     */
    private String refId;

    //临时文件？
    // 预览文件和略缩图文件采用约定文件名
    //预览文件
    //略缩图

}
