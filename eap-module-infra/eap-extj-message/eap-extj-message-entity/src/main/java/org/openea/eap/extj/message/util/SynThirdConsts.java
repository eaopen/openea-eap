package org.openea.eap.extj.message.util;

/**
 * 第三方工具实体类
 */
public class SynThirdConsts {
    /**
     * 第三方工具类型(1:企业微信;2:钉钉)
     * 改为
     * 第三方工具类型(1:本地到企业微信;11:企业微信到本地;2:本地到钉钉;22:钉钉到本地)
     */
    public static final String THIRD_TYPE_QY = "1";

    public static final String THIRD_TYPE_QY_To_Sys = "11";
    /**
     * 本地到钉钉
     */
    public static final String THIRD_TYPE_DING = "2";
    /**
     * 钉钉到本地
     */
    public static final String THIRD_TYPE_DING_To_Sys = "22";

    /**
     * 数据类型(1:组织(公司与部门);2:用户)
     */
    public static final String DATA_TYPE_ORG = "1";
    public static final String DATA_TYPE_USER = "2";

    /**
     * 对象操作类型(add:创建;upd:修改)
     */
    public static final String OBJECT_OP_ADD = "add";
    public static final String OBJECT_OP_UPD = "upd";

    /**
     * 本系统组织对象类型(company:公司;department:部门)
     */
    public static final String OBJECT_TYPE_COMPANY = "company";
    public static final String OBJECT_TYPE_DEPARTMENT = "department";

    /**
     * 同步方向类型(1:本系统同步到第三方;2:第三方同步到本系统)
     */
    public static final Integer SYN_SYSTEM_TO_THIRD = 1;
    public static final Integer SYN_THIRD_TO_SYSTEM = 2;

    /**
     * 同步状态值(0:未同步;1:同步成功;2:同步失败)
     */
    public static final Integer SYN_STATE_NO = 0;
    public static final Integer SYN_STATE_OK = 1;
    public static final Integer SYN_STATE_FAIL = 2;

    /**
     * 企业微信的部门根节点ID
     */
    public static String QY_ROOT_DEPT_ID = "1";


    /**
     * 钉钉的部门根节点ID
     */
    public static Long DING_ROOT_DEPT_ID = 1L;

}
