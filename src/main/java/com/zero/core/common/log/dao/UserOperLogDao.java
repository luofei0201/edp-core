package com.zero.core.common.log.dao;

import org.springframework.stereotype.Repository;

import com.zero.core.common.log.model.UserOperLog;
import com.zero.core.mybatis.mapper.common.CommMapper;

@Repository("UserOperLogDao")
public interface UserOperLogDao extends CommMapper<UserOperLog> {

}
