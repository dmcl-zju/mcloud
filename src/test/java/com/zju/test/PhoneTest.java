package com.zju.test;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.service.PhoneService;


/**
 * ����Service�ӿ�
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class PhoneTest {
	
	
	@Resource
	PhoneService phoneServiceImpl;
		
	@Test
	public void testDocter() {
		Map<String,String> map = phoneServiceImpl.sendMessage("18757576712");
		
		for(String key:map.keySet()) {
			System.out.println(key +"--"+map.get(key));
		}
		
	}

}
