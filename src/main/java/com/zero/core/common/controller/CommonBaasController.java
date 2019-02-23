package com.zero.core.common.controller;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.zero.core.baas.data.DataUtils;
import com.zero.core.common.service.ICommonBaasService;

/**
 * 通用Baas控制器
 * 
 * @author luofei
 */
@RestController
@RequestMapping("/common/baas")
public class CommonBaasController {

	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";

	/**
	 * baas服务
	 */
	@Autowired
	protected ICommonBaasService commonBaasService;

	/**
	 * 列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/query", produces = "application/json; charset=UTF-8")
	public JSONObject query(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = (JSONObject) getParams(request);
		DataUtils.writeJsonToResponse(response, commonBaasService.baasQuery(params));
		return null;
	}

	/**
	 * 列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/save", produces = "application/json; charset=UTF-8")
	public JSONObject save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = (JSONObject) getParams(request);
		commonBaasService.baasSave(params);
		// DataUtils.writeJsonToResponse(response,
		// commonBaasService.baasSave(params));
		return null;
	}

	@RequestMapping(value = "/sqlQuery", produces = "application/json; charset=UTF-8")
	public JSONObject sqlQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = (JSONObject) getParams(request);
		DataUtils.writeJsonToResponse(response, commonBaasService.baasSqlQuery(params));
		return null;
	}

	/**
	 * 判断某个对象是否重复
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/duplicate", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public boolean duplicate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject params = (JSONObject) getParams(request);
		return commonBaasService.duplicate(params);
	}

	protected JSONObject getParams(HttpServletRequest request) throws Exception {
		String method = request.getMethod();
		if (isRequestMultipart(request) || (!METHOD_GET.equalsIgnoreCase(method) && !isJson(request))) {
			JSONObject params = new JSONObject();
			return params;
		} else if (METHOD_POST.equalsIgnoreCase(method) && isJson(request))
			return getPostParams(request);
		else {
			JSONObject params = new JSONObject();
			for (Object k : request.getParameterMap().keySet()) {
				String key = (String) k;
				params.put(key, request.getParameter(key));
			}
			params.put("context", buildContext(request));
			return params;
		}
	}

	private static JSONObject buildContext(HttpServletRequest request) throws Exception {
		JSONObject context = new JSONObject();
		Object bean = request.getSession().getAttribute("user_context");
		if(bean==null){
			return context;
		}
		Class userContext = bean.getClass();
		Field filed = null;
		// ip地址
		context.put("ipAddr", request.getRemoteAddr());
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

	private static String getRequestContentType(HttpServletRequest request) {
		return request.getContentType();
	}

	private static boolean isRequestMultipart(String type) {
		return null != type && -1 < type.indexOf(MULTIPART_CONTENT_TYPE);
	}

	private static boolean isJson(String type) {
		return null != type && -1 < type.indexOf(JSON_CONTENT_TYPE);
	}

	private static boolean isJson(HttpServletRequest request) {
		return isJson(getRequestContentType(request));
	}

	private static boolean isRequestMultipart(HttpServletRequest request) {
		return isRequestMultipart(getRequestContentType(request));
	}

	private JSONObject getPostParams(HttpServletRequest request) throws Exception {
		final int BUFFER_SIZE = 8 * 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ServletInputStream inputStream = request.getInputStream();
		int bLen = 0;
		while ((bLen = inputStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bLen);
		}
		String bodyData = new String(baos.toByteArray(), "UTF-8");
		JSONObject jo = JSONObject.parseObject(bodyData);
		jo.put("context", buildContext(request));
		return jo;
	}

}
