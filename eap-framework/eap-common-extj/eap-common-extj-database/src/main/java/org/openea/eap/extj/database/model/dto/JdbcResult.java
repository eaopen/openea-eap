package org.openea.eap.extj.database.model.dto;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 类功能
 *
 */
@Data
@Accessors(chain = true)
public class JdbcResult<R> {

    /**
     * 别名小写开关
     */
    private Boolean isLowerCase = false;

    /**
     * 别名开关
     */
    private Boolean isAlias = false;

    /**
     * 是查询结构还是查询值
     */
    private Boolean isValue = true;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private MyFunction<JdbcResult<R>, R> func;

    public JdbcResult(MyFunction<JdbcResult<R>, R> func){
        this.func = func;
    }

    /**
     * 在get的时候才进行查询操作
     */
    public R get() throws Exception {
        return this.func.apply(this);
    }

    @FunctionalInterface
    public static interface MyFunction<T, R>{
        R apply(T t) throws Exception;
    }

}
