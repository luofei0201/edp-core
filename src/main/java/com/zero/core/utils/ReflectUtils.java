package com.zero.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtils {
	/**
	 * 获得超类的参数类型，取第一个参数类型
	 * 
	 * @param <T>
	 *            类型参数
	 * @param clazz
	 *            超类类型
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Class<T> getClassGenricType(final Class clazz) {
		return getClassGenricType(clazz, 0);
	}

	/**
	 * 根据索引获得超类的参数类型
	 * 
	 * @param clazz
	 *            超类类型
	 * @param index
	 *            索引
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassGenricType(final Class clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

	/**
	 * java反射bean的get方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Method getIdGetMethod(Class objectClass) {
		String fieldName = "";
		Field[] field = objectClass.getDeclaredFields();
		for (Field f : field) {
			javax.persistence.Id t = (javax.persistence.Id) f.getAnnotation(javax.persistence.Id.class);
			fieldName=f.getName();
			if (t != null) {
				break;
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		try {
			return objectClass.getMethod(sb.toString());
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * java反射bean的set方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Method getIdSetMethod(Class objectClass) {
		try {
			Class[] parameterTypes = new Class[1];
			Field[] field = objectClass.getDeclaredFields();
			String fieldName = "";
			for (Field f : field) {
				javax.persistence.Id t = (javax.persistence.Id) f.getAnnotation(javax.persistence.Id.class);
				fieldName=f.getName();
				parameterTypes[0] = f.getType();
				if (t != null) {
					break;
				}
			}
			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			Method method = objectClass.getMethod(sb.toString(), parameterTypes);
			return method;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static String getIdFieldName(Class objectClass) {
		Field[] field = objectClass.getDeclaredFields();
		String fieldName = "";
		for (Field f : field) {
			javax.persistence.Id t = (javax.persistence.Id) f.getAnnotation(javax.persistence.Id.class);
			fieldName=f.getName();
			if (t != null) {
				break;
			}
		}
		return fieldName;
	}


	public static void main(String[] args) {
	}

}
