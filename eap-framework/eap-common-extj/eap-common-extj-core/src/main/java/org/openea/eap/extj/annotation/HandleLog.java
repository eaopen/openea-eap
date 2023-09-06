package org.openea.eap.extj.annotation;

import java.lang.annotation.*;

/**
 * 请求日志注解
 *
 * 
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleLog {

    /**
     * 操作模块
     *
     * @return
     */
    String moduleName() default "";

    /**
     * 操作方式
     *
     * @return
     */
    String requestMethod() default "";

}
