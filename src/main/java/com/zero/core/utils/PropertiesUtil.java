package com.zero.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * 属性文件工具类
 * 
 * @author luofei
 * 
 */
public class PropertiesUtil {
	/**
	 * 
	 */
	private static Properties properties;
	/**
	 * 属性输入文件流
	 */
	private  static InputStream inputFile;
	
	/**
	 * 属性文件流
	 */
	private  static FileOutputStream outFile;
	
	static{
		inputFile=PropertiesUtil.class.getClassLoader().getResourceAsStream("system-config.properties");
		URL url = PropertiesUtil.class.getClassLoader().getResource("system-config.properties");
		File file = new File(url.getFile());
		properties = new Properties();
		try {
			outFile = new FileOutputStream(file);
			properties.load(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化PropertiesUtil类
	 */
	public PropertiesUtil() {
		properties = new Properties();
	}

	/**
	 * 初始化PropertiesUtil类，
	 * 
	 * @param insFile
	 *            资源文件
	 */
	public  PropertiesUtil(InputStream insFile) {
		properties = new Properties();
		try {
			inputFile = insFile;
			properties.load(insFile);
			inputFile.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
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

	/**
	 * 通过文件+key获取属性值
	 * 
	 * @param insFile
	 *            属性文件
	 * @param key
	 *            键值
	 * @return 属性值
	 */
	public static String getValue(InputStream insFile, String key) {
		try {
			String value = "";
			inputFile = insFile;
			properties.load(inputFile);
			inputFile.close();
			if (properties.containsKey(key)) {
				value = properties.getProperty(key);
				return value;
			} else
				return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * 清空属性文件
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * 修改属性文件某个key的值
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		properties.setProperty(key, value);
	}
	
	/**
	 * 修改属性文件某个key的值
	 * 
	 * @param key
	 * @param value
	 */
	public static void store(String key,String value) {
		try {
		    properties.setProperty(key, value);
			properties.store(outFile, "The New properties file");
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		store("uploadPath","d://upload");
		
	}
	
}
