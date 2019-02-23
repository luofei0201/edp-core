package com.zero.core.mybatis.mapper.common;

import java.util.List;

public interface BaasSqlQueryMapper<T> {
	/**
	 * 
	 * @param params
	 * @return
	 */
	public List<T> baasQuery(String fiter);

}
