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
 * 测试Service接口
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class GravidaInfoTest {
	
	//name指定要实现的实体类
	@Resource
	GravidaInfoMapper gravidaInfoMapper;
	
	
	
		
	@Test
	public void testDocter() {
		GravidaInfo Info = new GravidaInfo();
		Info.setUserId(12);
		Info.setName("张翼德");
		Info.setAge(23);
		Info.setWeight(72.5);
		Info.setHeight(185);
		Info.setPregnantTimes(1);
		Info.setWeeks(16);
		Info.setExpectedDate(new Date());
		//插入一条数据
		int res = gravidaInfoMapper.insGravidaInfo(Info);
		System.out.println(res);
		
		Info = gravidaInfoMapper.selGravidaInfo(12);
		
		System.out.println(Info);
		
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
