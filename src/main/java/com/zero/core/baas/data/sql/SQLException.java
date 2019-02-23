package com.zero.core.baas.data.sql;

import com.zero.core.baas.BaasException;

public class SQLException extends BaasException {
	private static final long serialVersionUID = -6774208105258505926L;

	public SQLException(String msg){
		super(msg);
	}

	public SQLException(String msg, Exception exception){
		super(msg, exception);
	}

}
