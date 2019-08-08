package com.zju.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.mapper.CommentMapper;
import com.zju.model.Comment;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
//@Sql("/init-schema2.sql")
public class CommentTest {
	Logger logger = Logger.getLogger(CommentTest.class);
	
	
	@Resource
	CommentMapper commentMapper;
	
	@Test
	public void commentTest() {
		
		/*Comment comment= new Comment();
		comment.setContent("hahaha");
		comment.setCreatedDate(new Date());
		comment.setEntityId(1);
		comment.setEntityType(33);
		comment.setUserId(10);
		comment.setStatus(0);
		commentMapper.insComment(comment);
		System.out.println("数据库初始化成功");
		List<Comment> list = commentMapper.selByEntity(1, 33);
		System.out.println(list.size());
		list = commentMapper.selByUserId(10);
		System.out.println(list.size());*/
		
		commentMapper.updCommentStatus(1, 33, 1);
		
		System.out.println(commentMapper.selCommentCount(1, 33));
		
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
