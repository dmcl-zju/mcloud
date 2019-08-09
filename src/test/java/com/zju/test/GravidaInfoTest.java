package com.zju.test;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.mapper.GravidaInfoMapper;
import com.zju.model.GravidaInfo;


/**
 * ����Service�ӿ�
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class GravidaInfoTest {
	
	//nameָ��Ҫʵ�ֵ�ʵ����
	@Resource
	GravidaInfoMapper gravidaInfoMapper;
	
	
	
		
	@Test
	public void testDocter() {
		GravidaInfo Info = new GravidaInfo();
		Info.setUserId(12);
		Info.setName("�����");
		Info.setAge(23);
		Info.setWeight(72.5);
		Info.setHeight(185);
		Info.setPregnantTimes(1);
		Info.setWeeks(16);
		Info.setExpectedDate(new Date());
		//����һ������
		int res = gravidaInfoMapper.insGravidaInfo(Info);
		System.out.println(res);
		
		Info = gravidaInfoMapper.selGravidaInfo(12);
		
		System.out.println(Info);
		
	}
	
	
	/**
	 * ��ȡspring�е�bean����
	 * @return
	 */
	public void getBeans() {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
		String[] strs = context.getBeanDefinitionNames();
		for(String str:strs) {
			System.out.println("bean���ƣ�  "+str);
		}
	}
	
}
