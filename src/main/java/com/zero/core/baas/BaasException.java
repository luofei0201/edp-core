package com.zero.core.baas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaasException extends RuntimeException {
	protected static Logger logger = LoggerFactory.getLogger(BaasException.class);
	private static final long serialVersionUID = 4788254265308389054L;

	public BaasException(String msg){
		super(msg);
		logger.error(msg);
	}

	public BaasException(String msg, Exception exception){
		super(msg, exception);
		logger.error(msg, exception);
	}
}
