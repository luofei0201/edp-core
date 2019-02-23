package com.zero.core.baas;

/**
 * Baas数据格式化
 * @author luofei
 */
public class BaasDataFormater {
	
	private Class modelClass;

	public Class getModelClass() {
		return modelClass;
	}
	

	public void setModelClass(Class modelClass) {
		this.modelClass = modelClass;
	}
	
	
	public BaasDataFormater(Class modelClass){
		this.modelClass=modelClass;
	}
	
}
