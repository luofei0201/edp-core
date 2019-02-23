package com.zero.core.common.log.service;

import com.zero.core.common.log.model.UserOperLog;

public interface IUserOperLoginService {
	
	/**
	 * 保存日志
	 * @param userOperLog
	 */
	public void saveLog(UserOperLog userOperLog);

}
