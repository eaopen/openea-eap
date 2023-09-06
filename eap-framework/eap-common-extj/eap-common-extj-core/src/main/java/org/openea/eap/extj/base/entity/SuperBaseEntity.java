package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperBaseEntity implements Serializable {


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperIBaseEntity<T> extends SuperBaseEntity {

        /**
         * 主键
         */
        @TableId("F_ID")
        public T id;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperTBaseEntity<T> extends SuperIBaseEntity<T> {

        /**
         * 租户id
         */
        @TableField("F_TENANTID")
        private String tenantId;

    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperCBaseEntity<T> extends SuperTBaseEntity<T> {

        /**
         * 创建时间
         */
        @TableField(value = "F_CREATORTIME" , fill = FieldFill.INSERT)
        private Date creatorTime;

        /**
         * 创建用户
         */
        @TableField(value = "F_CREATORUSERID" , fill = FieldFill.INSERT)
        private String creatorUserId;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperCUBaseEntity<T> extends SuperCBaseEntity<T> {

        /**
         * 修改时间
         */
        @TableField(value = "F_LASTMODIFYTIME" , fill = FieldFill.UPDATE)
        private Date lastModifyTime;

        /**
         * 修改用户
         */
        @TableField(value = "F_LASTMODIFYUSERID" , fill = FieldFill.UPDATE)
        private String lastModifyUserId;

    }


    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperCUDBaseEntity<T> extends SuperCUBaseEntity<T> {

        /**
         * 删除标志
         */
        @TableField(value = "F_DELETEMARK" , updateStrategy = FieldStrategy.IGNORED)
        private Integer deleteMark;

        /**
         * 删除时间
         */
        @TableField(value = "F_DELETETIME" , fill = FieldFill.UPDATE)
        private Date deleteTime;

        /**
         * 删除用户
         */
        @TableField(value = "F_DELETEUSERID" , fill = FieldFill.UPDATE)
        private String deleteUserId;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperCDBaseEntity<T> extends SuperCBaseEntity<T> {

        /**
         * 删除标志
         */
        @TableField(value = "F_DELETEMARK" , updateStrategy = FieldStrategy.IGNORED)
        private Integer deleteMark;

        /**
         * 删除时间
         */
        @TableField(value = "F_DELETETIME" , fill = FieldFill.UPDATE)
        private Date deleteTime;

        /**
         * 删除用户
         */
        @TableField(value = "F_DELETEUSERID" , fill = FieldFill.UPDATE)
        private String deleteUserId;
    }


}

