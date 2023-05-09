package org.hubz.common.annocations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限定属性
 * 限定Runtime时存在
 * @author hubz
 * @date 2023/5/9 0:06
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StaticPropertyName {
    /**
     * 配置项名
     */
    String value();

}
