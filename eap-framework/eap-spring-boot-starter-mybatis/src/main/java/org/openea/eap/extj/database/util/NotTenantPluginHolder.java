package org.openea.eap.extj.database.util;

public class NotTenantPluginHolder {
    private static final ThreadLocal<Boolean> CONTEXT_NOTSWITCH_HOLDER = ThreadLocal.withInitial(() -> {
        return Boolean.FALSE;
    });
    private static final ThreadLocal<Boolean> CONTEXT_NOTSWITCH_ALWAYS_HOLDER = ThreadLocal.withInitial(() -> {
        return Boolean.FALSE;
    });

    public NotTenantPluginHolder() {
    }

    public static void setNotSwitchFlag() {
        CONTEXT_NOTSWITCH_HOLDER.set(Boolean.TRUE);
    }

    public static Boolean isNotSwitch() {
        return (Boolean)CONTEXT_NOTSWITCH_HOLDER.get();
    }

    public static void clearNotSwitchFlag() {
        CONTEXT_NOTSWITCH_HOLDER.remove();
    }

    public static void setNotSwitchAlwaysFlag() {
        CONTEXT_NOTSWITCH_ALWAYS_HOLDER.set(Boolean.TRUE);
    }

    public static Boolean isNotSwitchAlways() {
        return (Boolean)CONTEXT_NOTSWITCH_ALWAYS_HOLDER.get();
    }

    public static void clearNotSwitchAlwaysFlag() {
        CONTEXT_NOTSWITCH_ALWAYS_HOLDER.remove();
    }
}