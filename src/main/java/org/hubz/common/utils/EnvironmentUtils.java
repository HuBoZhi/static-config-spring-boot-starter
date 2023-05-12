package org.hubz.common.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hubz
 * @date 2023/5/11 23:00
 **/
public final class EnvironmentUtils {

    private EnvironmentUtils() {
    }

    public static <T> List<T> castSamePrefixPropertyToClazz(ConfigurableEnvironment environment, String prefix, Class<T> clazz) {
        Map<String, Object> prefixProperty = getPrefixProperty(environment, prefix);
        return parsePropertiesToClazzInvoke(prefixProperty, prefix, clazz);
    }

    /**
     * 获取指定前缀的配置项
     * @author hubz
     * @date 2023/5/11 22:58
     *
     * @param environment 上下文环境变量
     * @param prefix 前缀
     * @return java.util.Map<java.lang.String, java.lang.Object> 含有指定前缀的配置项
     **/
    public static Map<String, Object> getPrefixProperty(ConfigurableEnvironment environment, String prefix) {
        Map<String, Object> propertyWithPrefix = new HashMap<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            Map<String, Object> properties = getAllProperties(propertySource);
            if (!CollectionUtils.isEmpty(properties)) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.startsWith(prefix)) {
                        propertyWithPrefix.put(key, value);
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
     * @param properties 具有相同前缀的配置项
     * @param prefix 前缀
     * @param clazz 目标类型
     * @return java.util.List<T> 结果
     **/
    @SuppressWarnings("unchecked")
    public static <T> List<T> parsePropertiesToClazzInvoke(Map<String, Object> properties, String prefix, Class<T> clazz) {
        if (CollectionUtils.isEmpty(properties)) {
            return new ArrayList<>();
        }

        int index = 0;
        int size = properties.size();
        List<T> objList = new ArrayList<>();

        // 判断是否为Java基本数据类型对象
        Boolean checkBaseObject = ClassUtils.checkBaseObject(clazz);
        // 如果是Java基本数据类型对象则可以通过属性判断直接进行属性转换
        if (checkBaseObject) {
            while (index < size) {
                String tmpIndexPropertyName = prefix + "[" + index + "]";
                Object valueObj = properties.get(tmpIndexPropertyName);
                // 转换失败直接报错了
                Object cast = CastUtils.cast(clazz, valueObj);
                objList.add((T) cast);
                index++;
            }
        } else {
            // 通过反射获取类的所有属性名
            Field[] fieldFromClass = ClassUtils.getAllFieldFromClass(clazz);
            // todo 通过构造key，获取每一组中都有哪些属性有值，有值的就赋予近一个实例中去并返回给请求端
            while (index < size) {
                // todo 创建实例

                for (Field field : fieldFromClass) {
                    String fieldName = field.getName();
                    String tmpIndexPropertyName = prefix + "[" + index + "]." + fieldName;
                    Object valueObj = properties.get(tmpIndexPropertyName);
                    // 转换失败直接报错了
                    Object cast = CastUtils.cast(field.getType(), valueObj);
                    // todo 将值设置到实例上

                }
                // todo 检查实例是否所有属性,有属性有值则加入，如果全为null则终止循环

                //objList.add();

                index++;
            }
        }

        return objList;
    }

}
