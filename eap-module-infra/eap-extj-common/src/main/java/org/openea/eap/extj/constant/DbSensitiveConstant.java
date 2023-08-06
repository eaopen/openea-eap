package org.openea.eap.extj.constant;

public class DbSensitiveConstant {
    public static final String SENSITIVE = "CREATE,UNIQUE,CHECK,DEFAULT,DROP,INDEX,ALTER,TABLE,VIEW";
    public static final String FILE_SENSITIVE = "<,>,/,\\\\,:,|";

    public DbSensitiveConstant() {
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DbSensitiveConstant)) {
            return false;
        } else {
            DbSensitiveConstant other = (DbSensitiveConstant)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DbSensitiveConstant;
    }

    public int hashCode() {
        return 1;
    }

    public String toString() {
        return "DbSensitiveConstant()";
    }
}
