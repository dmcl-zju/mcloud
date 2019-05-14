package com.zju.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.zju.mapper.MessageMapper;
import com.zju.model.Message;
import com.zju.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService{

	@Resource
	MessageMapper messageMapper;
	
	@Resource
	SensitiveServiceImpl sensitiveServiceImpl;
		
	@Override
	public int addMessage(Message message) {
		//�Ƚ������дʹ���
		//html����
		message.setContent(HtmlUtils.htmlEscape(message.getContent()));
		//���дʹ���
		message.setContent(sensitiveServiceImpl.filter(message.getContent()));
		// TODO Auto-generated method stub
		return messageMapper.insMessage(message);
	}

	@Override
	public List<Message> getConversationDetail(String conversationId, int limit, int offset) {
		// TODO Auto-generated method stub
		return messageMapper.selByconversationId(conversationId, limit, offset);
	}

	@Override
	public List<Message> getConversationList(int userId, int offset, int limit) {
		// TODO Auto-generated method stub
		return messageMapper.selConversationListByUserid(userId, offset, limit);
	}

	@Override
	public int getUnreadCount(int userId, String conversationId) {
		// TODO Auto-generated method stub
		return messageMapper.selCountOfUnread(userId, conversationId);
	}

	@Override
	public int clearUnreadCount(int userId, String conversationId) {
		// TODO Auto-generated method stub
		return messageMapper.updHasread(userId, conversationId);
	}
	
}
