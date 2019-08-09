package com.zju.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zju.model.EntityType;
import com.zju.model.FhrData;
import com.zju.model.GravidaInfo;
import com.zju.model.HostHolder;
import com.zju.model.Report;
import com.zju.model.User;
import com.zju.model.ViewObject;
import com.zju.service.DataService;
import com.zju.service.FollowService;
import com.zju.service.UserService;

@Controller
public class UserController {

	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	DataService dataServiceImpl;
	

	//�и���ȡ�Լ�����ϱ����б�
	@RequestMapping(value="/user/reports")
	public String getReport(Model model) {
		
		try {
			if(hostHolder.get()==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}
			int userId = hostHolder.get().getId();
			
			List<ViewObject> vos = new ArrayList<>();
			List<Report> reports = userServiceImpl.getUserReportList(userId);
			
			for(Report report:reports) {
				
				ViewObject vo = new ViewObject();
				User docter = userServiceImpl.getDocterById(report.getDocterId());
				vo.set("doctername",docter.getName());
				vo.set("time",report.getCreatedTime());
				vo.set("reportId", report.getId());
				vos.add(vo);
			}
			model.addAttribute("vos", vos);			
			return "reports";
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("��ȡ�������"+e.getMessage());
			return "404";
		}
	}
	
	
	//��ȡ�Լ�����ϱ�������
	@RequestMapping(value="/user/reports/{reportId}")
	public String getReportDetial(Model model,@PathVariable("reportId") int reportId) {
		try {
			if(hostHolder.get()==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}
			int userId = hostHolder.get().getId();
			Report report = userServiceImpl.getReportDetail(reportId);
			if(userId != report.getUserId()) {
				//���ǵ�ǰ�û�û�з���Ȩ��
				return "noaccess";
			}
			//�ǵĻ����Է��أ�������������
			//���˻�����Ϣ
			//��ϵĲο�����
			//ҽ����������ϱ���
			ViewObject vo = new ViewObject();
			
			GravidaInfo info = userServiceImpl.getGravidaInfo(userId);
			int dataId = report.getDataId();
			FhrData data = dataServiceImpl.getDataById(dataId);
			User docter = userServiceImpl.getDocterById(report.getDocterId());
			vo.set("doctername",docter.getName());
			vo.set("data", data);
			vo.set("report", report);
			vo.set("info", info);
			model.addAttribute("vo", vo);
			return "reportetails";
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("��ȡ�������"+e.getMessage());
			return "404";
		}
	}
	
	
	
	
	
