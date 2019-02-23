package com.zero.core.baas.action;

import com.zero.core.baas.BaasException;

import com.zero.core.baas.BaasException;

public class ActionException extends BaasException {
	private static final long serialVersionUID = -8149788234971565128L;

	public ActionException(String msg){
		super(msg);
	}

	public ActionException(String msg, Exception exception){
		super(msg, exception);
	}
}
