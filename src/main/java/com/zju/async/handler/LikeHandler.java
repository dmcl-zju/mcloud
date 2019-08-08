package com.zju.async.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.zju.async.EventHandler;
import com.zju.async.EventModel;
import com.zju.async.EventType;
import com.zju.model.Message;
import com.zju.service.MessageService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;

@Component
public class LikeHandler implements EventHandler{

	@Resource
	MessageService messageServiceImpl;
	
	@Resource
	UserService userServiceImpl;
	
	@Override
	public void doHandler(EventModel model) {
		// TODO Auto-generated method stub
		
		Message message = new Message();
		message.setFromId(WendaUtil.SYSTEM_USERID);
		message.setFromRole(WendaUtil.SYSTEM_USERROLE);
		message.setToId(model.getEntityOwnerId());
		//这里点赞暂时定为只有用户能做，医生不能做
		message.setToRole(1);
		message.setContent("用户"+userServiceImpl.getUserById(model.getActorId()).getName()+"点赞了你的回答，http://127.0.0.1/question/"+model.getExts("questionId"));
		message.setConversationId(WendaUtil.SYSTEM_USERID>model.getEntityOwnerId()?""+model.getEntityOwnerId()+WendaUtil.SYSTEM_USERID:""+WendaUtil.SYSTEM_USERID+model.getEntityOwnerId());
		message.setCreatedDate(new Date());
		messageServiceImpl.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
		List<EventType> list = new ArrayList<>();
		list.add(EventType.LIKE);
		return list;
	}

}
