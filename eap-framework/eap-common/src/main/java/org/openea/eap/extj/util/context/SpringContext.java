package org.openea.eap.extj.util.context;

import org.openea.eap.framework.common.util.spring.EapAppUtil;

public class SpringContext{
    public static <T> T getBean(Class<T> beanClass) {
        return EapAppUtil.getBean(beanClass);
    }
}
