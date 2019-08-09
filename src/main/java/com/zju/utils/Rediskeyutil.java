package com.zju.utils;

public class Rediskeyutil {
	
	
	private static String SPLIT = ":";
	private static String LIKE = "LIKE";
	private static String DISLIKE = "DISLIKE";
	private static String EVENTQUEUE = "EVENTQUEUE";
	
	private static String FOLLOWERKEY="FOLLOWERKEY";
	private static String FOLLOWEEKEY="FOLLOWEEKEY";
	private static String TIMELINEKEY="TIMELINEKEY";
	private static String TICKET = "TICKET";
	
	
	
	public static String getTicketKey(String ticket) {
		return TICKET+SPLIT+ticket;
	}

	public static String getLikeKey(int entityType,int entityId) {
		return LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
	
	public static String getDislikeKey(int entityType,int entityId) {
		return DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
	
	public static String getFollowerKey(int entityType,int entityId) {
		return FOLLOWERKEY+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
	
	public static String getFolloweeKey(int entityType,int entityId) {
		return FOLLOWEEKEY+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
	
	public static String getEVentqueueKey() {
		return EVENTQUEUE;
	}
	
	public static String getTimelineKey(int userId) {
		return TIMELINEKEY+SPLIT+String.valueOf(userId);
	}
}
