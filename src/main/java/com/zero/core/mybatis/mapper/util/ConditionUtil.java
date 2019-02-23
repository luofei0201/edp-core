package com.zero.core.mybatis.mapper.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zero.core.baas.data.QueryParam;
import com.zero.core.mybatis.mapper.entity.Condition;
import com.zero.core.mybatis.mapper.entity.EntityTable;
import com.zero.core.mybatis.mapper.entity.Example;
import com.zero.core.mybatis.mapper.mapperhelper.EntityHelper;

public class ConditionUtil {

	/**
	 * 构造查询条件
	 * 
	 * @param condition
	 * @param symbol
	 * @param property
	 * @param op
	 * @param value
	 * @return
	 */
	public static Example buildCondition(Example condition, String symbol, String property, String op, Object value) {
		switch (symbol) {
		default:
			break;
		case "and":
			if ("=".equals(op)) {
				
				condition.and().andEqualTo(property, value);
			} else if (">".equals(op)) {
				condition.and().andGreaterThan(property, value);
			} else if (">=".equals(op)) {
				condition.and().andGreaterThanOrEqualTo(property, value);
			} else if ("<".equals(op)) {
				condition.and().andLessThan(property, value);
			} else if ("<=".equals(op)) {
				condition.and().andLessThanOrEqualTo(property, value);
			} else if ("like".equals(op)) {
				condition.and().andLike(property, String.valueOf(value));
			} else if ("isNull".equals(op)) {
				condition.and().andIsNull(property);
			} else if ("<>".equals(op) || "!=".equals(op)) {
				condition.and().andNotEqualTo(property, value);
			}
			break;
		case "or":
			if ("=".equals(op)) {
				condition.or().andEqualTo(property, value);
				//condition.and().orEqualTo(property, value);
			} else if (">".equals(op)) {
				condition.or().andGreaterThan(property, value);
			} else if (">=".equals(op)) {
				condition.or().andGreaterThanOrEqualTo(property, value);
			} else if ("<".equals(op)) {
				condition.or().andLessThan(property, value);
			} else if ("<=".equals(op)) {
				condition.and().andLessThanOrEqualTo(property, value);
			} else if ("like".equals(op)) {
				condition.or().andLike(property, String.valueOf(value));
			} else if ("isNull".equals(op)) {
				condition.or().andIsNull(property);
			} else if ("<>".equals(op) || "!=".equals(op)) {
				condition.or().andNotEqualTo(property, value);
			}
			break;
		}

		return condition;
	}

	/**
	 * 批量构造查询条件
	 * 
	 * @param filter
	 * @param cond
	 * @return
	 */
	public static Example buildCondition(JSONArray filters, Example cond) {
		for (int i = 0; i < filters.size(); i++) {
			JSONObject condition = filters.getJSONObject(i);
			String symbol = condition.getString("symbol");
			String op = condition.getString("op");
			String property = condition.getString("field");
			Object value = condition.get("value");
			ConditionUtil.buildCondition(cond, symbol, property, op, value);
		}
		return cond;
	}

	/**
	 * 批量构造查询条件
	 * 
	 * @param filter
	 * @param cond
	 * @return
	 */
	public static Condition buildOrderBy(JSONArray orderBys, Condition cond) {
		EntityTable entityTable = EntityHelper.getEntityTable(cond.getEntityClass());
		for (int i = 0; i < orderBys.size(); i++) {
			JSONObject o = orderBys.getJSONObject(i);
			String relation = o.getString("relation");
			String type = o.getString("type");
			Example.OrderBy OrderBy = new Example.OrderBy(cond, entityTable.getPropertyMap());
			if ("1".equals(type)) {
				OrderBy.orderBy(relation, "ASC");
			} else {
				OrderBy.orderBy(relation, "DESC");
			}
		}
		return cond;
	}

	/**
	 * 排序
	 * 
	 * @param property
	 * @param sortType
	 *            ASC,DESC
	 * @param cond
	 * @return
	 */
	public static Example buildOrderBy(String property, String sortType, Example example) {
		EntityTable entityTable = EntityHelper.getEntityTable(example.getEntityClass());
		Example.OrderBy OrderBy = new Example.OrderBy(example, entityTable.getPropertyMap());
		OrderBy.orderBy(property, sortType);
		return example;
	}

