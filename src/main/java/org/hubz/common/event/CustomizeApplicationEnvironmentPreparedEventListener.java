package org.hubz.common.event;

import org.hubz.common.annocations.StaticPropertyName;
import org.hubz.common.exceptions.PropertyNotFoundException;
import org.hubz.common.utils.SpringContextUtil;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author hubz
 * @date 2023/5/9 22:01
 **/
public class CustomizeApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String staticConfigClass = "static.config.class";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        String staticConfigClassNameString = environment.getProperty(staticConfigClass);
        // 校验不为空
        if (Objects.nonNull(staticConfigClassNameString) && !"".equals(staticConfigClassNameString)) {
            String[] staticConfigClassNameArray = staticConfigClassNameString.split(",");
            for (String staticConfigClassName : staticConfigClassNameArray) {
                // 加载类
                Object staticConfigClassObj = SpringContextUtil.getBean(staticConfigClassName);
                //todo 空指针问题待处理
                //if(Objects.isNull(staticConfigClassObj)) {
                //
                //}
                // 获取类中属性列表
                Field[] fields = staticConfigClassObj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    // 获取每个属性的StaticConfigValue注解的值
                    StaticPropertyName annotation = field.getAnnotation(StaticPropertyName.class);
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
                        field.set(staticConfigClassObj, fieldType.cast(propertyValue));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}