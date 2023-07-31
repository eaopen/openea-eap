package org.openea.eap.module.visualdev.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

public abstract class SuperBaseEntity implements Serializable {
    public SuperBaseEntity() {
    }

    public String toString() {
        return "SuperBaseEntity(super=" + super.toString() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SuperBaseEntity)) {
            return false;
        } else {
            SuperBaseEntity other = (SuperBaseEntity)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SuperBaseEntity;
    }

    public int hashCode() {
        return 1;
    }

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

        public SuperCDBaseEntity() {
        }

        public Integer getDeleteMark() {
            return this.deleteMark;
        }

        public Date getDeleteTime() {
            return this.deleteTime;
        }

        public String getDeleteUserId() {
            return this.deleteUserId;
        }

        public void setDeleteMark(Integer deleteMark) {
            this.deleteMark = deleteMark;
        }

        public void setDeleteTime(Date deleteTime) {
            this.deleteTime = deleteTime;
        }

        public void setDeleteUserId(String deleteUserId) {
            this.deleteUserId = deleteUserId;
        }

        public String toString() {
            return "SuperBaseEntity.SuperCDBaseEntity(super=" + super.toString() + ", deleteMark=" + this.getDeleteMark() + ", deleteTime=" + this.getDeleteTime() + ", deleteUserId=" + this.getDeleteUserId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperCDBaseEntity)) {
                return false;
            } else {
                SuperCDBaseEntity<?> other = (SuperCDBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    label47: {
                        Object this$deleteMark = this.getDeleteMark();
                        Object other$deleteMark = other.getDeleteMark();
                        if (this$deleteMark == null) {
                            if (other$deleteMark == null) {
                                break label47;
                            }
                        } else if (this$deleteMark.equals(other$deleteMark)) {
                            break label47;
                        }

                        return false;
                    }

                    Object this$deleteTime = this.getDeleteTime();
                    Object other$deleteTime = other.getDeleteTime();
                    if (this$deleteTime == null) {
                        if (other$deleteTime != null) {
                            return false;
                        }
                    } else if (!this$deleteTime.equals(other$deleteTime)) {
                        return false;
                    }

                    Object this$deleteUserId = this.getDeleteUserId();
                    Object other$deleteUserId = other.getDeleteUserId();
                    if (this$deleteUserId == null) {
                        if (other$deleteUserId != null) {
                            return false;
                        }
                    } else if (!this$deleteUserId.equals(other$deleteUserId)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperCDBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $deleteMark = this.getDeleteMark();
            result = result * 59 + ($deleteMark == null ? 43 : $deleteMark.hashCode());
            Object $deleteTime = this.getDeleteTime();
            result = result * 59 + ($deleteTime == null ? 43 : $deleteTime.hashCode());
            Object $deleteUserId = this.getDeleteUserId();
            result = result * 59 + ($deleteUserId == null ? 43 : $deleteUserId.hashCode());
            return result;
        }
    }

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

        public SuperCUDBaseEntity() {
        }

        public Integer getDeleteMark() {
            return this.deleteMark;
        }

        public Date getDeleteTime() {
            return this.deleteTime;
        }

        public String getDeleteUserId() {
            return this.deleteUserId;
        }

        public void setDeleteMark(Integer deleteMark) {
            this.deleteMark = deleteMark;
        }

        public void setDeleteTime(Date deleteTime) {
            this.deleteTime = deleteTime;
        }

        public void setDeleteUserId(String deleteUserId) {
            this.deleteUserId = deleteUserId;
        }

        public String toString() {
            return "SuperBaseEntity.SuperCUDBaseEntity(super=" + super.toString() + ", deleteMark=" + this.getDeleteMark() + ", deleteTime=" + this.getDeleteTime() + ", deleteUserId=" + this.getDeleteUserId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperCUDBaseEntity)) {
                return false;
            } else {
                SuperCUDBaseEntity<?> other = (SuperCUDBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    label47: {
                        Object this$deleteMark = this.getDeleteMark();
                        Object other$deleteMark = other.getDeleteMark();
                        if (this$deleteMark == null) {
                            if (other$deleteMark == null) {
                                break label47;
                            }
                        } else if (this$deleteMark.equals(other$deleteMark)) {
                            break label47;
                        }

                        return false;
                    }

                    Object this$deleteTime = this.getDeleteTime();
                    Object other$deleteTime = other.getDeleteTime();
                    if (this$deleteTime == null) {
                        if (other$deleteTime != null) {
                            return false;
                        }
                    } else if (!this$deleteTime.equals(other$deleteTime)) {
                        return false;
                    }

                    Object this$deleteUserId = this.getDeleteUserId();
                    Object other$deleteUserId = other.getDeleteUserId();
                    if (this$deleteUserId == null) {
                        if (other$deleteUserId != null) {
                            return false;
                        }
                    } else if (!this$deleteUserId.equals(other$deleteUserId)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperCUDBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $deleteMark = this.getDeleteMark();
            result = result * 59 + ($deleteMark == null ? 43 : $deleteMark.hashCode());
            Object $deleteTime = this.getDeleteTime();
            result = result * 59 + ($deleteTime == null ? 43 : $deleteTime.hashCode());
            Object $deleteUserId = this.getDeleteUserId();
            result = result * 59 + ($deleteUserId == null ? 43 : $deleteUserId.hashCode());
            return result;
        }
    }

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

        public SuperCUBaseEntity() {
        }

        public Date getLastModifyTime() {
            return this.lastModifyTime;
        }

        public String getLastModifyUserId() {
            return this.lastModifyUserId;
        }

        public void setLastModifyTime(Date lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public void setLastModifyUserId(String lastModifyUserId) {
            this.lastModifyUserId = lastModifyUserId;
        }

        public String toString() {
            return "SuperBaseEntity.SuperCUBaseEntity(super=" + super.toString() + ", lastModifyTime=" + this.getLastModifyTime() + ", lastModifyUserId=" + this.getLastModifyUserId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperCUBaseEntity)) {
                return false;
            } else {
                SuperCUBaseEntity<?> other = (SuperCUBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$lastModifyTime = this.getLastModifyTime();
                    Object other$lastModifyTime = other.getLastModifyTime();
                    if (this$lastModifyTime == null) {
                        if (other$lastModifyTime != null) {
                            return false;
                        }
                    } else if (!this$lastModifyTime.equals(other$lastModifyTime)) {
                        return false;
                    }

                    Object this$lastModifyUserId = this.getLastModifyUserId();
                    Object other$lastModifyUserId = other.getLastModifyUserId();
                    if (this$lastModifyUserId == null) {
                        if (other$lastModifyUserId != null) {
                            return false;
                        }
                    } else if (!this$lastModifyUserId.equals(other$lastModifyUserId)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperCUBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $lastModifyTime = this.getLastModifyTime();
            result = result * 59 + ($lastModifyTime == null ? 43 : $lastModifyTime.hashCode());
            Object $lastModifyUserId = this.getLastModifyUserId();
            result = result * 59 + ($lastModifyUserId == null ? 43 : $lastModifyUserId.hashCode());
            return result;
        }
    }

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

        public SuperCBaseEntity() {
        }

        public Date getCreatorTime() {
            return this.creatorTime;
        }

        public String getCreatorUserId() {
            return this.creatorUserId;
        }

        public void setCreatorTime(Date creatorTime) {
            this.creatorTime = creatorTime;
        }

        public void setCreatorUserId(String creatorUserId) {
            this.creatorUserId = creatorUserId;
        }

        public String toString() {
            return "SuperBaseEntity.SuperCBaseEntity(super=" + super.toString() + ", creatorTime=" + this.getCreatorTime() + ", creatorUserId=" + this.getCreatorUserId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperCBaseEntity)) {
                return false;
            } else {
                SuperCBaseEntity<?> other = (SuperCBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$creatorTime = this.getCreatorTime();
                    Object other$creatorTime = other.getCreatorTime();
                    if (this$creatorTime == null) {
                        if (other$creatorTime != null) {
                            return false;
                        }
                    } else if (!this$creatorTime.equals(other$creatorTime)) {
                        return false;
                    }

                    Object this$creatorUserId = this.getCreatorUserId();
                    Object other$creatorUserId = other.getCreatorUserId();
                    if (this$creatorUserId == null) {
                        if (other$creatorUserId != null) {
                            return false;
                        }
                    } else if (!this$creatorUserId.equals(other$creatorUserId)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperCBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $creatorTime = this.getCreatorTime();
            result = result * 59 + ($creatorTime == null ? 43 : $creatorTime.hashCode());
            Object $creatorUserId = this.getCreatorUserId();
            result = result * 59 + ($creatorUserId == null ? 43 : $creatorUserId.hashCode());
            return result;
        }
    }

    public abstract static class SuperTBaseEntity<T> extends SuperIBaseEntity<T> {
        @TableField("F_TENANTID")
        private String tenantId;

        public SuperTBaseEntity() {
        }

        public String getTenantId() {
            return this.tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String toString() {
            return "SuperBaseEntity.SuperTBaseEntity(super=" + super.toString() + ", tenantId=" + this.getTenantId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperTBaseEntity)) {
                return false;
            } else {
                SuperTBaseEntity<?> other = (SuperTBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$tenantId = this.getTenantId();
                    Object other$tenantId = other.getTenantId();
                    if (this$tenantId == null) {
                        if (other$tenantId != null) {
                            return false;
                        }
                    } else if (!this$tenantId.equals(other$tenantId)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperTBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $tenantId = this.getTenantId();
            result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
            return result;
        }
    }

    public abstract static class SuperIBaseEntity<T> extends SuperBaseEntity {
        @TableId("F_ID")
        public T id;

        public SuperIBaseEntity() {
        }

        public T getId() {
            return this.id;
        }

        public void setId(T id) {
            this.id = id;
        }

        public String toString() {
            return "SuperBaseEntity.SuperIBaseEntity(super=" + super.toString() + ", id=" + this.getId() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperIBaseEntity)) {
                return false;
            } else {
                SuperIBaseEntity<?> other = (SuperIBaseEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id != null) {
                            return false;
                        }
                    } else if (!this$id.equals(other$id)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperIBaseEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $id = this.getId();
            result = result * 59 + ($id == null ? 43 : $id.hashCode());
            return result;
        }
    }
}