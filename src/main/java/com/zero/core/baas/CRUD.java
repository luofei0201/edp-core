package com.zero.core.baas;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletInputStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.zero.core.baas.data.DataType;
import com.zero.core.baas.data.DataUtils;
import com.zero.core.baas.data.Row;
import com.zero.core.baas.data.RowState;
import com.zero.core.baas.data.Table;
import com.zero.core.baas.data.Transform;
import com.zero.core.baas.data.sql.SQLStruct;
import com.zero.core.common.annotation.BaasLogger;
import com.zero.core.common.context.SpringContextUtil;
import com.zero.core.common.log.model.UserOperLog;
import com.zero.core.mybatis.mapper.common.CommMapper;
import com.zero.core.mybatis.mapper.entity.Condition;
import com.zero.core.mybatis.mapper.entity.EntityTable;
import com.zero.core.mybatis.mapper.mapperhelper.EntityHelper;
import com.zero.core.mybatis.mapper.util.ConditionUtil;
import com.zero.core.mybatis.mapper.util.StringUtil;

/**
 * 通用增删改查,
 * 
 * @author luofei
 */
public class CRUD {

	public JSONObject save(JSONObject params, Connection conn) throws SQLException, NamingException, ParseException {
		JSONArray tables = params.getJSONArray("tables");
		JSONObject permissions = params.containsKey("permissions") ? params.getJSONObject("permissions") : null;
		try {
			conn.setAutoCommit(false);
			if (tables != null && tables.size() > 0) {
				for (Object jsonTable : tables) {
					Table table = Transform.jsonToTable((JSONObject) jsonTable);
					String tableName = table.getTableName();
					if (canSave(permissions, tableName))
						DataUtils.saveData(conn, table, getSaveColumnsByPermissions(permissions, tableName));
				}
			}
			conn.commit();
			return null;
		} finally {
			conn.close();
		}
	}

