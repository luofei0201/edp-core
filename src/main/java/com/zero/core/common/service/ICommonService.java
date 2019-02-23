package com.zero.core.common.service;

import java.util.List;

import com.zero.core.common.annotation.Logger;
import com.zero.core.mybatis.mapper.common.CommMapper;
import com.zero.core.mybatis.mapper.entity.Example;

/**
 * 通用的服务接口
 * 
 * @author luofei
 * 
 */
public interface ICommonService<T> {


	/**
	 * 获取一个对象
	 * 
	 * @param key
	 * @return
	 */
	
	public T get(Object key);

	/**
	 * 根据主键删除一个对象
	 * 
	 * @param key
	 * @return 删除的记录数
	 */
	public int deleteBykey(Object key);
	
	
	/**
	 * 删除一个对象
	 * @param t
	 * @return
	 */
	public int delete(T t);
	

	/**
	 * 按条件删除一个对象
	 * @param t
	 * @return
	 */
	public int delete(Example example);


	/**
	 * 新增一个对象
	 * 
	 * @param t
	 *            新增的对象
	 * @return 新增的条数
	 */
	public int add(T t);

	/**
	 * 更新一个对象
	 * 
	 * @param key
	 * @return
	 */
	public int update(T t);
	
	/**
	 * 更新一个对象,为null的不更新
	 * 
	 * @param key
	 * @return
	 */
	public int updateSelective(T t);
	
	


	/**
	 * 分页获取对象列表,默认为每页20条
	 * 
	 * @param key
	 * @return
	 */
	public List<T> page(Integer start);

	/**
	 * 分页获取对象列表
	 * 
	 * @param key
	 * @return
	 */
	public List<T> page(Integer start, Integer pageSize);
	
	/**
	 * 分页获取对象列表
	 * 
	 * @param key
	 * @return
	 */
	public List<T> pageByExample(Example example);
	
	/**
	 * 获取对象列表
	 * @param key
	 * @return
	 */
	public List<T> listByExample(Example example);
	
	/**
	 * 获取记录数
	 * @param example
	 * @return
	 */
	public int countByExample(Example example);
	
	
}
