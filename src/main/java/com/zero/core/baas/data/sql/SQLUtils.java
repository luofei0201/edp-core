package com.zero.core.baas.data.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.alibaba.fastjson.JSONObject;
import com.zero.core.baas.Utils;
import com.zero.core.baas.data.DataUtils;
import com.zero.core.baas.data.Table;
import com.zero.core.baas.data.Transform;

/**
 * sql工具类
 * @author luofei
 */
public class SQLUtils {
	
	  public final static String QUERY_MASTER_VAR_NAME = "_sys_master_value_";
	  public final static String QUERY_TREE_PARENT_VAR_NAME = "_sys_tree_parent_value_";
	  public final static String VARIABLE_FLAG = "var-";
	    
	public static JSONObject query(JSONObject params) throws SQLException, NamingException {
        // 获取参数
        String db = params.getString("db");
        String tableName = params.getString("tableName");
        Object columns = params.get("columns");
        Integer limit = params.getInteger("limit");
        Integer offset = params.getInteger("offset");
        String orderBy = params.getString("orderBy");
        String condition = params.getString("condition");
        String filter = params.getString("filter");
        JSONObject variables =getVariables(params);
        List<String> filters = new ArrayList<String>();
        if (!Utils.isEmptyString(condition)) {
            filters.add(condition);
        }
        if (!Utils.isEmptyString(filter)) {
            filters.add(filter);
        }
        //处理主从
        if (params.containsKey("master")) {
            JSONObject master = params.getJSONObject("master");
            if (master.containsKey("field")) {
                filters.add(master.getString("field") + " = :" + QUERY_MASTER_VAR_NAME);
                variables.put(QUERY_MASTER_VAR_NAME, master.get("value"));
            }
        }
        //树形数据
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
                //分级加载增加根据父的过滤条件
                String rootFilter = treeOption.containsKey("rootFilter") ? treeOption.getString("rootFilter") : null;
                Object parentValue = treeOption.containsKey("parentValue") ? treeOption.get("parentValue") : null;
                if (null != parentValue) {
                    filters.add(parentField + " = :" + QUERY_TREE_PARENT_VAR_NAME);
                    variables.put(QUERY_TREE_PARENT_VAR_NAME, parentValue);
                } else {
                    if (null == rootFilter) filters.add(parentField + " is null");//默认根条件就是 parent is null
                    else filters.add(rootFilter);
                }
            }
        }

        //limit处理，-1取全部
        if (null != limit && limit == -1) limit = null;

        Table table = null;

        try {
            String format = "SELECT %s FROM %s %s %s ";

            String where = (filters != null && filters.size() > 0) ? " WHERE " + DataUtils.arrayJoin(filters.toArray(), "(%s)", " AND ") : "";
            orderBy = !Utils.isEmptyString(orderBy) ? " ORDER BY " + orderBy : "";

            String sql = String.format(format, "*", tableName, where, orderBy);
            //进行名字变量转换
            SQLStruct sqlStruct = new SQLStruct(sql);
            //table = DataUtils.queryData(conn, sqlStruct.getSQL(), sqlStruct.getBinds(variables), columns, offset, limit);
            if (offset != null && offset.equals(0)) {
                //where部分进行名字变量转换
                SQLStruct sqlWhereStruct = new SQLStruct(where);
                String sqlTotal = String.format(format, "COUNT(*)", tableName, sqlWhereStruct.getSQL(), "");
                //Object total = DataUtils.getValueBySQL(conn, sqlTotal, sqlWhereStruct.getBinds(variables));
                //table.setTotal(Integer.parseInt(total.toString()));
            }

            if (isTree && !treeDelayLoad) {
                table.setIDColumn(idField);
                return Transform.tableToTreeJson(table, parentField);
            } else
                return Transform.tableToJson(table);
        } finally {
           // conn.close();
        }
    }
	
	 //var-开头的参数认为也是变量,variables优先级高于var-
    private static JSONObject getVariables(JSONObject params) {
        JSONObject variables = params.getJSONObject("variables");
        if (null == variables) variables = new JSONObject();
        for (String key : params.keySet()) {
            if (key.startsWith(VARIABLE_FLAG)) {
                String varName = key.substring(VARIABLE_FLAG.length());
                if (!variables.containsKey(varName)) variables.put(varName, params.get(key));
            } else if (!"db".equals(key) && !"sql".equals(key) && !"countSql".equals(key) && !variables.containsKey(key)) {
                variables.put(key, params.get(key));
            }
        }
        return variables;
    }

}
