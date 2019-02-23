package com.zero.core.common.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * Bass服务接口
 * @author luofei
 */
public interface ICommonBaasService {
	/**
	 * baas通用保存
	 */
	public JSONObject baasSave(JSONObject params);
	
	/**
	 * baas通用查询接口
	 * @param t
	 * @return
	 */
	public JSONObject baasQuery(JSONObject params);
	
	/**
	 * baas通用查询接口
	 * @param t
	 * @return
	 */
	public JSONObject baasSqlQuery(JSONObject params);
	
	/**
	 * 判断是否重复
	 * @param params
	 * @return
	 */
	public boolean duplicate(JSONObject params);
	/**
	 *  baas通用查询接口
	 * @param params
	 * @param dataList
	 * @return
	 */
	public JSONObject baasSqlQuery(JSONObject params,List<?> dataList);
	
	
	
}
