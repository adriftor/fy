package com.xh.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SpringBeanUtils implements BeanFactoryAware {  
	  
    private static BeanFactory beanFactory;  
  
    // private static ApplicationContext context;  
  
    public void setBeanFactory(BeanFactory factory)  {  
        this.beanFactory = factory;  
    }  
  
    /** 
     * 根据beanName名字取得bean 
     *  
     * @param beanName 
     * @return 
     */  
    public static Object getBean(String beanName) {  
        if (null != beanFactory) {  
            return beanFactory.getBean(beanName);  
        }  
        return null;  
    }  
    public static <T> T getBean(Class<T> c) {  
        if (null != beanFactory) {  
            return (T) beanFactory.getBean(c);  
        }  
        return null;  
    }  
} 