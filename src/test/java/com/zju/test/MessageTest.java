package com.zju.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.mapper.MessageMapper;
import com.zju.model.Message;

@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class MessageTest {

	@Resource
	MessageMapper messageMapper;
	
	
	@Test
	public void messageTest() {
		/*Message message = new Message();
		message.setContent("������");
		message.setFromId(10);
		message.setToId(11);
		message.setConversationId("10_11");
		messageMapper.insMessage(message);
		System.out.println("���ݲ���ɹ�");
		System.out.println(messageMapper.selByconversationId("10_11", 10, 0).size());*/
		
		List<Message> messages = messageMapper.selByconversationId("13_14", 10, 0);
		for(Message message:messages) {
			System.out.println(message);
		}
		/*
		List<Message> messages = messageMapper.selConversationListByUserid(14, 0, 10);
		for(Message message:messages) {
			System.out.println(message);
		}*/
		System.out.println(messageMapper.selCountOfUnread(14, "13_14"));
		
	}
}
