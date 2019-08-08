package com.zju.async.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.zju.async.EventHandler;
import com.zju.async.EventModel;
import com.zju.async.EventType;
import com.zju.model.EntityType;
import com.zju.model.FhrData;
import com.zju.model.Message;
import com.zju.service.DataService;
import com.zju.service.FollowService;
import com.zju.service.MessageService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;

@Component
public class ComputeHandler implements EventHandler{

	@Resource
	DataService dataServiceImpl;
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	MessageService messageServiceImpl;
	
	
	@Override
	public void doHandler(EventModel model) {
		// TODO Auto-generated method stub
		
		//��������ֵ�ü����봦��
		
		int dataId = Integer.parseInt(model.getExts("dataId"));
		
		FhrData fhrData = dataServiceImpl.getDataById(dataId);
		String path = fhrData.getData();
		comput(path);
		fhrData.setFhrBase(1);
		fhrData.setFhrDown(5);
		fhrData.setFhrUp(5);
		fhrData.setFhrStv(5);
		fhrData.setUcBase(5);
		fhrData.setScore(6);
		//�������ݸ���
		dataServiceImpl.updValue(fhrData);
		//ת����ҽ������
		
		//�ҵ��û��󶨵�ҽ��
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
	//ģ��������
	private void comput(String path) {
		System.out.println("��̨�����ݽ��м���-----�ļ�·��:"+path);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		List<EventType> list = new ArrayList<>();
		list.add(EventType.GETDATA);
		// TODO Auto-generated method stub
		return list;
	}

}
