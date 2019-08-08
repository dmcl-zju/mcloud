package com.zju.model;

import java.util.Date;

public class Message {
	private int id;
	private int fromId;
	private int fromRole;
	private int toId;
	private int toRole;
	private String content;
	private Date createdDate;
	private int hasRead;
	private String conversationId;
	
	

	public int getFromRole() {
		return fromRole;
	}
	public void setFromRole(int fromRole) {
		this.fromRole = fromRole;
	}
	public int getToRole() {
		return toRole;
	}
	public void setToRole(int toRole) {
		this.toRole = toRole;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFromId() {
		return fromId;
	}
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	public int getToId() {
		return toId;
	}
	public void setToId(int toId) {
		this.toId = toId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public int getHasRead() {
		return hasRead;
	}
	public void setHasRead(int hasRead) {
		this.hasRead = hasRead;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	@Override
	public String toString() {
		return "Message [id=" + id + ", fromId=" + fromId + ", fromRole=" + fromRole + ", toId=" + toId + ", toRole="
				+ toRole + ", content=" + content + ", createdDate=" + createdDate + ", hasRead=" + hasRead
				+ ", conversationId=" + conversationId + "]";
	}
	
	
	
	
}
