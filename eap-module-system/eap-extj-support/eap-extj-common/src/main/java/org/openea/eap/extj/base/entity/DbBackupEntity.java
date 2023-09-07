package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

import java.util.Date;

@Data
@TableName("base_dbbackup")
public class DbBackupEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 备份库名
     */
    @TableField("F_BACKUPDBNAME")
    private String backupDbName;

    /**
     * 备份时间
     */
    @TableField("F_BACKUPTIME")
    private Date backupTime;

    /**
     * 文件名称
     */
    @TableField("F_FILENAME")
    private String fileName;

    /**
     * 文件大小
     */
    @TableField("F_FILESIZE")
    private String fileSize;

    /**
     * 文件路径
     */
    @TableField("F_FILEPATH")
    private String filePath;

}
