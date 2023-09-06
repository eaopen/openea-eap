package org.openea.eap.extj.database.plugins;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.openea.eap.extj.util.ReflectionUtil;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * MyBatisPlus自定义方法实现
 * 给默认方法新增IgnoreLogic结尾的方法用于操作已逻辑删除的数据
 * 
 */
public class MyDefaultSqlInjector extends DefaultSqlInjector {

    public static final String ignoreLogicPrefix = "IgnoreLogic";

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder();
        addInjector(builder, new Insert());
        addInjector(builder, new Delete());
        addInjector(builder, new DeleteByMap());
        addInjector(builder, new Update());
        addInjector(builder, new SelectByMap());
        addInjector(builder, new SelectCount());
        addInjector(builder, new SelectMaps());
        addInjector(builder, new SelectMapsPage());
        addInjector(builder, new SelectObjs());
        addInjector(builder, new SelectList());
        addInjector(builder, new SelectPage());
        if (tableInfo.havePK()) {
            addInjector(builder, new DeleteById());
            addInjector(builder, new DeleteBatchByIds());
            addInjector(builder, new UpdateById());
            addInjector(builder, new SelectById());
            addInjector(builder, new SelectBatchByIds());
        } else {
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method." ,
                    tableInfo.getEntityType()));
        }
        return builder.build().collect(toList());
    }

    private void addInjector(Stream.Builder<AbstractMethod> builder, AbstractMethod method) {
        builder.add(method);
        //默认方法新增IgnoreLogic结尾的方法
        builder.add(enhancerMethod(method));
    }

    private AbstractMethod enhancerMethod(AbstractMethod method){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(method.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if(method.getName().equals("inject")){
                    TableInfo tableInfo = (TableInfo) objects[3];
                    if(tableInfo.isWithLogicDelete()) {
                        TableInfo tableInfo1 = new TableInfo(tableInfo.getConfiguration(), tableInfo.getEntityType());
                        BeanUtil.copyProperties(tableInfo, tableInfo1);
                        ReflectionUtil.setFieldValue(tableInfo1, "withLogicDelete" , false);
                        objects[3] = tableInfo1;
                    }
                }
                return methodProxy.invokeSuper(o, objects);
            }
        });
        return (AbstractMethod) enhancer.create(new Class[]{String.class}, new Object[]{ReflectionUtil.getFieldValue(method, "methodName" ) + ignoreLogicPrefix});
    }

    private String getMethodName(AbstractMethod method) {
        try {
            Field field = method.getClass().getField("methodName" );
            field.setAccessible(true);
            return field.get(method).toString();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
