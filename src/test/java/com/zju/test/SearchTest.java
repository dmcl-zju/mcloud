package com.zju.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.model.EntityType;
import com.zju.model.Question;
import com.zju.model.ViewObject;
import com.zju.service.QuestionService;
import com.zju.service.impl.SearchServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class SearchTest {
/*
	@Resource
	SearchServiceImpl searchServiceImpl;
	
	@Resource
	QuestionService questionServiceImpl;
	
	@Test
	public void messageTest() {
		
		String keyword="hahah";
		try {
			List<Question> qs = searchServiceImpl.searchQuestion(keyword, 0, 10, "<a>", "</a>");
			System.out.println("查到"+qs.size()+"条记录");
			for(Question question:qs) {
				ViewObject vo = new ViewObject();
				Question q = questionServiceImpl.getQuestionDetail(question.getId());
				if(question.getContent()!=null) {
					q.setContent(question.getContent());
				}
				if(question.getTitle()!=null) {
					q.setTitle(question.getTitle());
				}
				System.out.println(q);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
