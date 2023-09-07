package org.openea.eap.extj.onlinedev.model.OnlineDevListModel;

import lombok.Data;

@Data
public class OnlineListSqlModel {
    /**
     * 主表
     */
    private String mainTable;
    /**
     * 用到的字段
     */
    private String fields;
    /**
     * 主键
     */
    private String pKeyName;
    /**
     * 数据权限条件
     */
    private String resultSql;

    private String defaultSidx;
    private String sort;
}
