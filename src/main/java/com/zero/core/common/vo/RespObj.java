package com.zero.core.common.vo;

public class RespObj {

	/**
	 * 数据
	 */
	private Object data;
	/**
	 * 状态码
	 */
	private String code;
	/**
	 * 返回消息
	 */
	private String message;

	/**
	 * 私有的构造函数,外部无法new
	 */
	private RespObj() {

	}

	private RespObj(Object data, String code, String message) {
		this.data = data;
		this.code = code;
		this.message = message;

	}

	public static RespObj success(Object data, String message) {
		return new RespObj(data, "1", message);
	}
	/**
	 * 失败
	 * 
	 * @param data
	 * @param message
	 * @return
	 */
	public static RespObj fail(String message) {
		return new RespObj(null, "0", message);
	}

	@Override
	public String toString() {
		return "RespObj [data=" + data + ", code=" + code + ", message=" + message + "]";
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}


	public String getMessage() {
		return message;
	}

}
