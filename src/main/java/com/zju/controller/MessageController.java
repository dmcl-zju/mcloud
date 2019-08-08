package com.zju.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zju.model.HostHolder;
import com.zju.model.Message;
import com.zju.model.User;
import com.zju.model.ViewObject;
import com.zju.service.MessageService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;

@Controller
public class MessageController {

Logger logger = Logger.getLogger(CommentController.class);
	
	@Resource
	MessageService messageServiceImpl;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	UserService userServiceImpl;

	
	
	
	@RequestMapping(value= {"/msg/list"},method = {RequestMethod.GET})
	public String conversationDetail(Model model) {
		
		try {
			//先获取登陆的对象
			int userId = 0;
			User user = hostHolder.get();
			//如果没有登录就跳转到登陆页面
			if(null==user) {
				return "redirect:/reglogin"; 
			}
			userId = hostHolder.get().getId();
			int userRole = hostHolder.get().getRole();
			
			List<Message> conversationList = messageServiceImpl.getConversationList(userId,userRole,0, 10);
			List<ViewObject> conversations = new ArrayList<>();
			for(Message message:conversationList) {
				ViewObject vo = new ViewObject();
				vo.set("conversation", message);
				vo.set("unread", messageServiceImpl.getUnreadCount(userId,userRole, message.getConversationId()));
				
				int targetId = 0;
				int targetRole = 0;
				
				//获取对方的id
				if(userId==message.getFromId() && userRole==message.getFromRole()) {
					//说明发起者是本人，要找的是toId
					targetId = message.getToId();
					targetRole = message.getToRole();
				}else {
					//说明发起者是对方，要找的是toId
					targetId = message.getFromId();
					targetRole = message.getFromRole();
				}
				vo.set("user", userServiceImpl.getUserByIdAndRole(targetId, targetRole));
				conversations.add(vo);
			}
		
			model.addAttribute("conversations", conversations);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("获取会话列表失败"+e.getMessage());
		}
		
		return "letter";
		
	}
	
	
	
	@RequestMapping(value= {"/msg/detail"},method = {RequestMethod.GET})
	public String conversationDetail(Model model,@RequestParam("conversationId") String conversationId) {
		try {
			List<Message> conversation = messageServiceImpl.getConversationDetail(conversationId, 10, 0);
			List<ViewObject> messages = new ArrayList<>();
			for(Message message:conversation) {
				ViewObject vo = new ViewObject();
				vo.set("message", message);
				User user = userServiceImpl.getUserByIdAndRole(message.getFromId(), message.getFromRole());
				//System.out.println(user+"-----"+message.getFromId()+"--"+message.getContent());
               if (user == null) {
                    continue;
                }
	            vo.set("headUrl", user.getHeadUrl());
	            vo.set("userId", user.getId());
				messages.add(vo);
			}
			model.addAttribute("messages", messages);
			//进入了详情页面后将未读数清零
			messageServiceImpl.clearUnreadCount(hostHolder.get().getId(),hostHolder.get().getRole(), conversationId);
		} catch (Exception e) {
			logger.info("获取详情失败"+e.getMessage());
		}
		return "letterDetail";
	}
	
	
/////////////////////////////////////////////原来的信息发送/////////////////////////////////////////////	
	@RequestMapping(value = {"/msg/addMessage"}, method = {RequestMethod.POST})
	@ResponseBody
	public String addMessage(@RequestParam("toName") String toName,
							 @RequestParam("content") String content) {
		try {

			if(hostHolder.get()==null) {
				return WendaUtil.getJSONString(999, "用户未登陆");
			}
			
			User user = userServiceImpl.getUserByName(toName);
			if(user == null) {
				return WendaUtil.getJSONString(1, "用户不存在");
			}
			Message message = new Message();
			int toId = user.getId();
			int fromId = hostHolder.get().getId();
			message.setToId(toId);
			message.setFromId(fromId);
			message.setContent(content);
			message.setHasRead(0);
			message.setCreatedDate(new Date());
			//始终让小的放前面
			message.setConversationId(toId<fromId?toId+"_"+fromId:fromId+"_"+toId);
			messageServiceImpl.addMessage(message);
			return WendaUtil.getJSONString(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("插入失败"+e.getMessage());
			return WendaUtil.getJSONString(1,"插入失败");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
///////////////////////////////////////////////增加医生和孕妇的信息后进行更改/////////////////////////////////////////////////////////
	
	@RequestMapping(value = {"/msg/sendMessage"}, method = {RequestMethod.POST})
	@ResponseBody
	public String addMessage2(@RequestParam("toId") int toId,
							  @RequestParam("toRole") int toRole,
							  @RequestParam("content") String content) {
		try {

			if(hostHolder.get()==null) {
				return WendaUtil.getJSONString(999, "用户未登陆");
			}
			
			User user = userServiceImpl.getUserByIdAndRole(toId,toRole);
			if(user == null) {
				return WendaUtil.getJSONString(1, "用户不存在");
			}
			Message message = new Message();
			int fromId = hostHolder.get().getId();
			int fromRole = hostHolder.get().getRole();
		
			message.setToId(user.getId());
			message.setToRole(user.getRole());
			message.setFromId(fromId);
			message.setFromRole(fromRole);
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
			return WendaUtil.getJSONString(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("插入失败"+e.getMessage());
			return WendaUtil.getJSONString(1,"插入失败");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
