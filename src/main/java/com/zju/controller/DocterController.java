package com.zju.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zju.async.EventModel;
import com.zju.async.EventProducer;
import com.zju.async.EventType;
import com.zju.model.Comment;
import com.zju.model.EntityType;
import com.zju.model.FhrData;
import com.zju.model.GravidaInfo;
import com.zju.model.HostHolder;
import com.zju.model.Report;
import com.zju.model.User;
import com.zju.model.ViewObject;
import com.zju.service.DataService;
import com.zju.service.FollowService;
import com.zju.service.ReportService;
import com.zju.service.UserService;

@Controller
public class DocterController {
	Logger logger = Logger.getLogger(LoginController.class);
	
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	DataService dataServiceImpl;
	
	@Resource
	ReportService reportServiceImpl;
	
	@Resource
	EventProducer eventProducer;
	
	
	/**
	 * ҽ����ȡ���Լ����и��б�
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/docter/binders",produces="text/html;charset=UTF-8")
	public String getBinders(Model model) {
		User docter = hostHolder.get();
		if(null==docter) {
			//û�е�¼����ת����½ҳ��
            return "redirect:/reglogin";
		}else if(docter.getRole()!=2) {
			//˵������ҽ���û���û�з���Ȩ��
			return "noaccess";
		}else {
			//��ҽ����½�û�
			List<Integer> followerIds = followServiceImpl.getFollowers(EntityType.ENTITY_DOCTER_BIND, hostHolder.get().getId(), 0, 10);
	        model.addAttribute("followers", getUsersInfo(hostHolder.get().getId(), followerIds)); 
	        
	        return "binders";
		}
	}
	 private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
	        List<ViewObject> userInfos = new ArrayList<ViewObject>();
	        for (Integer uid : userIds) {
	            User user = userServiceImpl.getUserById(uid);
	            if (user == null) {
	                continue;
	            }
	            ViewObject vo = new ViewObject();
	            vo.set("user", user);
	            vo.set("commentCount", 0);
	            vo.set("followerCount", followServiceImpl.getFollowerCount(EntityType.ENTITY_USER, uid));
	            vo.set("followeeCount", followServiceImpl.getFolloweeCount(uid, EntityType.ENTITY_USER));
	            if (localUserId != 0) {
	                vo.set("followed", followServiceImpl.isFollower(localUserId, EntityType.ENTITY_USER, uid));
	            } else {
	                vo.set("followed", false);
	            }
	            userInfos.add(vo);
	        }
	        return userInfos;
	    }
	
	
	////ҽ����ȡ���Լ����и��Ļ�����Ϣ
	@RequestMapping(value="/docter/binders/{userId}/getInfo",produces="text/html;charset=UTF-8")
	public String getInfo(Model model,@PathVariable("userId") int userId) {
		try {
			User user = hostHolder.get();
			
			if(user==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			//ȷ����ҽ���û��Ͱѻ�����Ϣ����
			GravidaInfo Info = userServiceImpl.getGravidaInfo(userId);
			model.addAttribute("Info", Info);
			return "binderInfo";
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("���ݻ�ȡ����"+e.getMessage());
			//�������
			model.addAttribute("msg", "���ݻ�ȡʧ��");
			//���ر���������д�ύ�����html����������д��
			return "erro";
		}	
	}
	
	
	//ҽ����ȡ���Լ����и��������ʷ�б�
	@RequestMapping(value="/docter/binders/{userId}/reprots",produces="text/html;charset=UTF-8")
	public String getBindersReports(Model model,@PathVariable("userId") int userId) {
		try {
			
			User user = hostHolder.get();
			
			if(user==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			//����˵����ҽ���û�
			List<ViewObject> vos = new ArrayList<>();
			List<Report> reports = userServiceImpl.getUserReportList(userId);			
			for(Report report:reports) {
				ViewObject vo = new ViewObject();
				//�������ϱ����ϵ�ҽ������һ���ǵ�½��ҽ��
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
	
	
	//ҽ����ȡ���Լ����и�����ϱ�������
	@RequestMapping(value="/docter/binders/report/{reportId}")
	public String getBinderReportDetial(Model model,@PathVariable("reportId") int reportId) {
		try {
			
			User user = hostHolder.get();
			
			//�����֤--����ĵ�ʱ��Ҫ�ŵ��������н�����֤
			if(user==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			
			Report report = userServiceImpl.getReportDetail(reportId);
			if(null==report) {
				//û����ݱ���
				return "404";
			}
			//�ǵĻ����Է��أ�������������
			//���˻�����Ϣ
			//��ϵĲο�����
			//ҽ����������ϱ���
			ViewObject vo = new ViewObject();
			//�и�������Ϣ
			GravidaInfo info = userServiceImpl.getGravidaInfo(report.getUserId());
			int dataId = report.getDataId();
			//�������
			FhrData data = dataServiceImpl.getDataById(dataId);
			User docter = userServiceImpl.getDocterById(report.getDocterId());
			vo.set("doctername",docter.getName());
			vo.set("data", data);
			vo.set("report", report);
			vo.set("info", info);
			model.addAttribute("vo", vo);
			return "reportform";
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("��ȡ�������"+e.getMessage());
			return "404";
		}
	}
	
	
	//ҽ����ȡ����д����ϱ���
	@RequestMapping(value="/docter/pullreport/{dataId}")
	public String getUnfilledReport(Model model,@PathVariable("dataId") int dataId) {
		try {
			
			User user = hostHolder.get();
			
			//�����֤--����ĵ�ʱ��Ҫ�ŵ��������н�����֤
			if(user==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			
			//�ǵĻ�,����������������
			//���˻�����Ϣ
			//��ϵĲο�����
			ViewObject vo = new ViewObject();
			//�������
			FhrData data = dataServiceImpl.getDataById(dataId);
			//�и�������Ϣ
			GravidaInfo info = userServiceImpl.getGravidaInfo(data.getUserId());
			User docter = hostHolder.get();
			vo.set("doctername",docter.getName());
			vo.set("data", data);
			vo.set("info", info);
			vo.set("dataId", dataId);
			model.addAttribute("vo", vo);
			//���ύ��form��
			return "reportform";
			
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("��ȡ�������"+e.getMessage());
			return "404";
		}
	}
	
	
	
	//ҽ���ύ��ϱ���
	@RequestMapping(value="/docter/pushreport/{dataId}")
	public String addReport(Model model,@PathVariable("dataId") int dataId,
							@RequestParam("guidance") String guidance) {
		try {
			User user = hostHolder.get();
			
			//�����֤--����ĵ�ʱ��Ҫ�ŵ��������н�����֤
			if(user==null) {
				//û�е�¼
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			
			Report report = new Report();
			int userId = dataServiceImpl.getDataById(dataId).getUserId();
			report.setUserId(userId);
			report.setDataId(dataId);
			report.setDocterId(user.getId());
			report.setCreatedTime(new Date());
			report.setGuidance(guidance);
		
			//����ʹ�����Զ���������ID����ʽ����mapper����Կ�������ͨ�Ĳ��벻һ��
			int res = reportServiceImpl.addReport(report);
			if(res>0) {
				//�������ݳɹ����ض����û������б�
				
				
				//�����첽�¼���֪ͨ�û��鿴
				EventModel eventModel = new EventModel();
				eventModel.setActorId(hostHolder.get().getId()).setEntityOwnerId(userId).setExts("reportId", String.valueOf(report.getId())).
				setEventType(EventType.NEWREPORT);
				eventProducer.fireEvent(eventModel);	
				
				return "redirect:/docter/binders/"+userId+"/reprots";
			}else {
				//����ʧ��,�ض����ȥ������ȡ��д
				model.addAttribute("erro", "����ʧ��");
				return "redirect:/docter/pullreport/"+dataId;
			}
		
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("��ȡ�������"+e.getMessage());
			return "404";
		}
	}
	
	
	
	
	
	
	
	
	
	

}
