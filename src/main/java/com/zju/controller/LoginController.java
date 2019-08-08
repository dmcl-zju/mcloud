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
			//登陆成功，将ticket放到cookie中
			if(map.containsKey("ticket")) {
				Cookie cookie = new Cookie("ticket", map.get("ticket"));
				cookie.setPath("/");
				if(rememberme) {
					cookie.setMaxAge(3600*24*5);
				}
				response.addCookie(cookie);
				//调回登录前的地方去
				if(!StringUtils.isBlank(next)) {
					return "redirect:"+next;
				}
				
				if(role==1) {
					//跳转到用户首页
					return "redirect:/usermain";
					
				}else if(role==2){
					//重定向到医生首页
					return "docterMain";
				}else {
					//登陆成功直接重定向到主页
					return "redirect:/";
				}				
			}else {
				if(map.containsKey("erro")) {
					logger.info(map.get("erro"));
					model.addAttribute("msg", "系统繁忙");
					//因为html文件名为login
					return "login";
				}
				//注册不成功不成功--回到登陆页面，同时携带错误信息
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("注册异常"+e.getMessage());
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
			//登陆成功，将ticket放到cookie中
			if(map.containsKey("ticket")) {
				Cookie cookie = new Cookie("ticket", map.get("ticket"));
				cookie.setPath("/");
				if(rememberme) {
					cookie.setMaxAge(3600*24*5);
				}
				response.addCookie(cookie);
				//调回登录前的地方去
				if(!StringUtils.isBlank(next)) {
					return "redirect:"+next;
				}
				
				
				if(role==1) {
					//跳转到用户首页
					return "redirect:/usermain";
					
				}else if(role==2){
					//重定向到医生首页
					return "docterMain";
				}else {
					//登陆成功直接重定向到主页
					return "redirect:/";
				}		
			}else {
				//登陆不成功--回到登陆页面，同时携带错误信息
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("注册异常"+e.getMessage());
			return "login";
		}
		
	}

	@RequestMapping({"/logout"})
	public String logout(@CookieValue("ticket") String ticket) {
		userServiceImpl.logout(ticket);
		//重定向到首页
		return "redirect:/";
	}

	
	
	
	//给出登陆注册的主页面一个个跳转控制台
	@RequestMapping({"/reglogin"})
	public String loginreg(Model model,@RequestParam(value="next",required=false) String next) {
		model.addAttribute("next", next);
		return "login";
	}
}
