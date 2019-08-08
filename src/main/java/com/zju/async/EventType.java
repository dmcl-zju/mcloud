package com.zju.async;

public enum EventType {
	LIKE(0),
	COMMENT(1),
	LOGIN(2),
	MAIL(3),
	FOLLOW(4),
	UNFOLLOW(5),
	QUESTION(6),
	//数据计算完成事件
	NEWREPORT(7),
	GETDATA(8);
	
	private int value;
	EventType(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
