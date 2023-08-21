package org.openea.eap.extj.util.context;

import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.framework.common.util.spring.EapAppUtil;

public class SpringContext extends EapAppUtil {
    public static <T> T getBean(Class<T> beanClass) {
        return EapAppUtil.getBean(beanClass);
    }
}
