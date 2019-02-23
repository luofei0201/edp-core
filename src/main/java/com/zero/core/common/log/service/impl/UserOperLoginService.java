/**
 * 
 */
package com.zero.core.common.log.service.impl;

import org.springframework.stereotype.Service;

import com.zero.core.common.log.model.UserOperLog;
import com.zero.core.common.log.service.IUserOperLoginService;
import com.zero.core.common.service.CommonService;

/**
 * @author luofei
 */
@Service
public class UserOperLoginService extends CommonService<UserOperLog> implements IUserOperLoginService {

	@Override
	public void saveLog(UserOperLog userOperLog) {
		super.add(userOperLog);

	}

}