	/**
	 * 将实体条件构造成sql语句
	 * 
	 * @param filter
	 * @param cond
	 * @return
	 */
	public static String buildFilter(JSONObject params) {
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(params.getString("tableName"));
		JSONObject filter = JSONObject.parseObject(params.getString("filter")).getJSONObject("filter");
		JSONArray filters = filter == null ? new JSONArray() : filter.getJSONArray("filters");
		for (int i = 0; i < filters.size(); i++) {
			JSONObject condition = filters.getJSONObject(i);
			String symbol = condition.getString("symbol");
			String op = condition.getString("op");
			String field = entityTable.getColumn(condition.getString("field"));
			Object value = condition.get("value");
			StringBuffer filterBuf = new StringBuffer();
			switch (symbol) {
			default:
				break;
			case "and":
				if ("=".equals(op)) {
					filterBuf.append(" and " + field + "='" + value + "'");
				} else if (">".equals(op)) {
					filterBuf.append(" and " + field + ">'" + value + "'");
				} else if (">=".equals(op)) {
					filterBuf.append(" and " + field + ">='" + value + "'");
				} else if ("<".equals(op)) {
					filterBuf.append(" and " + field + "<'" + value + "'");
				} else if ("<=".equals(op)) {
					filterBuf.append(" and " + field + "<='" + value + "'");
				} else if ("like".equals(op)) {
					filterBuf.append(" and " + field + "='" + value + "'");
				} else if ("isNull".equals(op)) {
					filterBuf.append(" and " + field + "='" + value + "'");
				} else if ("<>".equals(op) || "!=".equals(op)) {
					filterBuf.append(" and " + field + "<>'" + value + "'");
				}
				break;
			case "or":
				if ("=".equals(op)) {
					filterBuf.append(" or " + field + "='" + value + "'");
				} else if (">".equals(op)) {
					filterBuf.append(" or " + field + ">'" + value + "'");
				} else if (">=".equals(op)) {
					filterBuf.append(" or " + field + ">='" + value + "'");
				} else if ("<".equals(op)) {
					filterBuf.append(" or " + field + "<'" + value + "'");
				} else if ("<=".equals(op)) {
					filterBuf.append(" or " + field + "<='" + value + "'");
				} else if ("like".equals(op)) {
					filterBuf.append(" or " + field + "='" + value + "'");
				} else if ("isNull".equals(op)) {
					filterBuf.append(" or " + field + "='" + value + "'");
				} else if ("<>".equals(op) || "!=".equals(op)) {
					filterBuf.append(" or " + field + "<>'" + value + "'");
				}
				break;
			}
		}
		return "";

	}

	/**
	 * 批量构造查询条件
	 * 
	 * @param filter
	 * @param cond
	 * @return
	 */
	public static QueryParam buildSqlVariables(JSONObject params) {
		System.out.println("======variables====="+params.getString("variables"));
		return (QueryParam)JSONObject.parseObject(params.getString("variables"),QueryParam.class);
		/*
		 * 	String tableName = params.getString("tableName");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Condition cond = new Condition(entityTable.getEntityClass());
		JSONObject filter = JSONObject.parseObject(params.getString("filter")).getJSONObject("filter");
		ConditionUtil.buildCondition(filter == null ? new JSONArray() : filter.getJSONArray("filters"), cond);
		// 树形数据
		boolean isTree = false;
		boolean treeDelayLoad = true;
		String parentField = null, idField = null;
		if (params.containsKey("tree")) {
			isTree = true;
			JSONObject treeOption = params.getJSONObject("tree");
			treeDelayLoad = treeOption.getBoolean("isDelayLoad");
			parentField = treeOption.getString("parentField");
			idField = treeOption.getString("idField");
			if (treeDelayLoad) {
				JSONArray rootConditions = treeOption.containsKey("rootFilter") ? treeOption.getJSONObject("rootFilter").getJSONArray("filters") : null;
				Object parentValue = treeOption.containsKey("parentValue") ? treeOption.get("parentValue") : null;
				if (null != parentValue) {
					cond.and().andEqualTo(parentField, parentValue);

				} else {
					if (null == rootConditions)
						cond.and().andIsNull(parentField);
					else
						ConditionUtil.buildCondition(rootConditions, cond);
				}
			}
		}*/
		//return cond;
	}

	/**
	 * 批量构造查询条件
	 * 
	 * @param filter
	 * @param cond
	 * @return
	 */
	public static Example buildSqlQuery(JSONObject params) {
		String tableName = params.getString("tableName");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Condition cond = new Condition(entityTable.getEntityClass());
		JSONObject filter = JSONObject.parseObject(params.getString("filter")).getJSONObject("filter");
		ConditionUtil.buildCondition(filter == null ? new JSONArray() : filter.getJSONArray("filters"), cond);
		// 树形数据
		boolean isTree = false;
		boolean treeDelayLoad = true;
		String parentField = null, idField = null;
		if (params.containsKey("tree")) {
			isTree = true;
			JSONObject treeOption = params.getJSONObject("tree");
			treeDelayLoad = treeOption.getBoolean("isDelayLoad");
			parentField = treeOption.getString("parentField");
			idField = treeOption.getString("idField");
			if (treeDelayLoad) {
				JSONArray rootConditions = treeOption.containsKey("rootFilter") ? treeOption.getJSONObject("rootFilter").getJSONArray("filters") : null;
				Object parentValue = treeOption.containsKey("parentValue") ? treeOption.get("parentValue") : null;
				if (null != parentValue) {
					cond.and().andEqualTo(parentField, parentValue);

				} else {
					if (null == rootConditions)
						cond.and().andIsNull(parentField);
					else
						ConditionUtil.buildCondition(rootConditions, cond);
				}
			}
		}
		return cond;
	}

}
