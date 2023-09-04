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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyDefaultSqlInjector extends DefaultSqlInjector {
    public static final String ignoreLogicPrefix = "IgnoreLogic";

    public MyDefaultSqlInjector() {
    }

    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        Stream.Builder<AbstractMethod> builder = Stream.builder();
        this.addInjector(builder, new Insert());
        this.addInjector(builder, new Delete());
        this.addInjector(builder, new DeleteByMap());
        this.addInjector(builder, new Update());
        this.addInjector(builder, new SelectByMap());
        this.addInjector(builder, new SelectCount());
        this.addInjector(builder, new SelectMaps());
        this.addInjector(builder, new SelectMapsPage());
        this.addInjector(builder, new SelectObjs());
        this.addInjector(builder, new SelectList());
        this.addInjector(builder, new SelectPage());
        if (tableInfo.havePK()) {
            this.addInjector(builder, new DeleteById());
            this.addInjector(builder, new DeleteBatchByIds());
            this.addInjector(builder, new UpdateById());
            this.addInjector(builder, new SelectById());
            this.addInjector(builder, new SelectBatchByIds());
        } else {
            this.logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType()));
        }

        return (List)builder.build().collect(Collectors.toList());
    }

    private void addInjector(Stream.Builder<AbstractMethod> builder, AbstractMethod method) {
        builder.add(method);
        builder.add(this.enhancerMethod(method));
    }

    private AbstractMethod enhancerMethod(AbstractMethod method) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(method.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (method.getName().equals("inject")) {
                    TableInfo tableInfo = (TableInfo)objects[3];
                    if (tableInfo.isWithLogicDelete()) {
                        TableInfo tableInfo1 = new TableInfo(tableInfo.getConfiguration(), tableInfo.getEntityType());
                        BeanUtil.copyProperties(tableInfo, tableInfo1, new String[0]);
                        ReflectionUtil.setFieldValue(tableInfo1, "withLogicDelete", false);
                        objects[3] = tableInfo1;
                    }
                }

                return methodProxy.invokeSuper(o, objects);
            }
        });
        return (AbstractMethod)enhancer.create(new Class[]{String.class}, new Object[]{ReflectionUtil.getFieldValue(method, "methodName") + "IgnoreLogic"});
    }

    private String getMethodName(AbstractMethod method) {
        try {
            Field field = method.getClass().getField("methodName");
            field.setAccessible(true);
            return field.get(method).toString();
        } catch (NoSuchFieldException var3) {
            throw new RuntimeException(var3);
        } catch (IllegalAccessException var4) {
            throw new RuntimeException(var4);
        }
    }
}
