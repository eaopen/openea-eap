package org.openea.eap.module.system.dal.dataobject.dict;

import com.alibaba.fastjson.JSONObject;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据表
 *
 * @author ruoyi
 */
@TableName("system_dict_data")
@KeySequence("system_dict_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataDO extends BaseDO {

    /**
     * 字典数据编号
     */
    @TableId
    private Long id;
    /**
     * 字典排序
     */
    private Integer sort;
    /**
     * 字典标签
     */
    private String label;
    /**
     * 字典值
     */
    private String value;
    /**
     * 字典类型（dictKey）
     *
     * 冗余 {@link DictDataDO#getDictType()}
     */
    private String dictType;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 颜色类型
     *
     * 对应到 element-ui 为 default、primary、success、info、warning、danger
     */
    private String colorType;
    /**
     * css 样式
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String cssClass;
    /**
     * 备注
     */
    private String remark;

    // 扩展字段
    /**
     * 上级 parentId
     * 用于树形结构
     */
    private Long parentId;

    /**
     * 扩展属性
     * 用于显示json/sql/api返回的扩展字段
     */
    @TableField(exist = false)
    private JSONObject extendProps;

}
