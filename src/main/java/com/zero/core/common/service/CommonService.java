package com.zero.core.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.zero.core.common.annotation.Logger;
import com.zero.core.mybatis.mapper.common.CommMapper;
import com.zero.core.mybatis.mapper.entity.Example;

/**
 * 通用接口实现类
 * 
 * @author luofei
 * 
 */

public  abstract  class CommonService<T> implements ICommonService<T> {
   
	@Autowired
	private CommMapper<T> commMapper;
	public T get(Object key) {
		return (T) commMapper.selectByPrimaryKey(key);
	}

	@Override
	public int deleteBykey(Object key) {
		return commMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int add(T t) {
		return commMapper.insert(t);
	}

	@Override
	public int update(T t) {
		return commMapper.updateByPrimaryKey(t);
	}
	

	@Override
	public int updateSelective(T t) {
		// TODO 自动生成的方法存根
		return commMapper.updateByPrimaryKeySelective(t);
	}

	@Override
	public List<T> page(Integer start) {
		PageHelper.startPage(start, 20);
		return commMapper.selectAll();
	}

	@Override
	public List<T> page(Integer start, Integer pageSize) {
		PageHelper.startPage(start, pageSize);
		return commMapper.selectAll();
	}

	@Override
	public List<T> pageByExample(Example example) {
		if(example.getPageSize()!=-1){
			PageHelper.startPage(example.getPageNo(), example.getPageSize());
		}
		return commMapper.selectByExample(example);
	}

	@Override
	public List<T> listByExample(Example example) {
		return commMapper.selectByExample(example);
	}


	@Override
	public int countByExample(Example example) {
		return commMapper.selectCountByExample(example);
	}


	@Override
	public int delete(T t) {
		return commMapper.delete(t);
	}


	@Override
	public int delete(Example example) {
		return commMapper.deleteByExample(example);
	}
	
	



}
