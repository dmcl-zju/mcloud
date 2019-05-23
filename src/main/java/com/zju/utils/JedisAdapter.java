package com.zju.utils;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter implements InitializingBean{
	
	Logger logger = Logger.getLogger(JedisAdapter.class);
	JedisPool pool;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		pool = new JedisPool("localhost",6379);
	}

//-------------------------------------------��װһЩ���õ�����----------------------------------------------------------------------------
	//����ַ���
	public void set(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//��ȡ�ַ���
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//��Ӽ���Ԫ��
	public long sadd(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//ɾ������Ԫ��
	public long srem(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.srem(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//�Ƿ��Ǽ��ϳ�Ա
	public boolean sismember(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sismember(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
			return false;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//���ϴ�С
	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}

}
