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
	

	//孕妇获取自己的诊断报告列表
	@RequestMapping(value="/user/reports")
	public String getReport(Model model) {
		
		try {
			if(hostHolder.get()==null) {
				//没有登录
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
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	//获取自己的诊断报告详情
	@RequestMapping(value="/user/reports/{reportId}")
	public String getReportDetial(Model model,@PathVariable("reportId") int reportId) {
		try {
			if(hostHolder.get()==null) {
				//没有登录
				return "redirect:/reglogin";
			}
			int userId = hostHolder.get().getId();
			Report report = userServiceImpl.getReportDetail(reportId);
			if(userId != report.getUserId()) {
				//不是当前用户没有访问权限
				return "noaccess";
			}
			//是的话可以返回，三个方面内容
			//个人基本信息
			//诊断的参考数据
			//医生给出的诊断报告
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
			logger.error("获取报告出错"+e.getMessage());
			return "404";
		}
	}
	
	
	
	
	
	//用户获取自己的基本信息,同时医生也可以获取
	@RequestMapping(value="/user/getInfo",produces="text/html;charset=UTF-8")
	public String getInfo(Model model) {
		try {
			if(null==hostHolder.get()) {
				//没有登录，跳转到登陆页面
	            return"redirect:/reglogin";
			}
			//说明是用户自己看自己的
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
			logger.info("数据获取错误"+e.getMessage());
			//插入错误
			model.addAttribute("msg", "数据获取失败");
			//返回表单，重新填写提交，这个html用作资料填写表单
			return "erro";
		}	
	}
	
	
	//用户填写自己的信息
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
			//没有登录，跳转到登陆页面
            return"redirect:/reglogin";
		}
		try {
			GravidaInfo Info = new GravidaInfo();
			//登陆用户的id
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
				//System.out.println("更新成功");
				//说明插入正常
				//重定向到表单获取页面
				//System.out.println("redirect:/user/"+hostHolder.get().getId()+"/getInfo");
				return "redirect:/user/getInfo";
			}else {
				//插入错误
				model.addAttribute("msg", "插入错误");
				//返回表单信息界面，重新填写提交，这个html用作资料填写表单
				return "gravidaInfo";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("插入信息错误"+e.getMessage());
			//插入错误
			model.addAttribute("msg", "插入错误");
			//返回表单，重新填写提交，这个html用作资料填写表单
			return "gravidaInfo";
		}	
	}
	
	
	//用户的主页面：显示医生列表和自己绑定的医生
	@RequestMapping(value="/usermain",produces="text/html;charset=UTF-8")
	public String Usermain(Model model) {
		
        if (hostHolder.get() != null) {
        	
        	//进入用户首页,先找到自己绑定的医生
        	int userId = hostHolder.get().getId();
        	List<Integer> followeeIds = followServiceImpl.getFollowees(EntityType.ENTITY_DOCTER_BIND,userId, 0, 10);
        	//System.out.println("绑定的医生数："+followeeIds.size());
        	//这里不管有没有都加进入了model中，因此前端要判断一size来确定有没有邦定医生
            model.addAttribute("followees", getDoctersInfo(hostHolder.get().getId(), followeeIds));
           
            //找到所有的医生，剔除自己绑定的医生
            List<Map<String,Object>> docters = userServiceImpl.getAllDocter();
            List<ViewObject> vos = new ArrayList<>();
            outer:for(Map<String,Object> docter:docters) {
            	int id = Integer.parseInt(String.valueOf(docter.get("id")));
            	//去除已经绑定的医生
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
        	//没有登录，跳转到登陆页面
            return"redirect:/reglogin";
        }
	}
	
	 //获取关注医生的详细信息
	 private List<ViewObject> getDoctersInfo(int localUserId, List<Integer> userIds) {
	        List<ViewObject> docterInfos = new ArrayList<ViewObject>();
	        for (Integer uid : userIds) {
	        	Map<String,Object> map = userServiceImpl.getDocterBackById(uid);
	        	if(map.size()<=0) {
	        		continue;
	        	}
	            ViewObject vo = new ViewObject();
	            //医生的基本信息
	            vo.set("id", map.get("id"));
				vo.set("name", map.get("name"));
				vo.set("background", map.get("background"));
	            //获取医生被绑定这数量
	            vo.set("count", followServiceImpl.getFollowerCount(EntityType.ENTITY_DOCTER_BIND, uid));
	            docterInfos.add(vo);
	        }
	        return docterInfos;
	    }
	
	
}
