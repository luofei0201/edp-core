package com.zero.core.common.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface IBaasSqlQuerySerive {
	
	/**
	 * 自定义bass的query方法
	 * @param example
	 * @return
	 */
	public List<?> baasSqlQuery(JSONObject params);

}
