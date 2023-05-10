package org.hubz.common.annocations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hubz
 * @date 2023/3/12 14:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public  @interface ExtraPropertyFile {

    String[] value();
}
