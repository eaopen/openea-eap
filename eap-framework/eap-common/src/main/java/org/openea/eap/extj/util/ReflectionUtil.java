package org.openea.eap.extj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.*;

public class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    private static boolean isDebug = false;

    public ReflectionUtil() {
    }

    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + StringUtil.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[0], new Object[0]);
    }

    public static void invokeSetterMethod(Object obj, String propertyName, Object value) {
        invokeSetterMethod(obj, propertyName, value, (Class)null);
    }

    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtil.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class[]{type}, new Object[]{value});
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        } else {
            Object result = null;

            try {
                result = field.get(obj);
            } catch (IllegalAccessException var5) {
                log.error("不可能抛出的异常{}", var5);
            }

            return result;
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        } else {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException var5) {
                log.error("不可能抛出的异常:{}", var5);
            }

        }
    }

    public static Field getAccessibleField(Object obj, String fieldName) {
        Assert.notNull(obj, "object不能为空");
        Assert.hasText(fieldName, "fieldName");
        Class<?> superClass = obj.getClass();

        while(superClass != Object.class) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        } else {
            try {
                return method.invoke(obj, args);
            } catch (Exception var6) {
                throw convertReflectionExceptionToUnchecked(var6);
            }
        }
    }

    public static Boolean invokeMethodByTask(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            return false;
        } else {
            try {
                method.invoke(obj, args);
                return true;
            } catch (Exception var6) {
                throw convertReflectionExceptionToUnchecked(var6);
            }
        }
    }

    public static Method getAccessibleMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        Assert.notNull(obj, "object不能为空");
        Class<?> superClass = obj.getClass();

        while(superClass != Object.class) {
            try {
                Method method = superClass.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException var5) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static <T> Class<T> getSuperClassGenricType(Class<T> clazz) {
        return (Class<T>) getSuperClassGenricType(clazz, 0);
    }

    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        } else {
            Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
                    return Object.class;
                } else {
                    return (Class)params[index];
                }
            } else {
                log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
                return Object.class;
            }
        }
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (!(e instanceof IllegalAccessException) && !(e instanceof IllegalArgumentException) && !(e instanceof NoSuchMethodException)) {
            if (e instanceof InvocationTargetException) {
                return new RuntimeException("Reflection jnpf.exception.", ((InvocationTargetException)e).getTargetException());
            } else {
                return e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException("Unexpected Checked jnpf.exception.", e);
            }
        } else {
            return new IllegalArgumentException("Reflection jnpf.exception.", e);
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj.getClass() != Object.class && !obj.getClass().isPrimitive()) {
            try {
                Method method = obj.getClass().getDeclaredMethod("toString");
                if (isDebug) {
                    log.debug("传入的对象实现了自己的toString方法，直接调用！");
                }

                return (String)method.invoke(obj);
            } catch (NoSuchMethodException var8) {
                if (isDebug) {
                    log.debug("传入的对象没有实现自己的toString方法，反射获取！");
                }

                StringBuffer buf = new StringBuffer(obj.getClass().getName());
                buf.append(" [");
                Field[] fileds = obj.getClass().getDeclaredFields();
                int size = fileds.length;

                for(int i = 0; i < size; ++i) {
                    Field field = fileds[i];
                    Object value = getFieldValue(obj, field.getName());
                    buf.append(field.getName() + "=" + toString(value));
                    if (i != size - 1) {
                        buf.append(", ");
                    }
                }

                buf.append("]");
                return buf.toString();
            } catch (Exception var9) {
                throw convertReflectionExceptionToUnchecked(var9);
            }
        } else {
            return obj.toString();
        }
    }
}
