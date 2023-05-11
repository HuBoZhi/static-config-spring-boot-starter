package org.hubz.common.utils;

import java.util.Objects;

/**
 * @author hubz
 * @date 2023/5/11 23:06
 **/
public class CastUtils {

    /**
     * 类型转换工具类
     * @author hubz
     * @date 2023/5/10 22:40
     *
     * @param fieldType 静态属性类型
     * @param value 配置项值
     * @return java.lang.Object 返回转换后的结果
     **/
    public static Object cast(Class<?> fieldType, String value) {
        if (Objects.isNull(fieldType)) {
            return null;
        }
        String fileTypeName = fieldType.getName();
        if ("java.lang.Integer".equals(fileTypeName)) {
            return Integer.parseInt(value);
        } else if ("java.lang.String".equals(fileTypeName)) {
            return value;
        }
        return null;
    }

}
