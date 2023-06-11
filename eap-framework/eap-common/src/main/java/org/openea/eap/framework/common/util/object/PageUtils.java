package org.openea.eap.framework.common.util.object;

import org.openea.eap.framework.common.pojo.PageParam;

/**
 * {@link org.openea.eap.framework.common.pojo.PageParam} 工具类
 *
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
