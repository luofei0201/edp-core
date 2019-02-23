package com.zero.core.utils;

import java.util.Properties;

import com.zero.core.utils.PropertiesUtil;

/**
 * 系统配置项读取
 * @author luofei
 */
public class SystemConfig {
	
	
	private static Properties properties;
	
	static{
		properties = new Properties();
		try {
			properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("system-config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取属性值
	 * 
	 * @param key
	 * @return 属性值
	 */
	public static String getValue(String key) {
		if (properties.containsKey(key)) {
			String value = properties.getProperty(key);
			return value;
		} else
			return "";
	}

}
