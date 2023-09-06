package org.openea.eap.extj.model.visualJson;

import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class TableModel {

     /**
     * 类型：1-主表、0-非主表
     */
     private String typeId;
     /**
     * 表名
     */
     private String table;
     /**
     * 说明
     */
     private String comment;
     /**
     * 主键
     */
     private String tableKey;
     /**
     * 外键字段
     */
     private String tableField;
     /**
     * 关联主表
     */
     private String relationTable;
     /**
     * 关联主键
     */
     private String relationField;

     private List<TableFields> fields;

     private String initName;
     /**
      * 是否子表, sub:子表 sub-jnpf:副表 main:主表
      */
     private String tableTag;
}
