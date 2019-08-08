package com.zju.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.Comment;
import com.zju.model.EntityType;
import com.zju.model.HostHolder;
import com.zju.service.CommentService;
import com.zju.service.LikeService;
import com.zju.utils.WendaUtil;

@Controller
public class LikeController {
	@Resource
	LikeService likeServiceImpl;
	@Resource
	HostHolder hostHolder;
	@Resource
	EventProducer eventProducer;
	@Resource
	CommentService commentServiceImpl;
	
	@RequestMapping(value = {"/like"}, method = {RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("commentId") int commentId) {
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		//异步处理点赞通知
		EventModel model = new EventModel();
		Comment comment = commentServiceImpl.getCommentById(commentId);
		
		
		
		model.setActorId(hostHolder.get().getId()).setEntityOwnerId(comment.getUserId()).setExts("questionId", String.valueOf(comment.getEntityId())).
		setEntityType(EntityType.ENTITY_COMMENT).setEntityId(commentId).setEventType(EventType.LIKE);
		
		
		eventProducer.fireEvent(model);	
		
		long likecount = likeServiceImpl.like(hostHolder.get().getId(),EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0,String.valueOf(likecount));
	}
	
	@RequestMapping(value = {"/dislike"}, method = {RequestMethod.POST})
	@ResponseBody
	public String dislike(@RequestParam("commentId") int commentId) {
		if(hostHolder.get()==null) {
			return WendaUtil.getJSONString(999);
		}
		long likecount = likeServiceImpl.dislike(hostHolder.get().getId(),EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0,String.valueOf(likecount));
	}
}
