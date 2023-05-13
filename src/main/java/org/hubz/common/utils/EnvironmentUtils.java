package org.hubz.common.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author hubz
 * @date 2023/5/11 23:00
 **/
public final class EnvironmentUtils {

    private EnvironmentUtils() {
    }

    /**
     * 将同前缀的数据转换成指定对象列表
     * @author hubz
     * @date 2023/5/13 13:50
     *
     * @param environment 环境上下文
     * @param prefix 前缀
     * @param clazz 目标对象
     * @return java.util.List<T>
     **/
    public static <T> List<T> castSamePrefixPropertyToClazz(ConfigurableEnvironment environment, String prefix, Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HashSet<String> prefixProperty = getPrefixProperty(environment, prefix);
        return parsePropertiesToClazzInvoke(environment, prefixProperty, prefix, clazz);
    }

    /**
     * 获取指定前缀的配置项
     * @author hubz
     * @date 2023/5/13 14:20
     *
     * @param environment 上下文环境变量
     * @param prefix 前缀
     * @return java.util.HashSet<java.lang.String>含有指定前缀的配置项
     **/
    public static HashSet<String> getPrefixProperty(ConfigurableEnvironment environment, String prefix) {
        HashSet<String> propertyWithPrefix = new HashSet<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            Map<String, Object> properties = getAllProperties(propertySource);
            if (!CollectionUtils.isEmpty(properties)) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(prefix)) {
                        propertyWithPrefix.add(key);
                    }
                }
            }
        }
        return propertyWithPrefix;
    }

    /**
     * 获取某个配置源中全部的配置项
     * @author hubz
     * @date 2023/5/11 23:00
     *
     * @param propertySource 配置源
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getAllProperties(PropertySource<?> propertySource) {
        if (propertySource.getSource() instanceof Map) {
            return (Map<String, Object>) propertySource.getSource();
        }
        return null;
    }


    /**
     * 将具有相同前缀的配置项转换成对应的类
     * @author hubz
     * @date 2023/5/12 23:53
     *
     * @param propertyNameSet 具有相同前缀的配置项名
     * @param prefix 前缀
     * @param clazz 目标类型
     * @return java.util.List<T> 结果
     **/
    @SuppressWarnings("unchecked")
    public static <T> List<T> parsePropertiesToClazzInvoke(ConfigurableEnvironment environment, HashSet<String> propertyNameSet,
                                                           String prefix, Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (CollectionUtils.isEmpty(propertyNameSet)) {
            return new ArrayList<>();
        }

        int index = 0;
        int size = propertyNameSet.size();
        List<T> objList = new ArrayList<>();

        // 判断是否为Java基本数据类型对象
        Boolean checkBaseObject = ClassUtils.checkBaseObject(clazz);
        // 如果是Java基本数据类型对象则可以通过属性判断直接进行属性转换
        if (checkBaseObject) {
            while (index < size) {
                String tmpIndexPropertyName = prefix + "[" + index + "]";
                String valueObj = environment.getProperty(tmpIndexPropertyName);
                // 转换失败直接报错了
                Object cast = CastUtils.cast(clazz, valueObj);
                objList.add((T) cast);
                index++;
            }
        } else {
            // 通过反射获取类的所有属性名
            Field[] fieldFromClass = ClassUtils.getAllFieldFromClass(clazz);
            while (index < size) {
                // 创建实例
                Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
                T obj = declaredConstructor.newInstance();
                for (Field field : fieldFromClass) {
                    String fieldName = field.getName();
                    // 通过构造key，获取每一组中都有哪些属性有值，有值的就赋予近一个实例中去并返回给请求端
                    String tmpIndexPropertyName = prefix + "[" + index + "]." + fieldName;

                    Class<?> fieldType = field.getType();
                    Object castValue;
                    if (ClassUtils.checkBaseObject(clazz)) {
                        String valueObj = environment.getProperty(tmpIndexPropertyName);
                        // 转换失败直接报错了
                        castValue = CastUtils.cast(field.getType(), valueObj);
                    } else {
                        // 子类或者List<String>类似的结构
                        castValue = parsePropertiesToClazzInvoke(environment, propertyNameSet, tmpIndexPropertyName, fieldType);
                    }
                    // 将值设置到实例上
                    field.set(obj, castValue);
                }
                // 检查实例是否所有属性,有属性有值则加入，如果全为null则终止循环
                Boolean checkFieldsValueNonAllNull = ClassUtils.checkFieldsValueNonAllNull(clazz, obj);
                if (checkFieldsValueNonAllNull) {
                    objList.add(obj);
                    index++;
                } else {
                    break;
                }
            }
        }

        return objList;
    }

}
