/**
 * 
 */
package com.external.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ObjectFactory {
	private static Logger logger = Logger.getLogger(ObjectFactory.class);

	/**
	 * Initialization on Demand Holder，使用内部类，延迟加载，且线程安全
	 * 
	 */
	private static class PropertiesHolder {
		private static Properties properties;

		private static Properties getInstance() {
			if (properties == null) {
				properties = new Properties();
			}
			return properties;
		}

		public static Properties getUrlProps() {
			properties = getInstance();
			InputStream is = null;
			try {
				is = PropertiesHolder.class.getResourceAsStream("/url.properties");
				properties.load(is);
			} catch (IOException e) {
				logger.error("load url properties failed.", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			return properties;
		}

		public static Properties getSetsailProps() {
			properties = getInstance();
			InputStream is = null;
			try {
				is = PropertiesHolder.class.getResourceAsStream("/setsail.properties");
				properties.load(new InputStreamReader(is, "UTF-8"));
			} catch (IOException e) {
				logger.error("load setsail properties failed.", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			return properties;
		}
		

		public static Properties getMessageProps() {
			properties = getInstance();
			InputStream is = null;
			try {
				is = PropertiesHolder.class.getResourceAsStream("/message.properties");
				properties.load(new InputStreamReader(is, "UTF-8"));
			} catch (IOException e) {
				logger.error("load setsail properties failed.", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
			return properties;
		}

	}

	public static Properties getUrlProps() {
		return PropertiesHolder.getUrlProps();
	}

	public static Properties getSetsailProps() {
		return PropertiesHolder.getSetsailProps();
	}

	public static Properties getMessageProps() {
		return PropertiesHolder.getMessageProps();
	}
}
