package com.zero.core.mybatis.mapper.config.base;

import java.util.List;

/**
 * mybatis配置接口
 * @author luofei
 * 
 */
public interface Configuration {
	/**
	 * 执行配置
	 * @param config
	 */
	public void configure(Config config);
	
	/**
	 * 执行配置
	 * @param configList
	 */
	public void configure(List<Config> configList);
}
