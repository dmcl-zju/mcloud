package com.zju.service;

import java.util.List;

/**
 * 	��עService
 * @author lin
 *
 */
public interface FollowService {
	
	
	/**
	 * 
	 * @param userId ˭�����Ĺ�ע����---һ����ǵ�ǰ��¼�û�
	 * @param entityType Ҫ��ע��ʵ���������
	 * @param entityId Ҫ��ע��ʵ�����id
	 * @return
	 */
	public boolean follow(int userId,int entityType,int entityId);
	//ȡ����ע
	public boolean unfollow(int userId,int entityType,int entityId);
	//��ȡ��ע�Լ��ķ�˿
	public List<Integer> getFollowers(int entityType,int entityId,int offset,int count);
	//��ȡ�Լ���ע����
	public List<Integer> getFollowees(int entityType,int userId,int offset,int count);
	//���ط�˿��Ŀ
	public long getFollowerCount(int entityType,int entityId);
	//���ع�ע��
	public long getFolloweeCount(int entityType,int userId);
	//�����ǲ��Ƿ�˿����ע�ߣ�
	public boolean isFollower(int userId,int entityType,int entityId);
	
}
