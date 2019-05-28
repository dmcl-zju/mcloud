package com.zju.utils;



import java.io.IOException;
import java.util.List;
import java.util.Set;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

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
			logger.error("redis�쳣��"+e.getMessage());
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
			logger.error("redis�쳣��"+e.getMessage());
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
			logger.error("redis�쳣��"+e.getMessage());
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
			logger.error("redis�쳣��"+e.getMessage());
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
			logger.error("redis�쳣��"+e.getMessage());
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
			logger.error("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//listͷ�����루��ߣ�
	public long lpush(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lpush(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//listͷ�����루��ߣ�
	public List<String> lrange(String key,long start,long stop) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lrange(key, start, stop);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}

	//listβ�������������Ҳࣩ
	public List<String> brpop(int timeout,String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.brpop(timeout, key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//redis������
	public Jedis getJedis() {
		return pool.getResource();
	}
	
	
	public Transaction multi(Jedis jedis) {
		 try {
	          return jedis.multi();
         } catch (Exception e) {
             logger.error("�����쳣" + e.getMessage());
         } finally {
         }
       return null;
	}
	
	public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("�����쳣" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                tx.close();
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
	
	//����set
	public long zadd(String key,double score,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zadd(key,score,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//����set
	public long zrem(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrem(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//����set
	public Set<String> zrange(String key,long start,long end) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrange(key,start,end);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//����set
	public long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zcard(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//����set
	public Double zscore(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zscore(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis�쳣��"+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	
	

}
