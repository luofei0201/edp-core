/**
 * 
 */
package com.zero.core.common.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.zero.core.common.adapter.IBaasServiceAdapter;

/**
 * 通用baas服务
 * 
 * @author luofei
 */
@Service
@Transactional
public class CommonBaasService implements ICommonBaasService {
	@Autowired
	private IBaasServiceAdapter baasServiceAdapter;

	@Override
	public JSONObject baasSave(JSONObject params) {
		return baasServiceAdapter.save(params);
	}

	@Override
	public JSONObject baasQuery(JSONObject params) {
		return baasServiceAdapter.query(params);
	}

	@Override
	public JSONObject baasSqlQuery(JSONObject params) {
		return baasServiceAdapter.sqlQuery(params);
	}

	@Override
	public boolean duplicate(JSONObject params) {
		// TODO 自动生成的方法存根
		return baasServiceAdapter.duplicate(params);
	}

	@Override
	public JSONObject baasSqlQuery(JSONObject params, List<?> dataList) {
		return baasServiceAdapter.sqlQuery(params, dataList);
	}

}
