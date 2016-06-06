package com.xh.util.db;


import java.lang.reflect.Field;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
/**
 * 动态加载XML扩展
 * @author adriftor
 *
 */
public class SqlSessionFactoryBeanExt extends SqlSessionFactoryBean {

   private SqlSessionFactory sqlSessionFactory;

   public void setValue(String name, Object value) {
       try {
           Field field = SqlSessionFactoryBean.class.getDeclaredField(name);
           field.setAccessible(true);
           field.set(this, value);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public SqlSessionFactory getObject() throws Exception {
       if (this.sqlSessionFactory == null) {
           sqlSessionFactory = buildSqlSessionFactory();
           setValue("sqlSessionFactory", sqlSessionFactory);
       }
       return this.sqlSessionFactory;
   }

   public void afterPropertiesSet() throws Exception {
   }

}