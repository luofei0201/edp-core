package com.zero.core.common.annotation;

public enum OperType {

	ADD("query"), DELETE("delete"),UPDATE("update"), QUERY("query");

	private String value;

	private OperType(String type) {
		this.value = type;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
