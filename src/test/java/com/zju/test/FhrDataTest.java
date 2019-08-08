package com.zju.test;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.mapper.FeedMapper;
import com.zju.mapper.FhrDataMapper;
import com.zju.mapper.ReportMapper;
import com.zju.model.Feed;
import com.zju.model.Report;


/**
 * ����Service�ӿ�
 * @author lin
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ���λ��
@ContextConfiguration("classpath:/applicationContext.xml")
public class FhrDataTest {
	
	
	@Resource
	FhrDataMapper fhrDataMapper;
	
	@Resource
	ReportMapper reportMapper;
	
	@Resource
	FeedMapper feedMapper;
		
	@Test
	public void testDocter() {
		
		//��������
//		FhrData fhrData = new FhrData();
//		//�û���
//		fhrData.setUserId(14);
//		fhrData.setmTime(new Date());
//		fhrData.setUpTime(new Date());
//		fhrData.setData("D:fhrdata5");
//		//������Ĭ��ֵ����
//		
//		int res = fhrDataMapper.insFhrDataAll(fhrData);
//		System.out.println(res);
//		if(res>0) {
//			List<FhrData> list = fhrDataMapper.selByUserId(14);
//			for(FhrData data:list) {
//				System.out.println(data);
//			}
//		}
	
		
		//���������Ӳ���ֵ
//		fhrData.setId(1);
//		//������������
//		fhrData.setFhrBase(162.3);
//		fhrData.setFhrStv(25.2);
//		fhrData.setUcBase(16.2);
//		fhrData.setFhrDown(3);
//		fhrData.setFhrUp(5);
//		fhrData.setScore(8);
//		
//		int res = fhrDataMapper.updPassword(fhrData);
		
		
		
		//ͨ���û����ͨ��id�鵥��
//		List<FhrData> list = fhrDataMapper.selByUserId(13);
//		for(FhrData data:list) {
//			System.out.println(data);
//		}	
//		System.out.println(fhrDataMapper.selById(1));
		

/////////////////////////////////////////////////////������ϱ���/////////////////////////////////////////////////////
		Report report = new Report();
		report.setDataId(6);
		report.setUserId(15);
		report.setDocterId(1);
		report.setGuidance("��Ͻ��666");
		report.setCreatedTime(new Date());
		
		int res = reportMapper.insReport(report);
		System.out.println(report.getId());
		
		
		
		
		
		
//		System.out.println(reportMapper.selById(1));
//		System.out.println(reportMapper.selByUserId(13));
//		System.out.println(reportMapper.selByDocterId(1));
//		System.out.println(reportMapper.selByDocterAndUser(1, 13));
//		
//		reportMapper.updGuidence("��Ͻ�����쳣", new Date(),1);
//		
//		
//		System.out.println(reportMapper.selById(1));
		
		
		
		
		
		
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
