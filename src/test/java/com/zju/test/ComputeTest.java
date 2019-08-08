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
 * 测试Service接口
 * @author lin
 *
 */

/**
 * 模拟取出特征值计算完毕，进行一步通知
 * @author lin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
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
		
		//首先用户通过app上传了数据，直接进行数据入库，然后返回dataId,进入异步的数据处理，处理后再进行
		//特征值得入库，然后到这一步。留下的信息就是dataId
		//
		int dataId = 4;
		FhrData fhrData = dataServiceImpl.getDataById(dataId);
	
    	//进入用户首页,先找到自己绑定的医生
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
	
	
	/**
	 * 获取spring中的bean名称
	 * @return
	 */
	public void getBeans() {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
		String[] strs = context.getBeanDefinitionNames();
		for(String str:strs) {
			System.out.println("bean名称：  "+str);
		}
	}
	
}
