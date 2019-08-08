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
import com.zju.utils.WendaUtil;

@Component
public class ReportHandler implements EventHandler{

	@Resource
	MessageService messageServiceImpl;
	
	@Override
	public void doHandler(EventModel model) {
		// TODO Auto-generated method stub
		//进行处理
		Message message = new Message();
		int toId = model.getEntityOwnerId();
		int toRole = 1;
		int fromId = WendaUtil.ACTION_USERID;
		int fromRole = 1;
		
		
		message.setToId(toId);
		message.setToRole(1);
		message.setFromId(WendaUtil.ACTION_USERID);
		message.setFromRole(1);
		String content = "你有新的诊断报告生成，请点击查看: http://localhost/user/reports/"+model.getExts("reportId");
		message.setContent(content);
		message.setHasRead(0);
		message.setCreatedDate(new Date());
		//始终让小的放前面
		String conversationId = "";
		if(toId<fromId) {
			conversationId = toId+"_"+toRole+"_"+fromId+"_"+fromRole;
		}else {
			conversationId = fromId+"_"+fromRole+"_"+toId+"_"+toRole;
		}
		message.setConversationId(conversationId);
		messageServiceImpl.addMessage(message);
		
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		List<EventType> list = new ArrayList<>();
		list.add(EventType.NEWREPORT);
		// TODO Auto-generated method stub
		return list;
	}

}
