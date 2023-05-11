package org.hubz.common.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

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

    /**
     * 获取指定前缀的配置项
     * @author hubz
     * @date 2023/5/11 22:58
     *
     * @param environment 上下文环境变量
     * @param prefix 前缀
     * @return java.util.Map<java.lang.String, java.lang.Object> 含有指定前缀的配置项
     **/
    public Map<String, Object> getPrefixProperty(ConfigurableEnvironment environment, String prefix) {
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
    private Map<String, Object> getAllProperties(PropertySource<?> propertySource) {
        if (propertySource.getSource() instanceof Map) {
            return (Map<String, Object>) propertySource.getSource();
        }
        return null;
    }


    // 工具类，将具有相同前缀的配置项转换成对应的类
    public <T> List<T> xx(Map<String, Object> properties, String prefix, Class<T> clazz) {
        if (CollectionUtils.isEmpty(properties)) {
            return new ArrayList<>();
        }
        // todo 判断是否为Java基本数据类型对象

        // todo 如果是单属性的则可以通过属性判断直接进行属性转换

        // 通过反射获取类的所有属性名
        List<String> fieldNameList = ClassUtils.getAllFieldNameFromClass(clazz);
        int index = 0;
        int size = properties.size();
        List<T> objList = new ArrayList<>();
        // todo 通过构造key，获取每一组中都有哪些属性有值，有值的就赋予近一个实例中去并返回给请求端
        while (index < size) {
            // todo 创建实例
            for (String fieldName : fieldNameList) {


            }
            // todo 检查实例是否所有属性,有属性有值则加入，如果全为null则终止循环
            //objList.add();
            index++;
        }

        return objList;
    }

}
