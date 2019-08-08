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
		
		//进行特征值得计算与处理
		
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
		//进行数据更新
		dataServiceImpl.updValue(fhrData);
		//转发给医生处理
		
		//找到用户绑定的医生
    	int userId = fhrData.getUserId();
    	List<Integer> followeeIds = followServiceImpl.getFollowees(EntityType.ENTITY_DOCTER_BIND,userId, 0, 10);
		
    	//给这个用户关注的医生发提醒通知
    	for(int toId:followeeIds) {
    		//处理完成后直接发一条信息给对应的医生
    		Message message = new Message();
    		int fromId = WendaUtil.ACTION_USERID;
    		//因为是医生所以角色确定
    		message.setToId(toId);
    		message.setToRole(2);
    		message.setFromId(fromId);
    		message.setFromRole(1);
    		String content = "孕妇"+userServiceImpl.getGravidaInfo(userId).getName()+"提交的胎心数据处理完成，请前往写报告:http://127.0.0.1/docter/pullreport/"+dataId;
    		message.setContent(content);
    		message.setHasRead(0);
    		message.setCreatedDate(new Date());
    		//始终让小的放前面
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
	//模拟计算过程
	private void comput(String path) {
		System.out.println("后台对数据进行计算-----文件路径:"+path);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		List<EventType> list = new ArrayList<>();
		list.add(EventType.GETDATA);
		// TODO Auto-generated method stub
		return list;
	}

}
