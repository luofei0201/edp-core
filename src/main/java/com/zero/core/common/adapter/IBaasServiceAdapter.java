package com.zero.core.common.adapter;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * baas适配器
 * 
 * @author luofei
 * @param <T>
 */
public interface IBaasServiceAdapter {
	
	public final String QUERY_MASTER_VAR_NAME = "_sys_master_value_";
	public final String QUERY_TREE_PARENT_VAR_NAME = "_sys_tree_parent_value_";

	/**
	 * 通用保存
	 */
	public JSONObject save(JSONObject params);

	/**
	 * 通用查询接口
	 * 
	 * @param t
	 * @return
	 */
	public JSONObject query(JSONObject params);
	
	/**
	 * 通用查询接口
	 * 
	 * @param t
	 * @return
	 */
	public JSONObject sqlQuery(JSONObject params);
	
	/**
	 * 判断是否重复
	 * @param params
	 * @return
	 */
	public boolean duplicate(JSONObject params);
	
	
	/**
	 * 通用查询接口
	 * @param params
	 * @param dataList
	 * @return
	 */
	public JSONObject sqlQuery(JSONObject params,List<?> dataList);

	

}
