package org.hubz.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hubz
 * @date 2023/5/11 23:08
 **/
public class ClassUtils {

    /**
     * 获取一个类的所有属性
     * @author hubz
     * @date 2023/5/13 13:39
     *
     * @param clazz 类
     * @return java.lang.reflect.Field[] 属性数组
     **/
    public static Field[] getAllFieldFromClass(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return new Field[]{};
        }
        return clazz.getFields();
    }

    /**
     * 获取一个类的所有属性名
     * @author hubz
     * @date 2023/5/13 13:39
     *
     * @param clazz 类
     * @return java.util.List<java.lang.String> 属性名列表
     **/
    public static List<String> getAllFieldNameFromClass(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return new ArrayList<>();
        }
        Field[] allFieldFromClass = getAllFieldFromClass(clazz);
        List<String> fieldNameList = new ArrayList<>();
        for (Field field : allFieldFromClass) {
            fieldNameList.add(field.getName());
        }
        return fieldNameList;
    }

    /**
     * 检查是否为基础数据类型
     * @author hubz
     * @date 2023/5/13 13:13
     *
     * @param clazz 类
     * @return java.lang.Boolean true 是   false 否
     **/
    public static <T> Boolean checkBaseObject(Class<T> clazz) {
        String className = clazz.getName();
        return StaticConfigCommonConstant.BASE_DATA_OBJECT_LIST.contains(className);
    }

    /**
     * 获取某个类中拥有指定注解的属性列表
     * @author hubz
     * @date 2023/5/13 13:13
     *
     * @param clazz 类
     * @param annotationClass 注解
     * @return java.util.List<java.lang.reflect.Field> 含有指定注解的类属性
     **/
    public static <T extends Annotation> List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<T> annotationClass) {
        Field[] fields = getAllFieldFromClass(clazz);
        List<Field> fieldList = new ArrayList<>();
        for (Field field : fields) {
            T annotation = field.getAnnotation(annotationClass);
            if (Objects.nonNull(annotation)) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    /**
     * 检查对象的所有属性不全为空
     * @author hubz
     * @date 2023/5/13 13:48
     *
     * @param clazz 类
     * @param obj 实例
     * @return java.lang.Boolean true 不全为null   false 全为null
     **/
    public static <T> Boolean checkFieldsValueNonAllNull(Class<T> clazz, T obj) {
        Field[] fields = getAllFieldFromClass(clazz);
        for (Field field : fields) {
            Object valObj;
            try {
                valObj = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(valObj)) {
                return true;
            }
        }
        return false;
    }

}
