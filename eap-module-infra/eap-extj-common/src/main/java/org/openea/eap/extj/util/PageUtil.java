package org.openea.eap.extj.util;

import java.util.List;

public class PageUtil {
    public PageUtil() {
    }

    public static List getListPage(int page, int pageSize, List list) {
        if (list != null && list.size() != 0) {
            int totalCount = list.size();
            --page;
            int fromIndex = page * pageSize;
            if (fromIndex >= totalCount) {
                return list;
            } else {
                int toIndex = (page + 1) * pageSize;
                if (toIndex > totalCount) {
                    toIndex = totalCount;
                }

                return list.subList(fromIndex, toIndex);
            }
        } else {
            return list;
        }
    }
}
