/**
 * 南京青牛通讯技术有限公司
 * 日期：$$Date: 2016-07-30 15:16:57 +0800 (周六, 30 七月 2016) $$
 * 作者：$$Author: zhoulin $$
 * 版本：$$Rev: 125311 $$
 * 版权：All rights reserved.
 */
package com.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.mongodb.DBObject;

import org.apache.commons.lang.StringUtils;

public class BeanUtil extends StringUtils {
	/**
	 * 把DBObject转换成bean对象
	 * 
	 * @param dbObject
	 * @param bean
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> T dbObject2Bean(DBObject dbObject, T bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bean == null) {
			return null;
		}
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			String varName = field.getName();
			Object object = dbObject.get(varName);
			if (object != null) {
				BeanUtils.setProperty(bean, varName, object);
			}
		}
		return bean;
	}
	
	public static Map<String, Object> transBean2Map(Object obj) {  
		  
	    if (obj == null) {  
	        return null;  
	    }  
	    Map<String, Object> map = new HashMap<String, Object>();  
	    try {  
	        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
	        PropertyDescriptor[] propertyDescriptors = beanInfo  
	                .getPropertyDescriptors();  
	        for (PropertyDescriptor property : propertyDescriptors) {  
	            String key = property.getName();  
	  
	            // 过滤class属性  
	            if (!key.equals("class")) {  
	                // 得到property对应的getter方法  
	                Method getter = property.getReadMethod();  
	                Object value = getter.invoke(obj);  
	  
	                map.put(key, value);  
	            }  
	  
	        }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	  
	    return map;  
	  
	}  
}
