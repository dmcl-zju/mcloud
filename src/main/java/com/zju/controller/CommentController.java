package com.zju.controller;


import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.Comment;
import com.zju.model.EntityType;
import com.zju.model.HostHolder;
import com.zju.model.Question;
import com.zju.service.CommentService;
import com.zju.service.QuestionService;




@Controller
public class CommentController {
	
	Logger logger = Logger.getLogger(CommentController.class);
	
	@Resource
	CommentService commentServiceImpl;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	QuestionService questionServiceImpl;
	
	@Resource
	EventProducer eventProduce;
	
	
	
	@RequestMapping(value = {"/addComment"}, method = {RequestMethod.POST})
	public String addComment(@RequestParam("questionId") int questionId,
							 @RequestParam("content") String content) {
		try {
			Comment comment = new Comment();
			
			//�ж��ǲ��ǵ�½�û�
			if(hostHolder.get()!=null) {
				comment.setUserId(hostHolder.get().getId());
			}else {
				//������ǵ�½�û�ֱ������ȥ��½
				return "redirect:/reglogin";
			}
			comment.setContent(content);
			comment.setCreatedDate(new Date());
			comment.setEntityId(questionId);
			comment.setEntityType(EntityType.ENTITY_QUESTION);
			comment.setStatus(0);
			//ҵ����ύ
			commentServiceImpl.addComment(comment);
			
			//�����첽�¼�
			EventModel eventModel = new EventModel();
			Question question = questionServiceImpl.getQuestionDetail(questionId);
			eventModel.setActorId(hostHolder.get().getId()).setEntityOwnerId(question.getUserId()).setEntityType(EntityType.ENTITY_QUESTION)
						.setEntityId(questionId).setEventType(EventType.COMMENT);
			eventProduce.fireEvent(eventModel);
			
			//�������۵�����--֮�����첽������
			int commentCount = commentServiceImpl.getCommentCount(comment.getEntityId(), comment.getEntityType());
			questionServiceImpl.updateQuestionCount(comment.getEntityId(), commentCount);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("�ύ���۳���"+e.getMessage());
		}
		//�ض���Ⱥ������ҳ��
		return "redirect:/question/" + String.valueOf(questionId);
	}
}
