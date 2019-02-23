package com.zero.core.common.log.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户操作日志
 * 
 * @author luofei
 */
@Entity
@Table(name = "sa_user_oper_log")
public class UserOperLog {

	@Id
	private String id;
	/**
	 * 操作名称
	 */
	@Column(name = "oper_name")
	private String operName;

	/**
	 * 方法名
	 */
	@Column(name = "method_name")
	private String methodName;

	/**
	 * 来源
	 */
	@Column(name = "referer")
	private String referer;
	/**
	 * ip地址
	 */
	@Column(name = "ip_addr")
	private String ipAddr;
	/**
	 * 机构id
	 */
	@Column(name = "oper_org_id")
	private String operOrgId;
	/**
	 * 机构名称
	 */
	@Column(name = "oper_org_Name")
	private String operOrgName;
	/**
	 * 操作人id
	 */
	@Column(name = "oper_user_id")
	private String operUserId;

	/**
	 * 操作人名称
	 */
	@Column(name = "oper_user_name")
	private String operUserName;

	/**
	 * 开始时间
	 */
	@Column(name = "start_time")
	private Long startTime;

	/**
	 * 结束时间
	 */
	@Column(name = "end_time")
	private Long endTime;

	/**
	 *耗时
	 */
	@Column(name = "time_cost")
	private Long timeCost;
	
	/**
	 * 操作参数
	 */
	@Column(name = "oper_params",length=8000)
	private String operParams;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getOperOrgId() {
		return operOrgId;
	}

	public void setOperOrgId(String operOrgId) {
		this.operOrgId = operOrgId;
	}

	public String getOperOrgName() {
		return operOrgName;
	}

	public void setOperOrgName(String operOrgName) {
		this.operOrgName = operOrgName;
	}

	public String getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}

	public String getOperUserName() {
		return operUserName;
	}

	public void setOperUserName(String operUserName) {
		this.operUserName = operUserName;
	}

	public String getOperParams() {
		return operParams;
	}

	public void setOperParams(String operParams) {
		this.operParams = operParams;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(Long timeCost) {
		this.timeCost = timeCost;
	}
	
	
}
