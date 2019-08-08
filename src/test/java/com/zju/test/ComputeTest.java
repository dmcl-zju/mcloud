package com.zju.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.EntityType;
import com.zju.model.FhrData;
import com.zju.model.Message;
import com.zju.service.DataService;
import com.zju.service.FollowService;
import com.zju.service.MessageService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;


/**
 * ����Service�ӿ�
 * @author lin
 *
 */

/**
 * ģ��ȡ������ֵ������ϣ�����һ��֪ͨ
 * @author lin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class ComputeTest {
	
	@Resource
	DataService dataServiceImpl;
	
	@Resource
	EventProducer eventProducer;
	
	@Resource
	MessageService messageServiceImpl;
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	UserService userServiceImpl;
	
	
		
	@Test
	public void testCompute() {
		
		//�����û�ͨ��app�ϴ������ݣ�ֱ�ӽ���������⣬Ȼ�󷵻�dataId,�����첽�����ݴ���������ٽ���
		//����ֵ����⣬Ȼ����һ�������µ���Ϣ����dataId
		//
		int dataId = 4;
		FhrData fhrData = dataServiceImpl.getDataById(dataId);
	
    	//�����û���ҳ,���ҵ��Լ��󶨵�ҽ��
    	int userId = fhrData.getUserId();
    	List<Integer> followeeIds = followServiceImpl.getFollowees(EntityType.ENTITY_DOCTER_BIND,userId, 0, 10);
		
    	//������û���ע��ҽ��������֪ͨ
    	for(int toId:followeeIds) {
    		//������ɺ�ֱ�ӷ�һ����Ϣ����Ӧ��ҽ��
    		Message message = new Message();
    		int fromId = WendaUtil.ACTION_USERID;
    		//��Ϊ��ҽ�����Խ�ɫȷ��
    		message.setToId(toId);
    		message.setToRole(2);
    		message.setFromId(fromId);
    		message.setFromRole(1);
    		String content = "�и�"+userServiceImpl.getGravidaInfo(userId).getName()+"�ύ��̥�����ݴ�����ɣ���ǰ��д����:http://127.0.0.1/docter/pullreport/"+dataId;
    		message.setContent(content);
    		message.setHasRead(0);
    		message.setCreatedDate(new Date());
    		//ʼ����С�ķ�ǰ��
    		String conversationId = "";
    		if(toId<fromId) {
    			conversationId = toId+"_"+2+"_"+fromId+"_"+1;
    		}else {
    			conversationId = fromId+"_"+2+"_"+toId+"_"+1;
    		}
    		message.setConversationId(conversationId);
    		messageServiceImpl.addMessage(message);
    	}
		
		
		
		
	}
	
	
	/**
	 * ��ȡspring�е�bean����
	 * @return
	 */
	public void getBeans() {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
		String[] strs = context.getBeanDefinitionNames();
		for(String str:strs) {
			System.out.println("bean���ƣ�  "+str);
		}
	}
	
}
