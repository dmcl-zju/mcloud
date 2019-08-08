package com.zju.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.EntityType;
import com.zju.model.HostHolder;
import com.zju.model.Question;
import com.zju.model.User;
import com.zju.model.ViewObject;
import com.zju.service.FollowService;
import com.zju.service.QuestionService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;

/**
 * 	互相关注
 * @author lin
 *
 */
@Controller
public class FollowController {
	
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	EventProducer eventProducer;
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	QuestionService questionServiceImpl;
	
	
/////////////////////////////////////////////////增加医生相关///////////////////////////////////////////////////////////////////////////////////////////////////	
	//绑定自己的医生(和关注要区分一下）,输入的是要关注的医生id
	@RequestMapping({"/bindDocter"})
	@ResponseBody
	public String bindDocter(@RequestParam("docterid") int docterId) {

		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		User user = userServiceImpl.getDocterById(docterId);
		if(null==user) {
			return WendaUtil.getJSONString(1,"用户不存在");	
		}
		boolean res = followServiceImpl.follow(hostHolder.get().getId(), EntityType.ENTITY_DOCTER_BIND, docterId);
		
		
		//异步发站内信通知有人关注
//		EventModel eventModel = new EventModel();
//		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_USER).
//			setEntityId(docterId).setEntityOwnerId(docterId).setEventType(EventType.FOLLOW);
//		eventProducer.fireEvent(eventModel);
	
		return WendaUtil.getJSONString(res?0:1, String.valueOf(followServiceImpl.getFolloweeCount(EntityType.ENTITY_DOCTER_BIND, hostHolder.get().getId())));
	}
	
	//取关医生
	@RequestMapping({"/unBindDocter"})
	@ResponseBody
	public String unBindDocter(@RequestParam("docterid") int docterId) {
		
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		User user = userServiceImpl.getUserById(docterId);
		if(null==user) {
			return WendaUtil.getJSONString(1,"用户不存在");	
		}
		
		boolean res = followServiceImpl.unfollow(hostHolder.get().getId(), EntityType.ENTITY_USER, docterId);
		//异步发站内信通知有人关注
//		EventModel eventModel = new EventModel();
//		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_USER).
//			setEntityId(docterId).setEntityOwnerId(docterId).setEventType(EventType.UNFOLLOW);
//		eventProducer.fireEvent(eventModel);
	
		return WendaUtil.getJSONString(res?0:1, String.valueOf(followServiceImpl.getFolloweeCount(EntityType.ENTITY_DOCTER_BIND, hostHolder.get().getId())));
	}	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
	
	//关注用户,关注成功后返回自己的关注人数
	@RequestMapping({"/followUser"})
	@ResponseBody
	public String followUser(@RequestParam("userId") int userId) {

		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		User user = userServiceImpl.getUserById(userId);
		if(null==user) {
			return WendaUtil.getJSONString(1,"用户不存在");	
		}
		
	
		boolean res = followServiceImpl.follow(hostHolder.get().getId(), EntityType.ENTITY_USER, userId);
		//异步发站内信通知有人关注
		EventModel eventModel = new EventModel();
		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_USER).
			setEntityId(userId).setEntityOwnerId(userId).setEventType(EventType.FOLLOW);
		eventProducer.fireEvent(eventModel);
	
		return WendaUtil.getJSONString(res?0:1, String.valueOf(followServiceImpl.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.get().getId())));
	}
	

	//取关用户
	@RequestMapping({"/unfollowUser"})
	@ResponseBody
	public String unfollowUser(@RequestParam("userId") int userId) {
		
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		User user = userServiceImpl.getUserById(userId);
		if(null==user) {
			return WendaUtil.getJSONString(1,"用户不存在");	
		}
		
		boolean res = followServiceImpl.unfollow(hostHolder.get().getId(), EntityType.ENTITY_USER, userId);
		//异步发站内信通知有人关注
		EventModel eventModel = new EventModel();
		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_USER).
			setEntityId(userId).setEntityOwnerId(userId).setEventType(EventType.UNFOLLOW);
		eventProducer.fireEvent(eventModel);
	
		return WendaUtil.getJSONString(res?0:1, String.valueOf(followServiceImpl.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.get().getId())));
	}
	
	
	//关注问题
	@RequestMapping({"/followQuestion"})
	@ResponseBody
	public String followQuestion(@RequestParam("questionId") int questionId) {
		
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		Question question = questionServiceImpl.getQuestionDetail(questionId);
		if(question==null) {
			return WendaUtil.getJSONString(1, "问题不存在");
		}
		
		boolean res = followServiceImpl.follow(hostHolder.get().getId(), EntityType.ENTITY_QUESTION, questionId);
		//异步发站内信通知有人关注其发布的问题
		EventModel eventModel = new EventModel();
		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_QUESTION).
			setEntityId(questionId).setEntityOwnerId(question.getId()).setEventType(EventType.FOLLOW);
		eventProducer.fireEvent(eventModel);
		
		Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.get().getHeadUrl());
        info.put("name", hostHolder.get().getName());
        info.put("id", hostHolder.get().getId());
        info.put("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(res ? 0 : 1, info);
	}
	//取关问题
	@RequestMapping({"/unfollowQuestion"})
	@ResponseBody
	public String unfollowQuestion(@RequestParam("questionId") int questionId) {
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		Question question = questionServiceImpl.getQuestionDetail(questionId);
		if(question==null) {
			return WendaUtil.getJSONString(1, "问题不存在");
		}
		
		boolean res = followServiceImpl.unfollow(hostHolder.get().getId(), EntityType.ENTITY_QUESTION, questionId);
		//异步发站内信通知有人关注其发布的问题
		EventModel eventModel = new EventModel();
		eventModel.setActorId(hostHolder.get().getId()).setEntityType(EntityType.ENTITY_QUESTION).
			setEntityId(questionId).setEntityOwnerId(question.getId()).setEventType(EventType.UNFOLLOW);
		eventProducer.fireEvent(eventModel);
		
		Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.get().getId());
        info.put("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(res ? 0 : 1, info);
	}
	
	//显示关注者（粉丝）
	@RequestMapping({"/user/{uid}/followers"})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followServiceImpl.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.get() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.get().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userServiceImpl.getUserById(userId));
        return "followers";
    }

	//显示关注了什么
    @RequestMapping({"/user/{uid}/followees"})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followServiceImpl.getFollowees(EntityType.ENTITY_USER,userId, 0, 10);
        if (hostHolder.get() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.get().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followServiceImpl.getFolloweeCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser", userServiceImpl.getUserById(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userServiceImpl.getUserById(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", 0);
            vo.set("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followServiceImpl.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followServiceImpl.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
    
    
    
    
    
}
