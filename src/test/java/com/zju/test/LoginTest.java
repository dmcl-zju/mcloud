package com.zju.test;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.controller.LoginController;
import com.zju.service.UserService;

/**
 * ���Ե�½ע��
 * @author lin
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class LoginTest {
	
	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	UserService userServiceImpl;
	
	@Test
	public void regist() {
		String username = "����";
		String password = "123456";
		Map<String,String> map = userServiceImpl.login(username, password);
		//ע�᲻�ɹ�--�ص�ע��ҳ�棬ͬʱЯ��������Ϣ
		if(map.containsKey("msg")) {
			System.out.println(map.get("msg"));
		}
		if(map.containsKey("erro")) {
			System.out.println(map.get("erro"));
		}
		if(map.containsKey("ticket")) {
			System.out.println(map.get("ticket"));
		}
		//ע��ɹ�ֱ���ض�����ҳ
		
		
		
	}
	
}
