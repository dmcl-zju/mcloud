package com.zju.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zju.service.UserService;


@Controller
public class LoginController {

	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	UserService userServiceImpl;
	
	@RequestMapping({"/reg/"})
	public String regist(Model model,
						 @RequestParam("username") String username,
						 @RequestParam("password") String password,
						 @RequestParam(value="role",defaultValue="1") int role,
						 @RequestParam(value="next",required=false) String next,
						 @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
						 HttpServletResponse response) {
		
		try {
			Map<String,String> map = userServiceImpl.regist(username, password,role);
			//��½�ɹ�����ticket�ŵ�cookie��
			if(map.containsKey("ticket")) {
				Cookie cookie = new Cookie("ticket", map.get("ticket"));
				cookie.setPath("/");
				if(rememberme) {
					cookie.setMaxAge(3600*24*5);
				}
				response.addCookie(cookie);
				//���ص�¼ǰ�ĵط�ȥ
				if(!StringUtils.isBlank(next)) {
					return "redirect:"+next;
				}
				
				if(role==1) {
					//��ת���û���ҳ
					return "redirect:/usermain";
					
				}else if(role==2){
					//�ض���ҽ����ҳ
					return "docterMain";
				}else {
					//��½�ɹ�ֱ���ض�����ҳ
					return "redirect:/";
				}				
			}else {
				if(map.containsKey("erro")) {
					logger.info(map.get("erro"));
					model.addAttribute("msg", "ϵͳ��æ");
					//��Ϊhtml�ļ���Ϊlogin
					return "login";
				}
				//ע�᲻�ɹ����ɹ�--�ص���½ҳ�棬ͬʱЯ��������Ϣ
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("ע���쳣"+e.getMessage());
			return "login";
		}
		
	}
	
	@RequestMapping({"/login"})
	public String login(Model model,
						 @RequestParam("username") String username,
						 @RequestParam("password") String password,
						 @RequestParam(value="role",defaultValue="1") int role,
						 @RequestParam(value="next",required=false) String next,
						 @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
						 HttpServletResponse response) {
		
		try {
			Map<String,String> map = userServiceImpl.login(username, password,role);
			//��½�ɹ�����ticket�ŵ�cookie��
			if(map.containsKey("ticket")) {
				Cookie cookie = new Cookie("ticket", map.get("ticket"));
				cookie.setPath("/");
				if(rememberme) {
					cookie.setMaxAge(3600*24*5);
				}
				response.addCookie(cookie);
				//���ص�¼ǰ�ĵط�ȥ
				if(!StringUtils.isBlank(next)) {
					return "redirect:"+next;
				}
				
				
				if(role==1) {
					//��ת���û���ҳ
					return "redirect:/usermain";
					
				}else if(role==2){
					//�ض���ҽ����ҳ
					return "docterMain";
				}else {
					//��½�ɹ�ֱ���ض�����ҳ
					return "redirect:/";
				}		
			}else {
				//��½���ɹ�--�ص���½ҳ�棬ͬʱЯ��������Ϣ
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("ע���쳣"+e.getMessage());
			return "login";
		}
		
	}

	@RequestMapping({"/logout"})
	public String logout(@CookieValue("ticket") String ticket) {
		userServiceImpl.logout(ticket);
		//�ض�����ҳ
		return "redirect:/";
	}

	
	
	
	//������½ע�����ҳ��һ������ת����̨
	@RequestMapping({"/reglogin"})
	public String loginreg(Model model,@RequestParam(value="next",required=false) String next) {
		model.addAttribute("next", next);
		return "login";
	}
}
