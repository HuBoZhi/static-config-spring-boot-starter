package org.hubz.common.utils;

import org.springframework.context.ApplicationContext;

public class SpringContextUtil{

    private static ApplicationContext applicationContext;
    

    public static void setApplicationContext(ApplicationContext applicationContext){
        if(null==SpringContextUtil.applicationContext)
            SpringContextUtil.applicationContext=applicationContext;
    }
    
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }


    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);

    }

}