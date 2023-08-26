package org.openea.eap.module.system.dal.dataobject.dict;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.mybatis.core.dataobject.BaseDO;

import java.time.LocalDateTime;

/**
 * 字典类型表
 *
 * @author ruoyi
 */
@TableName("system_dict_type")
@KeySequence("system_dict_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeDO extends BaseDO {

    /**
     * 字典主键
     */
    @TableId
    private Long id;
    /**
     * 字典名称(简要描述)
     */
    private String name;
    /**
     * 字典类型（dictKey）
     */
    private String type;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;


    // 扩展字段
    /**
     * 树形 isTree
     */
    private Integer isTree;
    /**
     * 上级 parentId
     */
    private Long parentId;

    /**
     * 数据类型 data/json/sql/api
     * 默认为data
     */
    private String dataType;
    /**
     * json数据
     * 需要符合格式要求
     */
    private String dataJson;
    /**
     * 查询sql
     */
    private String dataSql;
    /**
     * 查询sql所需数据源
     * 默认为当前数据源
     */
    private String dataDs;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除时间
     */
    private LocalDateTime deletedTime;

}
