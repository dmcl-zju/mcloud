package com.zju.async.handler;

import java.util.ArrayList;
import java.util.List;


import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.zju.async.EventHandler;
import com.zju.async.EventModel;
import com.zju.async.EventType;
import com.zju.model.Question;
import com.zju.service.QuestionService;
import com.zju.service.impl.SearchServiceImpl;

@Component
public class QuestionHandler implements EventHandler{
	
	@Resource
	SearchServiceImpl searchServiceImpl;
	
	@Override
	public void doHandler(EventModel model) {
		Logger logger = Logger.getLogger(QuestionHandler.class);
		
		// TODO Auto-generated method stub
		try {
			searchServiceImpl.indexQuestion(model.getEntityId(),model.getExts("title"), model.getExts("content"));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("¸üÐÂsolr³ö´í"+e.getMessage());
		}
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
		List<EventType> list = new ArrayList<>();
		list.add(EventType.QUESTION);
		return list;
	}
	
	
}
