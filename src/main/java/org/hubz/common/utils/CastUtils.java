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
     * @param targetType 静态属性类型
     * @param value 配置项值
     * @return java.lang.Object 返回转换后的结果
     **/
    public static Object cast(Class<?> targetType, Object value) {
        if (Objects.isNull(targetType)) {
            return null;
        }
        String fileTypeName = targetType.getName();
        if (StaticConfigCommonConstant.INTEGER_BASE_OBJECT_NAME.equals(fileTypeName)) {
            return Integer.parseInt(String.valueOf(value));
        } else if (StaticConfigCommonConstant.STRING_BASE_OBJECT_NAME.equals(fileTypeName)) {
            return value;
        } else if (StaticConfigCommonConstant.LONG_BASE_OBJECT_NAME.equals(fileTypeName)) {
            return Long.parseLong(String.valueOf(value));
        }
        return null;
    }

}
