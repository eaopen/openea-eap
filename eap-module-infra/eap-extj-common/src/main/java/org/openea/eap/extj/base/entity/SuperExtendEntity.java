package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

public abstract class SuperExtendEntity<T> extends SuperEntity<T> {
    @TableField(
            value = "F_ENABLEDMARK",
            fill = FieldFill.INSERT
    )
    private Integer enabledMark;
    @TableField("F_DESCRIPTION")
    private String description;

    public SuperExtendEntity() {
    }

    public Integer getEnabledMark() {
        return this.enabledMark;
    }

    public String getDescription() {
        return this.description;
    }

    public void setEnabledMark(Integer enabledMark) {
        this.enabledMark = enabledMark;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "SuperExtendEntity(super=" + super.toString() + ", enabledMark=" + this.getEnabledMark() + ", description=" + this.getDescription() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SuperExtendEntity)) {
            return false;
        } else {
            SuperExtendEntity<?> other = (SuperExtendEntity)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$enabledMark = this.getEnabledMark();
                Object other$enabledMark = other.getEnabledMark();
                if (this$enabledMark == null) {
                    if (other$enabledMark != null) {
                        return false;
                    }
                } else if (!this$enabledMark.equals(other$enabledMark)) {
                    return false;
                }

                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description != null) {
                        return false;
                    }
                } else if (!this$description.equals(other$description)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SuperExtendEntity;
    }

    public int hashCode() {
        int result = 1;
        Object $enabledMark = this.getEnabledMark();
        result = result * 59 + ($enabledMark == null ? 43 : $enabledMark.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public abstract static class SuperExtendSortEntity<T> extends SuperExtendEntity<T> {
        @TableField("F_SORTCODE")
        private Long sortCode;

        public SuperExtendSortEntity() {
        }

        public Long getSortCode() {
            return this.sortCode;
        }

        public void setSortCode(Long sortCode) {
            this.sortCode = sortCode;
        }

        public String toString() {
            return "SuperExtendEntity.SuperExtendSortEntity(super=" + super.toString() + ", sortCode=" + this.getSortCode() + ")";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SuperExtendSortEntity)) {
                return false;
            } else {
                SuperExtendSortEntity<?> other = (SuperExtendSortEntity)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$sortCode = this.getSortCode();
                    Object other$sortCode = other.getSortCode();
                    if (this$sortCode == null) {
                        if (other$sortCode != null) {
                            return false;
                        }
                    } else if (!this$sortCode.equals(other$sortCode)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SuperExtendSortEntity;
        }

        public int hashCode() {
            int result = 1;
            Object $sortCode = this.getSortCode();
            result = result * 59 + ($sortCode == null ? 43 : $sortCode.hashCode());
            return result;
        }
    }
}
