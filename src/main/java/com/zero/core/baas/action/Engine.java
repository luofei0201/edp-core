package com.zero.core.baas.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zero.core.baas.Utils;

class ActionDef {
	String name;
	String clazz;

	ActionDef(String name, String clz) {
		this.name = name;
		this.clazz = clz;
	}
}

public class Engine {
	protected static Logger logger = LoggerFactory.getLogger(Engine.class);

	private static String getPackageName(String[] paths) {
		if (paths.length >= 2) {
			String name = "";
			for (int i = 0; i < paths.length - 2; i++) {
				name += ((!"".equals(name) ? "." : "") + paths[i]);
			}
			return name;
		} else
			return null;
	}

	private static String getClassName(String[] paths) {
		if (paths.length >= 2) {
			String clz = paths[paths.length - 2];
			if (!Utils.isEmptyString(clz))
				clz = clz.substring(0, 1).toUpperCase() + clz.substring(1);
			return getRunTimeClassName(clz);
		} else
			return null;
	}

	private static String getActionName(String[] paths) {
		if (paths.length >= 2) {
			return paths[paths.length - 1];
		} else
			return null;
	}

	private static ActionDef getAction(String actionPath) {
		String[] paths = actionPath.split("/");
		if (paths.length >= 2) {
			String name = getActionName(paths);
			String clazzName = getClassName(paths);
			String packageName = getPackageName(paths);
			ActionDef action = new ActionDef(name, !Utils.isEmptyString(packageName) ? (packageName + "." + clazzName) : clazzName);
			return action;
		} else
			return null;
	}

	public static String getRunTimeClassName(String clazz) {
		return clazz + "__do";
	}

	public static JSONObject execAction(String actionPath, JSONObject params) {
		return execAction(actionPath, params, null);
	}

	public static JSONObject execAction(String actionPath, JSONObject params, ActionContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("执行Action[" + actionPath + "]开始......");
		}
		ActionDef action = getAction(actionPath);
		Class<?> ownerClass;
		Method method;
		try {
			ownerClass = Class.forName(action.clazz);
			method = ownerClass.getMethod(action.name, JSONObject.class, ActionContext.class);
			JSONObject ret = (JSONObject) (method.invoke(null, params, context));
			if (logger.isDebugEnabled()) {
				logger.debug("执行Action[" + actionPath + "],返回：" + (null == ret ? "null" : ret.toString()));
			}
			return ret;
		} catch (ClassNotFoundException e) {
			String msg = "Action[" + actionPath + "] Class加载失败，可能原因：Baas模型没有编译，请Baas模型编译后重启服务！";
			logger.error(msg, e);
			throw new ActionException(msg);
		} catch (NoSuchMethodException | SecurityException e) {
			String msg = "Action[" + actionPath + "] Method加载失败，可能原因：Baas模型没有编译，请Baas模型编译后重启服务！";
			logger.error(msg, e);
			throw new ActionException(msg);
		} catch (InvocationTargetException e) {
			String msg = "Action[" + actionPath + "]执行失败，" + e.getTargetException().getMessage();
			logger.error(msg, e);
			throw new ActionException(msg);
		} catch (IllegalAccessException e) {
			String msg = "Action[" + actionPath + "]执行失败，不能执行私有方法！";
			logger.error(msg, e);
			throw new ActionException(msg);
		} catch (IllegalArgumentException e) {
			String msg = "Action[" + actionPath + "]执行失败，参数不匹配！";
			logger.error(msg, e);
			throw new ActionException(msg);
		}
	}

	void main(String[] args) {
		ActionDef ad = getAction("bbbtest/test/test/queryTest");
		System.out.println(ad.clazz);
	}

}