	/**
	 * 通用查询
	 * 
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public JSONObject query(JSONObject params, Connection conn) throws SQLException, NamingException {
		// 获取参数
		String db = params.getString("db");
		String tableName = params.getString("tableName");
		Object columns = params.get("columns");
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		String orderBy = params.getString("orderBy");
		String condition = params.getString("condition");
		String filter = params.getString("filter");
		JSONObject variables = getVariables(params);
		List<String> filters = new ArrayList<String>();
		if (!Utils.isEmptyString(condition)) {
			filters.add(condition);
		}
		if (!Utils.isEmptyString(filter)) {
			filters.add(filter);
		}
		// 处理主从
		if (params.containsKey("master")) {
			JSONObject master = params.getJSONObject("master");
			if (master.containsKey("field")) {
				filters.add(master.getString("field") + " = :" + QUERY_MASTER_VAR_NAME);
				variables.put(QUERY_MASTER_VAR_NAME, master.get("value"));
			}
		}
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
				// 分级加载增加根据父的过滤条件
				String rootFilter = treeOption.containsKey("rootFilter") ? treeOption.getString("rootFilter") : null;
				Object parentValue = treeOption.containsKey("parentValue") ? treeOption.get("parentValue") : null;
				if (null != parentValue) {
					filters.add(parentField + " = :" + QUERY_TREE_PARENT_VAR_NAME);
					variables.put(QUERY_TREE_PARENT_VAR_NAME, parentValue);
				} else {
					if (null == rootFilter)
						filters.add(parentField + " is null");// 默认根条件就是 parent
																// is null
					else
						filters.add(rootFilter);
				}
			}
		}

		// limit处理，-1取全部
		if (null != limit && limit == -1)
			limit = null;

		Table table = null;

		try {
			String format = "SELECT %s FROM %s %s %s ";

			String where = (filters != null && filters.size() > 0) ? " WHERE " + DataUtils.arrayJoin(filters.toArray(), "(%s)", " AND ") : "";
			orderBy = !Utils.isEmptyString(orderBy) ? " ORDER BY " + orderBy : "";

			String sql = String.format(format, "*", tableName, where, orderBy);
			// 进行名字变量转换
			SQLStruct sqlStruct = new SQLStruct(sql);
			table = DataUtils.queryData(conn, sqlStruct.getSQL(), sqlStruct.getBinds(variables), columns, offset, limit);
			if (offset != null && offset.equals(0)) {
				// where部分进行名字变量转换
				SQLStruct sqlWhereStruct = new SQLStruct(where);
				String sqlTotal = String.format(format, "COUNT(*)", tableName, sqlWhereStruct.getSQL(), "");
				Object total = DataUtils.getValueBySQL(conn, sqlTotal, sqlWhereStruct.getBinds(variables));
				table.setTotal(Integer.parseInt(total.toString()));
			}

			if (isTree && !treeDelayLoad) {
				table.setIDColumn(idField);
				return Transform.tableToTreeJson(table, parentField);
			} else
				return Transform.tableToJson(table);
		} finally {
			conn.close();
		}
	}

	/**
	 * 查询 sqlQuery
	 * 
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public JSONObject sqlQuery(JSONObject params, Connection conn) throws SQLException, NamingException {
		// 获取参数
		String db = params.getString("db");
		String sql = params.getString("sql");
		String countSql = params.getString("countSql");
		Object columns = params.get("columns");
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		if (Utils.isEmptyString(params.getString("filter")))
			params.put("filter", "1=1");
		String orderBy = params.getString("orderBy");
		if (Utils.isNotEmptyString(orderBy))
			params.put("orderBy", "order by " + orderBy);
		JSONObject variables = getVariables(params);
		Table table = null;
		try {
			// 进行名字变量转换
			SQLStruct sqlStruct = new SQLStruct(sql);
			table = DataUtils.queryData(conn, sqlStruct.getSQL(variables), sqlStruct.getBinds(variables), columns, offset, limit);
			if (offset != null && offset.equals(0)) {
				// where部分进行名字变量转换
				SQLStruct countSqlStruct = new SQLStruct(countSql);
				Object total = DataUtils.getValueBySQL(conn, countSqlStruct.getSQL(variables), countSqlStruct.getBinds(variables));
				table.setTotal(Integer.parseInt(total.toString()));
			}

			return Transform.tableToJson(table);
		} finally {
			conn.close();
		}
	}

	private boolean canSave(JSONObject permissions, String tableName) {
		return null == permissions || (null != permissions && permissions.containsKey(tableName));
	}

	private String getSaveColumnsByPermissions(JSONObject permissions, String tableName) {
		String ret = null;
		if (null != permissions) {
			if (permissions.containsKey(tableName))
				ret = permissions.getString(tableName);
		}
		return ret;
	}

	public final String QUERY_MASTER_VAR_NAME = "_sys_master_value_";
	public final String QUERY_TREE_PARENT_VAR_NAME = "_sys_tree_parent_value_";
	public final String VARIABLE_FLAG = "var-";

	// var-开头的参数认为也是变量,variables优先级高于var-
	private JSONObject getVariables(JSONObject params) {
		JSONObject variables = params.getJSONObject("variables");
		if (null == variables)
			variables = new JSONObject();
		for (String key : params.keySet()) {
			if (key.startsWith(VARIABLE_FLAG)) {
				String varName = key.substring(VARIABLE_FLAG.length());
				if (!variables.containsKey(varName))
					variables.put(varName, params.get(key));
			} else if (!"db".equals(key) && !"sql".equals(key) && !"countSql".equals(key) && !variables.containsKey(key)) {
				variables.put(key, params.get(key));
			}
		}
		return variables;
	}

	public static JSONObject getParams(ServletInputStream inputStream) throws Exception {
		final int BUFFER_SIZE = 8 * 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bLen = 0;
		while ((bLen = inputStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bLen);
		}
		String bodyData = new String(baos.toByteArray(), "UTF-8");
		// System.out.println(bodyData);
		JSONObject jo = JSONObject.parseObject(bodyData);

		return jo;
	}

	public static void initActionParams(JSONObject privateParams, JSONObject publicParams, JSONObject params) {
		if (privateParams != null) {
			for (String key : privateParams.keySet()) {
				params.put(key, privateParams.get(key));
			}
		}
		if (publicParams != null) {
			for (String key : publicParams.keySet()) {
				if (!params.containsKey(key))
					params.put(key, publicParams.get(key));
			}
		}
	}

	// ==============================================================================================================================
	/**
	 * 通用查询
	 * 
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws ClassNotFoundException
	 */
	public static JSONObject queryData(JSONObject params, CommMapper<?> commMapper) throws Exception {
		Date startDate = new Date();
		// 获取参数
		String tableName = params.getString("tableName");
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		String orderBy = params.getString("orderBy");
		JSONObject columns = params.getJSONObject("columns");
		JSONObject filter = JSONObject.parseObject(params.getString("filter")).getJSONObject("filter");
		EntityTable entityTable = EntityHelper.getEntityBySimpleName(tableName);
		Condition cond = new Condition(entityTable.getEntityClass());
		// 构造查询条件
		ConditionUtil.buildCondition(filter == null ? new JSONArray() : filter.getJSONArray("filters"), cond);
		// 处理主从
		if (params.containsKey("master")) {
			JSONObject master = params.getJSONObject("master");
			if (master.containsKey("field")) {
				// filters.add(master.getString("field") + " = :" +
				// QUERY_MASTER_VAR_NAME);
				// variables.put(QUERY_MASTER_VAR_NAME, master.get("value"));
				cond.and().andEqualTo(master.getString("field"), master.get("value"));
			}
		}
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
				// 分级加载增加根据父的过滤条件
				// String rootFilter = treeOption.containsKey("rootFilter") ?
				// treeOption.getString("rootFilter") : null;
				JSONArray rootConditions = treeOption.containsKey("rootFilter") ? treeOption.getJSONObject("rootFilter").getJSONArray("filters") : null;
				Object parentValue = treeOption.containsKey("parentValue") ? treeOption.get("parentValue") : null;
				if (null != parentValue) {
					// filters.add(parentField + " = :" +
					// QUERY_TREE_PARENT_VAR_NAME);
					// variables.put(QUERY_TREE_PARENT_VAR_NAME, parentValue);
					cond.and().andEqualTo(parentField, parentValue);

				} else {
					if (null == rootConditions)
						// filters.add(parentField + " is null");// 默认根条件就是
						// parent
						cond.and().andIsNull(parentField);
					else
						ConditionUtil.buildCondition(rootConditions, cond);
					// filters.add(rootFilter);
				}
			}
		}
		Table table = null;
		try {
			Integer total = commMapper.selectCountByExample(cond);
			
			
			// 设置分页信息
			//cond.setPageSize(limit);
			//cond.setPageNo(offset);
			// 排序
			ConditionUtil.buildOrderBy(JSONObject.parseArray(orderBy), cond);
			// 如果是-1,则获取全部
			if (limit!=-1) {
				
				PageHelper.startPage(offset/limit+1, limit);
			}
			//System.out.println("============cond.getPageNo()=================="+cond.getPageNo());
			//System.out.println("============cond.getPageSize()=================="+cond.getPageSize());
			// 查询table格式的结果集
			table = Transform.createTableByResultSet(commMapper.selectByExample(cond), entityTable.getEntityClass(), columns);
			// 设置偏移量
			if (offset != null && offset.equals(0)) {
				table.setTotal(total);
				table.setOffset(offset);
				table.setTableName(tableName);
			}

			if (isTree && !treeDelayLoad) {
				table.setIDColumn(idField);
				return Transform.tableToTreeJson(table, parentField);
			} else
				return Transform.tableToJson(table);
		} finally {
			// 记录日志
			Object query = getLoggerReturnValue(entityTable.getEntityClass(), "query");
			if (query != null && (Boolean) query) {
				saveUserLog(params, "查询", System.currentTimeMillis());
			}
		}
	}

	/**
	 * 保存Table数据，并指定列范围
	 * 
	 * @param table
	 * @param columns
	 *            列范围
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static void saveData(Table table, Collection<String> columns, CommMapper commMapper, JSONObject params) throws Exception {

		EntityTable entityTable = EntityHelper.getEntityBySimpleName(table.getTableName());
		Object query = getLoggerReturnValue(entityTable.getEntityClass(), "query");
		Long startDate = System.currentTimeMillis();
		if (columns == null) {
			columns = new ArrayList<String>();
			columns.addAll(table.getColumnNames());
		}
		Class<?> tableClass = entityTable.getEntityClass();
		for (Row row : table.getRows(RowState.NEW)) {
			Object bean = tableClass.newInstance();
			// JSONObject entity = new JSONObject();
			for (String column : columns) {
				String columnType = table.getColumnType(column).toString();
				Object columnValue = row.getValue(column);
				if ("null".equals(columnValue)||null==columnValue) {
					continue;
				}
				// entity.put(column, row.getValue(column));
				Field field = tableClass.getDeclaredField(column);
				field.setAccessible(true);
				
				if (DataType.STRING.toString().equals(columnType)) {
					field.set(bean, String.valueOf(columnValue));
				} else {
					field.set(bean, columnValue);
				}
			}
			commMapper.insertSelective(bean);
			// 记录日志
			if (query != null) {
				saveUserLog(params, "新增", startDate);
			}
		}
		for (Row row : table.getRows(RowState.EDIT)) {
			Object bean = tableClass.newInstance();
			// JSONObject entity = new JSONObject();
			for (String column : columns) {
				// entity.put(column, row.getValue(column));
				Object columnValue = row.getValue(column);
				if ("null".equals(columnValue)||null==columnValue) {
					continue;
				}
				Field field = tableClass.getDeclaredField(column);
				field.setAccessible(true);
				String columnType = table.getColumnType(column).toString();
				if (DataType.STRING.toString().equals(columnType)) {
					field.set(bean, String.valueOf(columnValue));
				} else {
					field.set(bean,columnValue);
				}

			}
			commMapper.updateByPrimaryKeySelective(bean);
			// 记录日志
			if (query != null) {
				saveUserLog(params, "更新", startDate);
			}
		}

		for (Row row : table.getRows(RowState.DELETE)) {
			commMapper.deleteByPrimaryKey(row.getValue(table.getIDColumn()));
			// 记录日志
			if (query != null) {
				saveUserLog(params, "删除", startDate);
			}
		}
	}

	private static void saveUserLog(JSONObject params, String operName, Long startDate) {
		CommMapper<UserOperLog> commMapper = (CommMapper<UserOperLog>) SpringContextUtil.getBean(StringUtil.getDaoName(UserOperLog.class));
		JSONObject context = params.getJSONObject("context");
		UserOperLog operLog = new UserOperLog();
		operLog.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
		if ("查询".equals(operName)) {
			operLog.setMethodName("com.zero.core.common.controller.CommonBaasController.query");
		} else if ("新增".equals(operName) || "更新".equals(operName)) {
			operLog.setMethodName("com.zero.core.common.controller.CommonBaasController.save");
		} else {
			operLog.setMethodName("com.zero.core.common.controller.CommonBaasController.sqlQuery");
		}
		operLog.setOperName(operName);
		operLog.setIpAddr(context.getString("ipAddr"));
		operLog.setOperOrgId(context.getString("orgId"));
		operLog.setOperOrgName(context.getString("orgName"));
		operLog.setOperUserId(context.getString("userId"));
		operLog.setOperUserName(context.getString("userName"));
		operLog.setReferer(context.getString("referer"));
		params.remove("context");
		operLog.setOperParams(params.toJSONString());
		operLog.setStartTime(startDate);
		operLog.setEndTime(System.currentTimeMillis());
		operLog.setTimeCost(operLog.getEndTime() - operLog.getStartTime());
		commMapper.insert(operLog);
	}

	public static Object getLoggerReturnValue(Class entityClass, String methodName) {
		Annotation annotation = entityClass.getAnnotation(BaasLogger.class);
		if (annotation != null) {
			Method m;
			try {
				m = annotation.annotationType().getDeclaredMethod(methodName, null);
				return m.invoke(annotation, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}
	

}
