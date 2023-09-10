package org.openea.eap.extj.annotation;

import java.lang.annotation.*;

/**
 * 组织权限验证
 *
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositionPermission {
}
