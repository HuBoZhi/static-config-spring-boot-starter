package org.hubz.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hubz
 * @date 2023/5/12 23:32
 */
public class StaticConfigCommonConstant {

    public static List<String> BASE_DATA_OBJECT_LIST = new ArrayList<>();

    public static String STRING_BASE_OBJECT_NAME = "java.lang.String";
    public static String Integer_BASE_OBJECT_NAME = "java.lang.Integer";

    static {
        BASE_DATA_OBJECT_LIST.add(STRING_BASE_OBJECT_NAME);
        BASE_DATA_OBJECT_LIST.add(Integer_BASE_OBJECT_NAME);
    }
}
