/**
 * 
 */
package com.zero.core.common.web.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.zero.core.common.annotation.IgnoreLogin;
import com.zero.core.common.vo.RespObj;

/**
 * 权限拦截器
 * 
 * @author luofei
 */
public class CommonInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 跨域
		response.setContentType("application/json; charset=utf-8");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET");
		response.setHeader("Access-Control-Allow-Headers", "content-type");
		response.setHeader("Access-Control-Allow-Origin", "*");
		IgnoreLogin ignoreLogin = null;
		if (handler instanceof HandlerMethod) {
			ignoreLogin = ((HandlerMethod) handler).getMethodAnnotation(IgnoreLogin.class);
		} else {
			return true;
		}
		boolean isLogin = false;
		// 是否忽略登录验证
		if (ignoreLogin != null) {
			isLogin = true;
		} else {
			isLogin = request.getSession().getAttribute("user_context") != null;
		}
		if (isLogin == false) {
			PrintWriter out = response.getWriter();
			out.write(JSON.toJSONString(RespObj.fail("对不起,您还没登录!!!")));
			out.flush();
			out.close();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

}
