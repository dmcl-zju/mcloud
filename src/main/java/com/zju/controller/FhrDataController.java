package com.zju.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.FhrData;
import com.zju.model.HostHolder;
import com.zju.service.DataService;
import com.zju.utils.WendaUtil;

@Controller
public class FhrDataController {
	
	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	DataService dataServiceImpl;
	
	@Resource
	EventProducer eventProducer;
	
	
	@RequestMapping({"/uploadData/{n}"})
	@ResponseBody
	public String reciveData(@PathVariable("n") int n) {
		
		
		if(hostHolder.get()==null) {
			//û�е�¼
			return "redirect:/reglogin";
		}
		
		try {
			//�������������õ�·��
			String path = "d:test--"+n;
			FhrData fhrData = new FhrData();
			fhrData.setData(path);
			fhrData.setUserId(hostHolder.get().getId());
			fhrData.setUpTime(new Date());
			fhrData.setmTime(new Date());
			dataServiceImpl.addData(fhrData);
			//������������첽���н��д���
			
			EventModel model = new EventModel();
			model.setEventType(EventType.GETDATA).setExts("dataId", String.valueOf(fhrData.getId()));
			eventProducer.fireEvent(model);
			
			
			return WendaUtil.getJSONString(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("��ȡ���ݳ���"+e.getMessage());
			return WendaUtil.getJSONString(1);
		}
	}
	
}