	//�û���ȡ�Լ��Ļ�����Ϣ,ͬʱҽ��Ҳ���Ի�ȡ
	@RequestMapping(value="/user/getInfo",produces="text/html;charset=UTF-8")
	public String getInfo(Model model) {
		try {
			if(null==hostHolder.get()) {
				//û�е�¼����ת����½ҳ��
	            return"redirect:/reglogin";
			}
			//˵�����û��Լ����Լ���
			GravidaInfo Info = userServiceImpl.getGravidaInfo(hostHolder.get().getId());
			if(Info==null) {
				model.addAttribute("code", "1");
				return "gravidaInfo";
			}
			model.addAttribute("Info", Info);
			model.addAttribute("code", "1");
			return "gravidaInfo";
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("���ݻ�ȡ����"+e.getMessage());
			//�������
			model.addAttribute("msg", "���ݻ�ȡʧ��");
			//���ر���������д�ύ�����html����������д��
			return "erro";
		}	
	}
	
	
	//�û���д�Լ�����Ϣ
	@RequestMapping(value="/user/updateInfo",produces="text/html;charset=UTF-8")
	public String updateInfo(Model model,
							 @RequestParam("name") String name,
							 @RequestParam("age") int age,
							 @RequestParam("weight") double weight,
							 @RequestParam("height") double height,
							 @RequestParam("times") int times,
							 @RequestParam("weeks") int weeks,
							 @RequestParam("expectedDate") String expectedDate) {
		if(null==hostHolder.get()) {
			//û�е�¼����ת����½ҳ��
            return"redirect:/reglogin";
		}
		try {
			GravidaInfo Info = new GravidaInfo();
			//��½�û���id
			Info.setUserId(hostHolder.get().getId());
			Info.setName(name);
			Info.setAge(age);
			Info.setWeight(weight);
			Info.setHeight(height);
			Info.setPregnantTimes(times);
			Info.setWeeks(weeks);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(expectedDate);
			Info.setExpectedDate(date);
			System.out.println(Info);
			int res = userServiceImpl.setInfo(Info);
			if(res>0) {
				//System.out.println("���³ɹ�");
				//˵����������
				//�ض��򵽱���ȡҳ��
				//System.out.println("redirect:/user/"+hostHolder.get().getId()+"/getInfo");
				return "redirect:/user/getInfo";
			}else {
				//�������
				model.addAttribute("msg", "�������");
				//���ر���Ϣ���棬������д�ύ�����html����������д��
				return "gravidaInfo";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("������Ϣ����"+e.getMessage());
			//�������
			model.addAttribute("msg", "�������");
			//���ر���������д�ύ�����html����������д��
			return "gravidaInfo";
		}	
	}
	
	
	//�û�����ҳ�棺��ʾҽ���б���Լ��󶨵�ҽ��
	@RequestMapping(value="/usermain",produces="text/html;charset=UTF-8")
	public String Usermain(Model model) {
		
        if (hostHolder.get() != null) {
        	
        	//�����û���ҳ,���ҵ��Լ��󶨵�ҽ��
        	int userId = hostHolder.get().getId();
        	List<Integer> followeeIds = followServiceImpl.getFollowees(EntityType.ENTITY_DOCTER_BIND,userId, 0, 10);
        	//System.out.println("�󶨵�ҽ������"+followeeIds.size());
        	//���ﲻ����û�ж��ӽ�����model�У����ǰ��Ҫ�ж�һsize��ȷ����û�аҽ��
            model.addAttribute("followees", getDoctersInfo(hostHolder.get().getId(), followeeIds));
           
            //�ҵ����е�ҽ�����޳��Լ��󶨵�ҽ��
            List<Map<String,Object>> docters = userServiceImpl.getAllDocter();
            List<ViewObject> vos = new ArrayList<>();
            outer:for(Map<String,Object> docter:docters) {
            	int id = Integer.parseInt(String.valueOf(docter.get("id")));
            	//ȥ���Ѿ��󶨵�ҽ��
            	for(int i:followeeIds) {
            		if(id==i) {
            			continue outer;
            		}
            	}
    			ViewObject vo = new ViewObject();
    			long count = followServiceImpl.getFollowerCount(EntityType.ENTITY_DOCTER_BIND, id);
    			vo.set("count", count);
    			vo.set("id", id);
    			vo.set("name", docter.get("name"));
    			vo.set("background", docter.get("background"));
    			vos.add(vo);
    		}
    		model.addAttribute("vos", vos);
    		return "userMain";
        } else {
        	//û�е�¼����ת����½ҳ��
            return"redirect:/reglogin";
        }
	}
	
	 //��ȡ��עҽ������ϸ��Ϣ
	 private List<ViewObject> getDoctersInfo(int localUserId, List<Integer> userIds) {
	        List<ViewObject> docterInfos = new ArrayList<ViewObject>();
	        for (Integer uid : userIds) {
	        	Map<String,Object> map = userServiceImpl.getDocterBackById(uid);
	        	if(map.size()<=0) {
	        		continue;
	        	}
	            ViewObject vo = new ViewObject();
	            //ҽ���Ļ�����Ϣ
	            vo.set("id", map.get("id"));
				vo.set("name", map.get("name"));
				vo.set("background", map.get("background"));
	            //��ȡҽ������������
	            vo.set("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_DOCTER_BIND, uid));
	            docterInfos.add(vo);
	        }
	        return docterInfos;
	    }
	
	
}
