package com.zero.core.mybatis.mapper.config.base;

import java.util.List;

/**
 * 代码生成器配置接口
 * @author luofei
 */
public interface GeneratorConfiguration extends Configuration {
	
	public final String CONFIG_FILE_PATH="generator/generatorConfig.properties";

	@Override
	public void configure(Config config) ;

	@Override
	public void configure(List<Config> configList);
}
