package com.zero.core.common.adapter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zero.core.baas.BaasException;
import com.zero.core.baas.CRUD;
import com.zero.core.baas.data.DataUtils;
import com.zero.core.baas.data.Table;
import com.zero.core.baas.data.Transform;
import com.zero.core.common.context.SpringContextUtil;
import com.zero.core.mybatis.mapper.common.CommMapper;
import com.zero.core.mybatis.mapper.entity.EntityTable;
import com.zero.core.mybatis.mapper.entity.Example;
import com.zero.core.mybatis.mapper.mapperhelper.EntityHelper;
import com.zero.core.mybatis.mapper.util.ConditionUtil;
import com.zero.core.mybatis.mapper.util.StringUtil;

@Service
public class BaasServiceAdapter implements IBaasServiceAdapter {

	protected static Logger logger = LoggerFactory.getLogger(BaasException.class);

	@Override
	public JSONObject save(JSONObject params) {
		JSONArray tables = params.getJSONArray("tables");
		try {
			Table table = null;
			if (tables != null && tables.size() > 0) {
				JSONObject tableParam = new JSONObject();
				for (Object jsonTable : tables) {
					table = Transform.jsonToTable((JSONObject) jsonTable);
					tableParam.put("tableName", table.getTableName());
					CRUD.saveData(table, null, this.getMapper(tableParam), params);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject query(JSONObject params) {
		try {
			return CRUD.queryData(params, this.getMapper(params));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject sqlQuery(JSONObject params) {
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		String tableName = params.getString("tableName");
		JSONObject columns = params.getJSONObject("columns");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Table table = null;
		try {
			if (limit != -1) {
				PageHelper.startPage(offset/limit+1, limit);
			}
			
			List<?> list = this.getMapper(params).baasSqlQuery(ConditionUtil.buildSqlVariables(params));
			PageInfo<?> pageInfo = new PageInfo(list);
			table = Transform.createTableByResultSet(pageInfo.getList(), entityTable.getEntityClass(),columns);
			table.setTotal(Integer.valueOf(String.valueOf(pageInfo.getTotal())));
			table.setOffset(offset);
			table.setTableName(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataUtils.buildReponseData(params, table);
	}

	private CommMapper<?> getMapper(JSONObject params) {
		String tableName = params.getString("tableName");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		return (CommMapper<?>) SpringContextUtil.getBean(StringUtil.getDaoName(entityTable.getEntityClass()));
	}

	@Override
	public boolean duplicate(JSONObject params) {
		String tableName = params.getString("tableName");
		JSONArray filters =params.getJSONObject("filter").getJSONArray("filters");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Class<?> tableClass = entityTable.getEntityClass();
		CommMapper mapper = (CommMapper<?>) SpringContextUtil.getBean(StringUtil.getDaoName(entityTable.getEntityClass()));
		Integer count = 0;
		Example example = new Example(tableClass);
		String IdColumn = params.getString("IdColumn");
		try {
			/*
			for (String key : params.keySet()) {
				if (IdColumn.equals(key)) {
					System.out.println("============="+key+"="+ params.get(key));
					example.and().andEqualTo(key, params.get(key));
				}
			}*/
			if (!StringUtil.isEmpty(params.getString(IdColumn))) {
				example.and().andNotEqualTo(IdColumn, params.get(IdColumn));
			}
			ConditionUtil.buildCondition(filters, example);
			List<?> list = mapper.selectByExample(example);
			count=list.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count > 0 ? true : false;
	}

	@Override
	public JSONObject sqlQuery(JSONObject params, List<?> dataList) {
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		String tableName = params.getString("tableName");
		JSONObject columns = params.getJSONObject("columns");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Table table = null;
		try {
			/*
			if (limit != -1) {
				PageHelper.startPage(offset, limit);
			}*/
			//List<?> list = this.getMapper(params).baasSqlQuery(ConditionUtil.buildSqlVariables(params));
			PageInfo<?> pageInfo = new PageInfo(dataList);
			table = Transform.createTableByResultSet(pageInfo.getList(), entityTable.getEntityClass(),columns);
			table.setTotal(Integer.valueOf(String.valueOf(pageInfo.getTotal())));
			table.setOffset(offset);
			table.setTableName(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataUtils.buildReponseData(params, table);
	}
}
