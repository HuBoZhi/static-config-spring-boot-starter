package org.hubz.common.event;

import org.hubz.common.annocations.ExtraPropertyFile;
import org.hubz.common.annocations.StaticPropertyName;
import org.hubz.common.exceptions.LoadExtraPropertyFileException;
import org.hubz.common.exceptions.PropertyNotFoundException;
import org.hubz.common.utils.CastUtils;
import org.hubz.common.utils.ClassUtils;
import org.hubz.common.utils.EnvironmentUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author hubz
 * @date 2023/5/9 22:01
 **/
public class StaticConfigApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    // todo 属性刷新时需要实时更新这些静态参数
    // https://blog.csdn.net/wangfenglei123456/article/details/128634050
    // https://blog.csdn.net/MoRan_Lei/article/details/123803435
    private static final String STATIC_CONFIG_CLASS = "static.config.class";


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        // 获取静态配置类数组
        List<String> staticConfigClassNameList;
        try {
            staticConfigClassNameList = EnvironmentUtils.castSamePrefixPropertyToClazz(environment,
                    STATIC_CONFIG_CLASS, String.class);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("load static config class error", e);
        }

        // 校验不为空,不为空这个功能才启用
        if (!CollectionUtils.isEmpty(staticConfigClassNameList)) {
            // 去重处理
            HashSet<String> staticConfigClassNameSet = new HashSet<>(staticConfigClassNameList);
            // 加载需要配置的静态类
            List<Class<?>> staticConfigClassList = loadStaticConfigClass(staticConfigClassNameSet);
            // 将配置文件中的配置注入到静态变量中
            configSetToStaticField(environment, staticConfigClassList);
            // 检查静态配置文件中需要配置的属性
            checkStaticField(staticConfigClassList);
        }

    }

    /**
     * 加载需要配置的静态类
     * @author hubz
     * @date 2023/5/13 13:32
     *
     * @param staticConfigClassNameSet 静态配置类数组
     * @return java.util.List<java.lang.Class < ?>> 加载后的静态配置类
     **/
    private List<Class<?>> loadStaticConfigClass(HashSet<String> staticConfigClassNameSet) {
        List<Class<?>> staticConfigClassList = new ArrayList<>();
        for (String staticConfigClassName : staticConfigClassNameSet) {
            // 加载类
            Class<?> staticConfigClass;
            try {
                staticConfigClass = Class.forName(staticConfigClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            staticConfigClassList.add(staticConfigClass);
        }
        return staticConfigClassList;
    }

    /**
     * 静态属性空值检查
     * @author hubz
     * @date 2023/5/11 22:32
     *
     * @param staticConfigClassList 静态配置类列表
     **/
    private void checkStaticField(List<Class<?>> staticConfigClassList) {
        for (Class<?> staticConfigClass : staticConfigClassList) {
            List<Field> fields = ClassUtils.getFieldsWithAnnotation(staticConfigClass, StaticPropertyName.class);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new RuntimeException("the StaticPropertyName is not expected to modify a non-static field.");
                }
                // 获取静态属性值的方法
                Object valueObject;
                try {
                    valueObject = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (Objects.isNull(valueObject)) {
                    throw new RuntimeException(String.format("%s -> %s value is auto-set error.",
                            staticConfigClass.getName(), field.getName()));
                }
            }
        }
    }

    /**
     * 将配置文件中的配置注入到静态变量中
     * @author hubz
     * @date 2023/5/11 22:30
     *
     * @param environment 上下文环境变量
     * @param staticConfigClassList 静态配置类列表
     **/
    private void configSetToStaticField(ConfigurableEnvironment environment, List<Class<?>> staticConfigClassList) {
        for (Class<?> staticConfigClass : staticConfigClassList) {
            // 判断是否存在需要额外加载的配置文件(额外配置文件必须在需要加载的静态类上)
            loadExtraPropertiesFile(environment, staticConfigClass);
            // 获取类中属性列表
            List<Field> fields = ClassUtils.getFieldsWithAnnotation(staticConfigClass, StaticPropertyName.class);
            for (Field field : fields) {
                // 获取每个属性的StaticConfigValue注解的值
                StaticPropertyName annotation = field.getAnnotation(StaticPropertyName.class);
                // 判断字段是否为静态字段，如果不是则报错注解加载的字段为非静态字段
                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new RuntimeException("the StaticPropertyName is not expected to modify a non-static field");
                }
                // 不为空说明有这个注解，这个属性是需要注入配置项的
                if (Objects.nonNull(annotation)) {
                    String propertyName = annotation.value();
                    String propertyValue = environment.getProperty(propertyName);
                    if (Objects.isNull(propertyValue) || "".equals(propertyValue)) {
                        // 判断在environment中是否有对应的配置项，没有则报错，终止程序
                        throw new PropertyNotFoundException(String.format("配置项【%s】职位空,请检查配置文件", propertyName));
                    }
                    // 获取属性的类型
                    Class<?> fieldType = field.getType();
                    // 配置项值转换
                    field.setAccessible(true);
                    try {
                        // 给静态属性赋值
                        field.set(null, CastUtils.cast(fieldType, propertyValue));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    /**
     * 加载额外配置文件【可使用 spring.config.import 代替】
     * @param environment 环境变量
     * @param needLoadPropertiesClass 需要加载额外配置的类
     */
    private void loadExtraPropertiesFile(ConfigurableEnvironment environment, Class<?> needLoadPropertiesClass) {
        ExtraPropertyFile annotation = needLoadPropertiesClass.getAnnotation(ExtraPropertyFile.class);
        if (!Objects.isNull(annotation)) {
            String[] extraPropertiesFileArray = annotation.value();
            for (String extraPropertiesFile : extraPropertiesFileArray) {
                try {
                    Resource resource = new ClassPathResource(extraPropertiesFile);
                    Properties props = PropertiesLoaderUtils.loadProperties(resource);
                    PropertiesPropertySource propertySource = new PropertiesPropertySource(extraPropertiesFile, props);
                    environment.getPropertySources().addLast(propertySource);
                } catch (Exception e) {
                    throw new LoadExtraPropertyFileException("load extraPropertiesFile [ " + extraPropertiesFile + " ] error,please check.", e);
                }
            }
        }
    }


}