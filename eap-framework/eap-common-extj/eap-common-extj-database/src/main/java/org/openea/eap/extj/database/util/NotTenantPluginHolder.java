package org.openea.eap.extj.database.util;

import javax.swing.text.html.Option;

/**
 * 不执行多租户切库，字段拼接插件
 * 
 */
public class NotTenantPluginHolder {


    private static final ThreadLocal<Boolean> CONTEXT_NOTSWITCH_HOLDER = ThreadLocal.withInitial(()->Boolean.FALSE);
    private static final ThreadLocal<Boolean> CONTEXT_NOTSWITCH_ALWAYS_HOLDER = ThreadLocal.withInitial(()->Boolean.FALSE);

    /**
     * 只能生效一次查询
     */
    public static void setNotSwitchFlag(){
        CONTEXT_NOTSWITCH_HOLDER.set(Boolean.TRUE);
    }

    public static Boolean isNotSwitch(){
        return CONTEXT_NOTSWITCH_HOLDER.get();
    }

    public static void clearNotSwitchFlag(){
        CONTEXT_NOTSWITCH_HOLDER.remove();
    }


    /**
     * 只能生效一次查询
     */
    public static void setNotSwitchAlwaysFlag(){
        CONTEXT_NOTSWITCH_ALWAYS_HOLDER.set(Boolean.TRUE);
    }

    public static Boolean isNotSwitchAlways(){
        return CONTEXT_NOTSWITCH_ALWAYS_HOLDER.get();
    }

    public static void clearNotSwitchAlwaysFlag(){
        CONTEXT_NOTSWITCH_ALWAYS_HOLDER.remove();
    }



}
