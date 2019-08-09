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

//-------------------------------------------封装一些常用的命令----------------------------------------------------------------------------
	
	
	
	
	
	
	//添加有过期时间的字符串
	public void setEX(String key,String value,int seconds) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.setex(key, seconds, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//删除key值
	public void del(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//是是否包含key
	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.exists(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return false;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}

	//添加字符串
	public void set(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//获取字符串
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//添加集合元素
	public long sadd(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//删除集合元素
	public long srem(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.srem(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//是否是集合成员
	public boolean sismember(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sismember(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return false;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//集合大小
	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//list头部插入（左边）
	public long lpush(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lpush(key, value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//list头部插入（左边）
	public List<String> lrange(String key,long start,long stop) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lrange(key, start, stop);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}

	//list尾部阻塞弹出（右侧）
	public List<String> brpop(int timeout,String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.brpop(timeout, key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//redis的事务
	public Jedis getJedis() {
		return pool.getResource();
	}
	
	
	public Transaction multi(Jedis jedis) {
		 try {
	          return jedis.multi();
         } catch (Exception e) {
             logger.error("发生异常" + e.getMessage());
         } finally {
         }
       return null;
	}
	
	public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
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
	
	//有序set
	public long zadd(String key,double score,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zadd(key,score,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//有序set
	public long zrem(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrem(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	//有序set
	public Set<String> zrange(String key,long start,long end) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrange(key,start,end);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//有序set
	public long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zcard(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return 0;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	//有序set
	public Double zscore(String key,String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zscore(key,value);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("redis异常："+e.getMessage());
			return null;
		}finally {
			if(null!=jedis) {
				jedis.close();
			}
		}
	}
	
	
	

}
