package com.zju.model;

import java.util.Date;

/**
 * 	’Ô∂œ±®∏Ê
 * @author lin
 *
 */
public class Report {
	private int id;
	private int dataId;
	private int docterId;
	private int userId;
	private String guidance;
	private Date createdTime;
	
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createTime) {
		this.createdTime = createTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDataId() {
		return dataId;
	}
	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
	public int getDocterId() {
		return docterId;
	}
	public void setDocterId(int docterId) {
		this.docterId = docterId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getGuidance() {
		return guidance;
	}
	public void setGuidance(String guidence) {
		this.guidance = guidence;
	}
	@Override
	public String toString() {
		return "Report [id=" + id + ", dataId=" + dataId + ", docterId=" + docterId + ", userId=" + userId
				+ ", guidence=" + guidance + ", createTime=" + createdTime + "]";
	}
	
	
	
}
