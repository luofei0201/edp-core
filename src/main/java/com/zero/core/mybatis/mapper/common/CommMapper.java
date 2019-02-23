package com.zero.core.mybatis.mapper.common;

import java.util.List;

import com.zero.core.baas.data.QueryParam;


/**
 * 通用Mapper接口
 * @author luofei
 *
 * @param <T>
 */

public interface CommMapper<T> extends Mapper<T> {
	/**
	 * 
	 * @param example
	 * @return
	 */
	public List<T> baasSqlQuery(QueryParam queryParam);
	

}
