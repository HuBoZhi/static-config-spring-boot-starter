package org.hubz.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hubz
 * @date 2023/5/11 23:08
 **/
public class ClassUtils {

    public static Field[] getAllFieldFromClass(Class<?> clazz) {
        return clazz.getFields();
    }

    public static List<String> getAllFieldNameFromClass(Class<?> clazz) {
        Field[] allFieldFromClass = getAllFieldFromClass(clazz);
        List<String> fieldNameList = new ArrayList<>();
        for (Field field : allFieldFromClass) {
            fieldNameList.add(field.getName());
        }
        return fieldNameList;
    }
}
