package org.hubz.common.event;

import org.hubz.common.annocations.ExtraPropertyFile;
import org.hubz.common.annocations.StaticPropertyName;
import org.hubz.common.exceptions.LoadExtraPropertyFileException;
import org.hubz.common.exceptions.PropertyNotFoundException;
import org.hubz.common.utils.CastUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Properties;

/**
 * @author hubz
 * @date 2023/5/9 22:01
 **/
public class CustomizeApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String staticConfigClass = "static.config.class";
    private static final String STATIC_CONFIG_FIELD_CHECK_ENABLE = "static.config.field.check.enable";


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        // 将配置文件中的配置注入到静态变量中
        configSetToStaticField(environment);
        // todo 新增静态配置检查是否开启，静态配置检查类，包含哪些字段，排除哪些字段
        checkStaticField(environment);
        // todo 默认检查【静态配置检查类】中的所有静态字段
        // todo 包含哪些字段配置项不为空时则只检查这些字段
        // todo 排除哪些字段不为空时则在检查时排除掉这些字段
    }

    /**
     *
     * @author hubz
     * @date 2023/5/11 22:32
     *
     * @param environment 上下文环境变量
     **/
    private void checkStaticField(ConfigurableEnvironment environment) {

    }

    /**
     * 将配置文件中的配置注入到静态变量中
     * @author hubz
     * @date 2023/5/11 22:30
     *
     * @param environment 上下文环境变量
     **/
    private void configSetToStaticField(ConfigurableEnvironment environment) {
        // todo 获取静态配置类数组

        // todo 将静态配置类数组转换成对象列表

        String staticConfigClassNameString = environment.getProperty(staticConfigClass);
        // 校验不为空
        if (Objects.nonNull(staticConfigClassNameString) && !"".equals(staticConfigClassNameString)) {
            String[] staticConfigClassNameArray = staticConfigClassNameString.split(",");
            for (String staticConfigClassName : staticConfigClassNameArray) {
                // 加载类
                Class<?> staticConfigClass;
                try {
                    staticConfigClass = Class.forName(staticConfigClassName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                // 判断是否存在需要额外加载的配置文件(额外配置文件必须在需要加载的静态类上)
                loadExtraPropertiesFile(environment, staticConfigClass);
                // 获取类中属性列表
                Field[] fields = staticConfigClass.getDeclaredFields();
                for (Field field : fields) {
                    // 获取每个属性的StaticConfigValue注解的值
                    StaticPropertyName annotation = field.getAnnotation(StaticPropertyName.class);
                    // 判断字段是否为静态字段，如果不是则报错注解加载的字段为非静态字段
                    if (!(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))) {
                        throw new RuntimeException("the StaticPropertyName is not expected to modify a non-static field or a non-final field");
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