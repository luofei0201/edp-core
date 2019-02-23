package com.zero.core.baas.data;

public enum RowState {
	NONE, NEW, EDIT, DELETE;
	
	public static RowState parse(String state) {
		return com.zero.core.baas.Utils.isEmptyString(state) ? RowState.NONE : RowState.valueOf(state.toUpperCase());
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}
