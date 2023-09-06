package org.openea.eap.extj.database.constant;

import com.google.common.collect.ImmutableMap;
import org.openea.eap.extj.exception.DataException;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * 字段别名特殊标识
 *
 */
public class DbAliasConst {

    /**
     * 允空
     */
    public static final String NULL = "NULL";

    /**
     * 非空
     */
    public static final String NOT_NULL = "NOT NULL";

    /**
     * 主键
     */
    public static final Boolean IS_PRIMARY_KEY = true;

    /**
     * 非主键
     */
    public static final Boolean NOT_PRIMARY_KEY = false;

    /**
     * 主键
     */
    public static final Boolean IS_AUTO_INCREMENT = true;

    /**
     * 非主键
     */
    public static final Boolean NOT_AUTO_INCREMENT = false;

    /**
     * 允空
     * 0:空值 NULL、1:非空值 NOT NULL
     */
    public static final NumFieldAttr<String> ALLOW_NULL = new NumFieldAttr<>(ImmutableMap.of(
            1, NULL,
            0, NOT_NULL
    ));

    /**
     * 主键
     *  0:非主键、1：主键
     */
    public static final NumFieldAttr<Boolean> PRIMARY_KEY = new NumFieldAttr<>(ImmutableMap.of(
            1, IS_PRIMARY_KEY,
            0, NOT_PRIMARY_KEY
    ));

    public static final NumFieldAttr<Boolean> AUTO_INCREMENT = new NumFieldAttr<>(ImmutableMap.of(
            1, IS_AUTO_INCREMENT,
            0, NOT_AUTO_INCREMENT
    ));

    /**
     * 数值对应字段属性
     * @param <T>
     */
    @AllArgsConstructor
    public static class NumFieldAttr<T>{

        private Map<Integer, T> config;

        /**
         * 获取标识
         */
        public T getSign(int i) {
            return config.get(i);
        }

        /**
         * 获取数值
         */
        public Integer getNum(T sign) throws DataException {
            if(sign == null){
                return 0;
            }
            Optional<Map.Entry<Integer, T>> first = config.entrySet().stream().filter(map -> map.getValue().equals(sign)).findFirst();
            if(first.isPresent()){
                return first.get().getKey();
            }else {
                throw new DataException("表示对应获取数值失败");
            }
        }
    }


}
