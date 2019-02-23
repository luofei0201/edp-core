package com.zero.core.common.log.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zero.core.baas.BaasException;
import com.zero.core.common.annotation.Logger;
import com.zero.core.common.log.model.UserOperLog;
import com.zero.core.common.log.service.IUserOperLoginService;

/**
 * 日志拦截器
 * 
 * @author luofei
 */
@Aspect
@Component
public class LogInterceptor {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(BaasException.class);

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private IUserOperLoginService userOperLoginService;

	/**
	 * 定义一个切入点
	 */
	@Pointcut("execution(** *..controller.*.*(..))")
	private void pointCutMethod() {
	}

	@Before("pointCutMethod()")
	public void recordTime(JoinPoint pjp) {
		request.setAttribute("startTime",System.currentTimeMillis());
	}

	@After("pointCutMethod()")
	public void recordLog(JoinPoint pjp) throws Throwable {
		Signature signature = pjp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		Map paramMap = request.getParameterMap();
		JSONObject params =null;
		if (paramMap.size() > 0) {
			params = (JSONObject) JSONObject.toJSON(paramMap);
		}else{
			params = new JSONObject();
		}
		Class[] parameterTypes = new Class[pjp.getArgs().length];
		String[] parameterNames = methodSignature.getParameterNames();
		Object[] args = pjp.getArgs();
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				parameterTypes[i] = args[i].getClass();
				if (!"HttpServletRequest".equals(args[i].getClass().getSimpleName()) || !"HttpServletResponse".equals(args[i].getClass().getSimpleName())) {
					params.put(parameterNames[i], args[i]);

				}
			} else {
				parameterTypes[i] = null;
			}
		}

		// 获取实际方法对象
		String methodName = targetMethod.getDeclaringClass().getName() + "." + targetMethod.getName();
		Method realMethod = null;
		try {
			realMethod = pjp.getTarget().getClass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());
		} catch (Exception e) {
			realMethod = pjp.getTarget().getClass().getSuperclass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());
		}
		if (realMethod.isAnnotationPresent(Logger.class)) {
			saveUserLog(params, buildUserContext(request), realMethod.getAnnotation(Logger.class).operName(), methodName, (Long) request.getAttribute("startTime"));
		}
	}

	private void saveUserLog(JSONObject params, JSONObject context, String operName, String methodName, Long startDate) {
		UserOperLog operLog = new UserOperLog();
		operLog.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
		operLog.setIpAddr(context.getString("ipAddr"));
		operLog.setOperOrgId(context.getString("orgId"));
		operLog.setMethodName(methodName);
		operLog.setOperOrgName(context.getString("orgName"));
		operLog.setOperUserId(context.getString("userId"));
		operLog.setOperUserName(context.getString("userName"));
		operLog.setReferer(context.getString("referer"));
		operLog.setOperName(operName);
		operLog.setOperParams(params.toJSONString());
		operLog.setEndTime(System.currentTimeMillis());
		operLog.setStartTime(startDate);
		operLog.setTimeCost(operLog.getEndTime()-operLog.getStartTime());
		userOperLoginService.saveLog(operLog);
	}

	private static JSONObject buildUserContext(HttpServletRequest request) throws Exception {
		Object bean = request.getSession().getAttribute("user_context");
		if (bean == null) {
			return new JSONObject();
		}
		Class userContext = bean.getClass();
		JSONObject context = new JSONObject();
		Field filed = null;
		// ip地址
		context.put("ipAddr", request.getRemoteAddr());
		context.put("referer", request.getHeader("referer"));
		filed = userContext.getDeclaredField("orgId");
		filed.setAccessible(true);
		context.put("orgId", filed.get(bean));
		filed = userContext.getDeclaredField("orgName");
		filed.setAccessible(true);
		context.put("orgName", filed.get(bean));
		filed = userContext.getDeclaredField("id");
		filed.setAccessible(true);
		context.put("userId", filed.get(bean));
		filed = userContext.getDeclaredField("name");
		filed.setAccessible(true);
		context.put("userName", filed.get(bean));
		return context;
	}
}
