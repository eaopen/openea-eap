package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
@TableName("base_print_log")
public class PrintLogEntity extends SuperBaseEntity.SuperTBaseEntity {
    @TableId("F_Id")
    private String id;
    /**
     * 打印人
     */
    @TableField("F_PrintMan")
    private String printMan;
    /**
     * 打印时间
     */
    @TableField("F_PrintTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh")
    private Date printTime;
    /**
     * 打印条数
     */
    @TableField("F_PrintNum")
    private Integer printNum;
    /**
     * 打印功能名称
     */
    @TableField("F_PrintTitle")
    private String printTitle;

    /**
     * 基于哪一个模板
     */
    @TableField("F_PrintId")
    private String printId;

}
