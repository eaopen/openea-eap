package org.openea.eap.extj.annotation;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrganizeAdminIsTrator {
    String value() default "";
}
