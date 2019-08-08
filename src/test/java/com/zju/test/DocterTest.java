package com.zju.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.model.EntityType;
import com.zju.model.User;
import com.zju.model.ViewObject;
import com.zju.service.FollowService;
import com.zju.service.MessageService;
import com.zju.service.UserService;
import com.zju.service.impl.UserServiceImpl;
import com.zju.utils.WendaUtil;


/**
 * 测试Service接口
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class DocterTest {
	
	//name指定要实现的实体类
	@Resource
	UserService userServiceImpl;
	
	@Resource
	FollowService followServiceImpl;
	
		
	@Test
	public void testDocter() {
		
//		List<Map<String,Object>> docters = userServiceImpl.getAllDocter();
//		
//		for(Map<String,Object> docter:docters) {
//			System.out.println("医生：");
//			for(String key:docter.keySet()) {
//
//				System.out.println(key+"-----"+docter.get(key));
//
//			}
//			System.out.println("--------------");
//		}
		
		//华佗
		int docterId = 1;
		//刘备
		int userId = 14;
		
		boolean res = followServiceImpl.follow(userId, EntityType.ENTITY_DOCTER_BIND, docterId);
		System.out.println("绑定："+res);
		
//		res = followServiceImpl.unfollow(userId, EntityType.ENTITY_DOCTER_BIND, docterId);
//		System.out.println("解绑:" +res);
		System.out.println(followServiceImpl.getFollowerCount(EntityType.ENTITY_DOCTER_BIND, 1));
		
		System.out.println(followServiceImpl.getFolloweeCount(EntityType.ENTITY_DOCTER_BIND, 13));
		
	
		
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
