package com.zju.test;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.zju.controller.LoginController;
import com.zju.model.User;
import com.zju.service.UserService;
import com.zju.utils.JedisAdapter;

/**
 * 测试登陆注册
 * @author lin
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class LoginTest {
	
	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	JedisAdapter jedisAdapter;
	
	@Test
	public void regist() {
//		String username = "关羽";
//		String password = "123456";
//		int role = 1;
//		Map<String,String> map = userServiceImpl.login(username, password, role);
//		//注册不成功--回到注册页面，同时携带错误信息
//		if(map.containsKey("msg")) {
//			System.out.println(map.get("msg"));
//		}
//		if(map.containsKey("erro")) {
//			System.out.println(map.get("erro"));
//		}
//		if(map.containsKey("ticket")) {
//			System.out.println(map.get("ticket"));
//		}
		//注册成功直接重定向到主页
//		String username = "葛洪";
//		String password = "123456";
//		int role = 2;
//		Map<String,String> map = userServiceImpl.regist(username, password, role);
//		for(String key:map.keySet()) {
//			System.out.println(key+"----"+map.get(key));
//		}
		
		
		String userJson = jedisAdapter.get("TICKET:3854bb45ca174d9e85bb10178317511a");
		System.out.println("吵到的内容"+userJson);
		User user = JSONObject.parseObject(userJson, User.class);
		System.out.println(user);
	}
	
}
