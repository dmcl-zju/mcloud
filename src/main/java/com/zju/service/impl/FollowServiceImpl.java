package com.zju.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zju.service.FollowService;
import com.zju.utils.JedisAdapter;
import com.zju.utils.Rediskeyutil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;


@Service
public class FollowServiceImpl implements FollowService{
	
	@Resource
	JedisAdapter jedisAdapter;

	@Override
	public boolean follow(int userId, int entityType, int entityId) {
		//˭��ע��������󣬴����userId
		String followerKey = Rediskeyutil.getFollowerKey(entityType, entityId);
		//user��ע��ʲô�������entityId
		String followeeKey = Rediskeyutil.getFolloweeKey(entityType, userId);
		//����ʱ������
		Date date = new Date();
		
		Jedis jedis = jedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
		tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
		List<Object> res = jedisAdapter.exec(tx, jedis);
		// TODO Auto-generated method stub
		if(res==null) {
			return false;
		}
		return res.size()==2&&(long)res.get(0)>0&&(long)res.get(1)>0;
	}

	@Override
	public boolean unfollow(int userId, int entityType, int entityId) {
		// TODO Auto-generated method stub
		//˭��ע��������󣬴����userId
		String followerKey = Rediskeyutil.getFollowerKey(entityType, entityId);
		//user��ע��ʲô�������entityId
		String followeeKey = Rediskeyutil.getFolloweeKey(entityType, userId);
		
		Jedis jedis = jedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zrem(followerKey, String.valueOf(userId));
		tx.zrem(followeeKey, String.valueOf(entityId));
		List<Object> res = jedisAdapter.exec(tx, jedis);
		// TODO Auto-generated method stub
		if(res==null) {
			return false;
		}
		return res.size()==2&&(long)res.get(0)>0&&(long)res.get(1)>0;
	}

	@Override
	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
		// TODO Auto-generated method stub
		String followerKey = Rediskeyutil.getFollowerKey(entityType, entityId);
		return convertHelper(jedisAdapter.zrange(followerKey,offset, count));
	}

	@Override
	public List<Integer> getFollowees(int entityType, int userId, int offset, int count) {
		// TODO Auto-generated method stub
		String followeeKey = Rediskeyutil.getFolloweeKey(entityType, userId);
		return convertHelper(jedisAdapter.zrange(followeeKey,offset, count));
	}
	
	//��Ϊjedis�з��صĶ����ַ�����ʽ�����תΪint���
	private List<Integer> convertHelper(Set<String> set){
		List<Integer> list = new ArrayList<>();
		for(String s:set) {
			list.add(Integer.parseInt(s));
		}
		return list;
	}
	
	@Override
	public long getFollowerCount(int entityType, int entityId) {
		// TODO Auto-generated method stub
		String followerKey = Rediskeyutil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zcard(followerKey);
	}
	
	@Override
	public long getFolloweeCount(int entityType, int userId) {
		// TODO Auto-generated method stub
		String followeeKey = Rediskeyutil.getFolloweeKey(entityType, userId);
		return jedisAdapter.zcard(followeeKey);
	}

	@Override
	public boolean isFollower(int userId,int entityType, int entityId) {
		// TODO Auto-generated method stub
		String followerKey = Rediskeyutil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zscore(followerKey, String.valueOf(userId))!=null;
	}
	

}
