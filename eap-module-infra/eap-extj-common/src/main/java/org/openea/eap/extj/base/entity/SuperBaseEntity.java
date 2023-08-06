package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public abstract class SuperBaseEntity implements Serializable {

    @Data
    public abstract static class SuperCDBaseEntity<T> extends SuperCBaseEntity<T> {
        @TableField(
                value = "F_DELETEMARK",
                updateStrategy = FieldStrategy.IGNORED
        )
        private Integer deleteMark;
        @TableField(
                value = "F_DELETETIME",
                fill = FieldFill.UPDATE
        )
        private Date deleteTime;
        @TableField(
                value = "F_DELETEUSERID",
                fill = FieldFill.UPDATE
        )
        private String deleteUserId;

    }


    @Data
    public abstract static class SuperCUDBaseEntity<T> extends SuperCUBaseEntity<T> {
        @TableField(
                value = "F_DELETEMARK",
                updateStrategy = FieldStrategy.IGNORED
        )
        private Integer deleteMark;
        @TableField(
                value = "F_DELETETIME",
                fill = FieldFill.UPDATE
        )
        private Date deleteTime;
        @TableField(
                value = "F_DELETEUSERID",
                fill = FieldFill.UPDATE
        )
        private String deleteUserId;


    }

    @Data
    public abstract static class SuperCUBaseEntity<T> extends SuperCBaseEntity<T> {
        @TableField(
                value = "F_LASTMODIFYTIME",
                fill = FieldFill.UPDATE
        )
        private Date lastModifyTime;
        @TableField(
                value = "F_LASTMODIFYUSERID",
                fill = FieldFill.UPDATE
        )
        private String lastModifyUserId;


    }

    @Data
    public abstract static class SuperCBaseEntity<T> extends SuperTBaseEntity<T> {
        @TableField(
                value = "F_CREATORTIME",
                fill = FieldFill.INSERT
        )
        private Date creatorTime;
        @TableField(
                value = "F_CREATORUSERID",
                fill = FieldFill.INSERT
        )
        private String creatorUserId;

    }

    @Data
    public abstract static class SuperTBaseEntity<T> extends SuperIBaseEntity<T> {
        @TableField("F_TENANTID")
        private String tenantId;

    }

    @Data
    public abstract static class SuperIBaseEntity<T> extends SuperBaseEntity {
        @TableId("F_ID")
        public T id;

    }
}