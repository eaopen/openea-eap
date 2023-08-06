package org.openea.eap.extj.base.entity;

public abstract class SuperEntity<T> extends SuperBaseEntity.SuperCUDBaseEntity<T> {
    public SuperEntity() {
    }

    public String toString() {
        return "SuperEntity(super=" + super.toString() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SuperEntity)) {
            return false;
        } else {
            SuperEntity<?> other = (SuperEntity)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SuperEntity;
    }

    public int hashCode() {
        return 1;
    }
}