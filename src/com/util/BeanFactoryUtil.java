/**
 * FileName: BeanFactoryUtil.java
 */
package com.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * @author zhangxuhua
 */
public class BeanFactoryUtil implements ServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(BeanFactoryUtil.class);

	private static ApplicationContext context;

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
	}

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		logger.info("初始化BeanFactory....");
		context = WebApplicationContextUtils.getWebApplicationContext(event
				.getServletContext());
		logger.info("初始化BeanFactory....OK.");
	}

	/**
	 * 获取Spring中的Bean
	 * 
	 * @param beanName
	 *            Bean的名称
	 * @return 如果成功则反回Bean对象，如果失败则抛出异常.
	 */
	public static Object getBean(Class<?> clazz) throws Exception
	{
		if (context == null)
		{
			logger.warn("ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
			throw new Exception(
					"ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
		}
		try
		{
			return context.getBean(clazz);
		}
		catch (BeansException exp)
		{
			if (exp instanceof NoSuchBeanDefinitionException)
			{
				logger.debug("bean[" + clazz.getName() + "]尚未装载到容器中！");
			}
			else
			{
				logger.error("读取[" + clazz.getName() + "]失败！", exp);
			}
			throw new Exception("读取[" + clazz.getName() + "]失败！",
					exp);
		}
	}

	/**
	 * 获取Spring中的Bean
	 * 
	 * @param beanName
	 *            Bean的名称
	 * @return 如果成功则反回Bean对象，如果失败则抛出异常.
	 */
	public static Object getBean(String beanName) throws Exception
	{
		if (context == null)
		{
			logger.warn("ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
			throw new Exception(
					"ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
		}
		try
		{
			return context.getBean(beanName);
		}
		catch (BeansException exp)
		{
			if (exp instanceof NoSuchBeanDefinitionException)
			{
				logger.debug("bean[" + beanName + "]尚未装载到容器中！");
			}
			else
			{
				logger.error("读取[" + beanName + "]失败！", exp);
			}
			throw new Exception("读取[" + beanName + "]失败！", exp);
		}
	}
	

	/**
	 * 获取Spring中的Bean
	 * 
	 * @param beanName
	 *            Bean的名称
	 * @return 如果成功则反回Bean对象，如果失败则抛出异常.
	 */
	public static Object getExternalBean(String beanName) throws Exception
	{
		if (context == null)
		{
			logger.warn("ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
			throw new Exception(
					"ApplicationContext 没有初始化！您无法获得业务处理对象，系统无法正常运行");
		}
		return context.getBean(beanName);
	}
}
