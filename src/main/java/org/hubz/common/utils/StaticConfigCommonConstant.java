package org.hubz.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hubz
 * @date 2023/5/12 23:32
 */
public class StaticConfigCommonConstant {

    public static List<String> BASE_DATA_OBJECT_LIST = new ArrayList<>();

    // region
    /**
     * 基本数据类型
     */
    public static String SHORT_BASE_OBJECT_NAME = "java.lang.Short";
    public static String STRING_BASE_OBJECT_NAME = "java.lang.String";
    public static String INTEGER_BASE_OBJECT_NAME = "java.lang.Integer";
    public static String LONG_BASE_OBJECT_NAME = "java.lang.Long";
    public static String DOUBLE_BASE_OBJECT_NAME = "java.lang.Double";
    public static String BOOLEAN_BASE_OBJECT_NAME = "java.lang.Boolean";
    public static String CHARACTER_BASE_OBJECT_NAME = "java.lang.Character";
    public static String FLOAT_BASE_OBJECT_NAME = "java.lang.Float";
    // endregion

    static {
        BASE_DATA_OBJECT_LIST.add(STRING_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(INTEGER_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(SHORT_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(LONG_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(DOUBLE_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(BOOLEAN_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(CHARACTER_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(FLOAT_BASE_OBJECT_NAME);
    }
}
