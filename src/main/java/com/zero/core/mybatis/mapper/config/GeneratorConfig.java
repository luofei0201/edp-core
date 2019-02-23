package com.zero.core.mybatis.mapper.config;

import java.util.List;

import com.zero.core.mybatis.mapper.config.base.Config;
import com.zero.core.mybatis.mapper.config.base.GeneratorConfiguration;
import com.zero.core.utils.PropertiesUtil;

public class GeneratorConfig implements GeneratorConfiguration {

	@Override
	public void configure(Config config) {
		PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesUtil.class.getClassLoader().getResourceAsStream(this.CONFIG_FILE_PATH));
		propertiesUtil.setValue(config.getKey(), config.getValue());

	}

	@Override
	public void configure(List<Config> configList) {
		PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesUtil.class.getClassLoader().getResourceAsStream(this.CONFIG_FILE_PATH));
		for (Config config : configList) {
			propertiesUtil.setValue(config.getKey(), config.getValue());
		}
	}


}
