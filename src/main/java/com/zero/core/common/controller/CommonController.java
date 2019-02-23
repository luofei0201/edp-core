package com.zero.core.common.controller;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zero.core.common.service.ICommonService;
import com.zero.core.mybatis.mapper.entity.Condition;
import com.zero.core.mybatis.mapper.util.ConditionUtil;
import com.zero.core.utils.ReflectUtils;
import com.zero.core.utils.StringUtil;

/**
 * RESTful风格的通用控制器
 * 
 * @author luofei
 */
public class CommonController<T> {

	/**
	 * 服务
	 */
	private ICommonService<T> commonService;
	/**
	 * 实体类型
	 */
	private Class<T> modelClass;

	public void setService(ICommonService<T> service) {
		commonService = service;
	}

	public void setModel(Class<T> cls) {
		modelClass = cls;
	}

	/**
	 * 列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String list(@RequestBody JSONObject params) {
		Integer limit = params.getInteger("limit");
		Integer offset = params.getInteger("offset");
		JSONObject filter = JSONObject.parseObject(params.getString("filter")).getJSONObject("filter");
		String orderrule = params.getString("order");
		Condition condition = new Condition(modelClass);
		ConditionUtil.buildCondition(filter == null ? new JSONArray() : filter.getJSONArray("filters"), condition);
		int count = commonService.countByExample(condition);
		condition.setPageSize(limit);
		condition.setPageNo(offset);
		if (!StringUtil.isEmpty(orderrule)) {
			condition.setOrderByClause(orderrule);
		}
		List<T> t = commonService.pageByExample(condition);
		JSONObject result = new JSONObject();
		result.put("rows", t);
		result.put("total", count);
		return result.toJSONString();
	}

	/**
	 * 根据判断是否重复
	 * 
	 * @return
	 */
	@RequestMapping(value = "/duplicate", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String duplicate(@RequestBody JSONObject params) {
		Object id = params.getString("id");
		String filter = params.getString("filter");
		Condition condition = new Condition(modelClass);
		condition.and().andCondition(filter);
		if (!StringUtil.isEmpty(condition)) {
			condition.and().andNotEqualTo(ReflectUtils.getIdFieldName(modelClass), id);
		}
		int count = commonService.countByExample(condition);
		JSONObject result = new JSONObject();
		result.put("total", count);
		return result.toJSONString();
	}

	/**
	 * 详细
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String detail(@PathVariable String id) {
		JSONObject result = new JSONObject();
		result.put("rows", commonService.get(id));
		return result.toJSONString();
	}

	/**
	 * 新增
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseBody
	public String save(@RequestBody T t) throws Exception {
		int count = 0;
		Method m = ReflectUtils.getIdGetMethod(t.getClass());
		Object object = m.invoke(t, new Object[] {});
		if (StringUtil.isEmpty(object)) {
			m = ReflectUtils.getIdSetMethod(t.getClass());
			m.invoke(t, java.util.UUID.randomUUID().toString().replaceAll("-", ""));
			count = commonService.add(t);
		} else {
			count = commonService.updateSelective(t);
		}
		JSONObject result = new JSONObject();
		if (count == 1) {
			result.put("state", 1);
			result.put("msg", "保存成功");
		} else {
			result.put("state", 1);
			result.put("msg", "保存失败");
		}
		return result.toJSONString();
	}

	/**
	 * 批量删除
	 * 
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String detele(@RequestBody List<T> t) {
		int count = 0;
		for (T entity : t) {
			count = commonService.delete(entity);
		}
		JSONObject result = new JSONObject();
		if (count == 1) {
			result.put("state", 1);
			result.put("msg", "删除成功");
		} else {
			result.put("state", 1);
			result.put("msg", "删除失败");
		}
		return result.toJSONString();
	}

	/**
	 * 单个删除
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteByExample(@PathVariable Serializable id) {
		int count = commonService.deleteBykey(id);
		JSONObject result = new JSONObject();
		if (count == 1) {
			result.put("state", 1);
			result.put("msg", "删除成功");
		} else {
			result.put("state", 1);
			result.put("msg", "删除失败");
		}
		return result.toJSONString();
	}

}
