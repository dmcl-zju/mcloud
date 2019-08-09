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
	 * 医生获取绑定自己的孕妇列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/docter/binders",produces="text/html;charset=UTF-8")
	public String getBinders(Model model) {
		User docter = hostHolder.get();
		if(null==docter) {
			//没有登录，跳转到登陆页面
            return "redirect:/reglogin";
		}else if(docter.getRole()!=2) {
			//说明不是医生用户，没有访问权限
			return "noaccess";
		}else {
			//是医生登陆用户
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
	
	
	////医生获取绑定自己的孕妇的基本信息
	@RequestMapping(value="/docter/binders/{userId}/getInfo",produces="text/html;charset=UTF-8")
	public String getInfo(Model model,@PathVariable("userId") int userId) {
		try {
			User user = hostHolder.get();
			
			if(user==null) {
				//没有登录
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			//确定是医生用户就把基本信息返回
			GravidaInfo Info = userServiceImpl.getGravidaInfo(userId);
			model.addAttribute("Info", Info);
			return "binderInfo";
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("数据获取错误"+e.getMessage());
			//插入错误
			model.addAttribute("msg", "数据获取失败");
			//返回表单，重新填写提交，这个html用作资料填写表单
			return "erro";
		}	
	}
	
	
	//医生获取绑定自己的孕妇的诊断历史列表
	@RequestMapping(value="/docter/binders/{userId}/reprots",produces="text/html;charset=UTF-8")
	public String getBindersReports(Model model,@PathVariable("userId") int userId) {
		try {
			
			User user = hostHolder.get();
			
			if(user==null) {
				//没有登录
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			//这里说明是医生用户
			List<ViewObject> vos = new ArrayList<>();
			List<Report> reports = userServiceImpl.getUserReportList(userId);			
			for(Report report:reports) {
				ViewObject vo = new ViewObject();
				//这个是诊断报告上的医生，不一定是登陆的医生
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
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	//医生获取绑定自己的孕妇的诊断报告详情
	@RequestMapping(value="/docter/binders/report/{reportId}")
	public String getBinderReportDetial(Model model,@PathVariable("reportId") int reportId) {
		try {
			
			User user = hostHolder.get();
			
			//身份验证--这里的到时候要放到拦截器中进行验证
			if(user==null) {
				//没有登录
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			
			Report report = userServiceImpl.getReportDetail(reportId);
			if(null==report) {
				//没有这份报告
				return "404";
			}
			//是的话可以返回，三个方面内容
			//个人基本信息
			//诊断的参考数据
			//医生给出的诊断报告
			ViewObject vo = new ViewObject();
			//孕妇个人信息
			GravidaInfo info = userServiceImpl.getGravidaInfo(report.getUserId());
			int dataId = report.getDataId();
			//诊断数据
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
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	//医生获取待填写的诊断报告
	@RequestMapping(value="/docter/pullreport/{dataId}")
	public String getUnfilledReport(Model model,@PathVariable("dataId") int dataId) {
		try {
			
			User user = hostHolder.get();
			
			//身份验证--这里的到时候要放到拦截器中进行验证
			if(user==null) {
				//没有登录
				return "redirect:/reglogin";
			}else if(user.getRole() != 2){
				return "noaccess";
			}
			
			//是的话,返回两个方面内容
			//个人基本信息
			//诊断的参考数据
			ViewObject vo = new ViewObject();
			//诊断数据
			FhrData data = dataServiceImpl.getDataById(dataId);
			//孕妇个人信息
			GravidaInfo info = userServiceImpl.getGravidaInfo(data.getUserId());
			User docter = hostHolder.get();
			vo.set("doctername",docter.getName());
			vo.set("data", data);
			vo.set("info", info);
			vo.set("dataId", dataId);
			model.addAttribute("vo", vo);
			//可提交的form表单
			return "reportform";
			
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	
	//医生提交诊断报告
	@RequestMapping(value="/docter/pushreport/{dataId}")
	public String addReport(Model model,@PathVariable("dataId") int dataId,
							@RequestParam("guidance") String guidance) {
		try {
			User user = hostHolder.get();
			
			//身份验证--这里的到时候要放到拦截器中进行验证
			if(user==null) {
				//没有登录
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
		
			//这里使用了自动返回自增ID的形式，在mapper层可以看到和普通的插入不一样
			int res = reportServiceImpl.addReport(report);
			if(res>0) {
				//插入数据成功，重定向到用户报告列表
				
				
				//生成异步事件，通知用户查看
				EventModel eventModel = new EventModel();
				eventModel.setActorId(hostHolder.get().getId()).setEntityOwnerId(userId).setExts("reportId", String.valueOf(report.getId())).
				setEventType(EventType.NEWREPORT);
				eventProducer.fireEvent(eventModel);	
				
				return "redirect:/docter/binders/"+userId+"/reprots";
			}else {
				//插入失败,重定向回去重新拉取填写
				model.addAttribute("erro", "插入失败");
				return "redirect:/docter/pullreport/"+dataId;
			}
		
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	
	
	
	
	
	
	
	

}
