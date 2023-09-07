package org.openea.eap.extj.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.exception.DataException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 联合主键
 *
 * 门户管理单条数据，可以由平台、门户ID、系统ID 三种数据定位
 * 它们组合成门户管理的联合主键，此类将其看成一个主键来配合QueryWrapper使用
 *
 */
public abstract class MyBatisPrimaryBase<T> {

    {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<T> clz = (Class<T>)actualTypeArguments[0];
        try {
            entity = clz.getDeclaredConstructor().newInstance();
        } catch (Exception ignore) {}
    }

    @Schema(description = "查询器")
    protected QueryWrapper<T> queryWrapper = new QueryWrapper<>();

    private T entity;

    @Schema(description = "获取处理后的查询器")
    public QueryWrapper<T> getQuery(){
        try{
            for (Field field : this.getClass().getDeclaredFields()) {
                try{
                    TableField annotation = entity.getClass().getDeclaredField(field.getName()).getAnnotation(TableField.class);
                    String columnName;
                    if(annotation != null) {
                        columnName = annotation.value();
                    }else if(field.getName().equalsIgnoreCase("id")) {
                        columnName = "F_Id";
                    }else if(field.getName().equalsIgnoreCase("creatorId")){
                        columnName = "F_Creator_Id";
                    }else {
                        columnName = field.getName();
                    }
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if(value != null) queryWrapper.eq(columnName, value);
                }catch (Exception ignore){}
            }
        }catch (Exception ignore){}
        return queryWrapper;
    }

    @Schema(description = "获取实例")
    public T getEntity() throws Exception {
        checkEntity();
        for (Field field : this.getClass().getDeclaredFields()) {
            for (Field entityField : entity.getClass().getDeclaredFields()) {
                if(entityField.getName().equals(field.getName())){
                    entityField.setAccessible(true);
                    field.setAccessible(true);
                    entityField.set(entity, field.get(this));
                }
            }
        }
        return entity;
    }

    private void checkEntity() throws Exception{
        for (Field field : this.getClass().getFields()) {
            Object o = field.get(this);
            if(o == null){
                throw new DataException("联合主键类缺少“" + field.getName() + "”字段值");
            }
        }
    }

}
