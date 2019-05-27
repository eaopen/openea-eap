package org.openea.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("sys_sub")
public class SubSystemEntity implements Serializable {

    /**
     * 主键
     */
    protected String id;

    /**
     * 系统名称
     */
    protected String name;

    /**
     * 系统别名
     */
    protected String alias;

    /**
     * 是否可用 1 可用，0 ，不可用
     */
    protected Integer enabled = 1;

    /**
     * 描述
     */
    protected String desc;


    protected String config;


}
