package com.zju.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.zju.utils.JedisAdapter;
import com.zju.utils.Rediskeyutil;

@Service
public class EventComsumer implements InitializingBean,ApplicationContextAware{
	
	Logger logger = Logger.getLogger(EventComsumer.class);
	
	private Map<EventType,List<EventHandler>> map = new HashMap<>();
	private ApplicationContext applicationContext;

	@Resource
	JedisAdapter jedisAdapter;
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		//��map������ӳ�䣬����һ�Զ�����
		if(null!=beans) {
			//����beans
			for(String key:beans.keySet()) {
				List<EventType> eventTypes = beans.get(key).getSupportEventTypes();
				for(EventType eventType:eventTypes) {
					if(!map.containsKey(eventType)) {
						map.put(eventType, new ArrayList<EventHandler>());
					}
					map.get(eventType).add(beans.get(key));
				}
			}
		}
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					//һֱѭ��ȡ�¼�
					String key = Rediskeyutil.getEVentqueueKey();
					List<String> events = jedisAdapter.brpop(0, key);
					for(String event:events) {
						if(event.equals(key)) {
							continue;
						}
						//���з����л�
						EventModel eventModel = JSONObject.parseObject(event, EventModel.class);
						if(!map.containsKey(eventModel.getEventType())) {
							logger.info("����ʶ���¼�");
							continue;
						}
						List<EventHandler> handlers = map.get(eventModel.getEventType());
						for(EventHandler handler:handlers) {
							handler.doHandler(eventModel);
						}	
					}
				}
				
			}
		});
		thread.start();
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
		
	}
	
	
}
