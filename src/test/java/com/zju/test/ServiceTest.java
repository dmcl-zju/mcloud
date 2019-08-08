package com.zju.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.model.Question;
import com.zju.model.ViewObject;
import com.zju.service.QuestionService;
import com.zju.service.UserService;
import com.zju.service.impl.UserServiceImpl;


/**
 * 测试Service接口
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class ServiceTest {
	
	//name指定要实现的实体类
	@Resource(name="userServiceImpl")
	UserServiceImpl userServiceImpl;
	
	@Resource(name="questionServiceImpl")
	QuestionService questionServiceImpl;
		
	@Test
	public void testService() {
		List<ViewObject> vos = new ArrayList<>();
		List<Question> questions = questionServiceImpl.getLastestQuestions(0, 0, 10);
		
		for(Question q:questions) {
			ViewObject vo = new ViewObject();
			vo.set("question",q);
			vo.set("user", userServiceImpl.getUserById(q.getUserId()));
			vos.add(vo);
			//System.out.println("问题："+q);
			//System.out.println("发布者："+userServiceImpl.getUserById(q.getUserId()));
		}
		for(ViewObject vo:vos) {
			System.out.println(vo.get("question"));
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
