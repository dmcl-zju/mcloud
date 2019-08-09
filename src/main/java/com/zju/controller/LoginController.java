package com.zju.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.zju.model.User;
import com.zju.service.PhoneService;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;


@Controller
public class LoginController {

	Logger logger = Logger.getLogger(LoginController.class);
	
	@Resource
	UserService userServiceImpl;
	
	@Resource
	PhoneService phoneServiceImpl;
	
	
	//点击获取手机验证码,注册的时候检验下是否注册过，没有才发
	//value="/docter/binders",produces="text/html;charset=UTF-8"
	@RequestMapping(value="/regcode",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getRegCode(@RequestParam(value="phoneNum") String phoneNum,
			 				 @RequestParam(value="role",defaultValue="1") int role) {
		User user = null;
		//孕妇注册
		if(role==1) {
			user = userServiceImpl.getUserByPhoneNum(phoneNum);
		}else if(role==2){
			user = userServiceImpl.getDocterByPhoneNum(phoneNum);
		}else {
			return WendaUtil.getJSONString(1, "角色异常");
		}
	
		if(user==null) {
			Map<String,String> map = phoneServiceImpl.sendMessage(phoneNum);
			if(map.get("code").equals("0")) {
				//短信发送成功
				return WendaUtil.getJSONString(0);
			}else {
				//短信发送失败
				String msg = map.get("msg");
				//System.out.println("短信发送失败："+msg);
				return WendaUtil.getJSONString(1,"短信发送失败："+msg);
			}
		}else {
			return WendaUtil.getJSONString(1, "手机号已经注册");
		}
	}

	
	//点击获取手机验证码，登录的时候校验下是不是有注册，有才发
	@RequestMapping(value="/logincode",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getLoginCode(@RequestParam(value="phoneNum") String phoneNum,
			                   @RequestParam(value="role",defaultValue="1") int role) {
		
		User user = null;
		//孕妇注册
		if(role==1) {
			user = userServiceImpl.getUserByPhoneNum(phoneNum);
		}else if(role==2){
			user = userServiceImpl.getDocterByPhoneNum(phoneNum);
		}else {
			return WendaUtil.getJSONString(1, "角色异常");
		}
	
		
		if(user!=null) {
			Map<String,String> map = phoneServiceImpl.sendMessage(phoneNum);
			if(map.get("code").equals("0")) {
				//短信发送成功
				return WendaUtil.getJSONString(0);
			}else {
				//短信发送失败
				String msg = map.get("msg");
				//System.out.println("短信发送失败："+msg);
				return WendaUtil.getJSONString(1,"短信发送失败："+msg);
			}
		}else {
			return WendaUtil.getJSONString(1, "手机号还未注册");
		}
		
		//return WendaUtil.getJSONString(1, "短信获取异常");
	}
	
	
	//注册必须经过验证码验证才能成功
	@RequestMapping({"/reg"})
	public String regist(Model model,
						 @RequestParam("username") String username,
						 @RequestParam("password") String password,
						 @RequestParam(value="phoneNum") String phoneNum,
						 @RequestParam(value="code") String code,
						 @RequestParam(value="role",defaultValue="1") int role,
						 @RequestParam(value="next",required=false) String next,
						 @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
						 HttpServletResponse response) {
		try {
			Map<String,String> map = userServiceImpl.regist(username, password,role,phoneNum,code);
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
	
	//通过密码登录，首先判断是手机号还是用户名
	@RequestMapping({"/login"})
	public String loginByPassWord(Model model,
						 @RequestParam("username") String username,
						 @RequestParam("password") String password,
						 @RequestParam(value="role",defaultValue="1") int role,
						 @RequestParam(value="next",required=false) String next,
						 @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
						 HttpServletResponse response) {
		
		try {
			Map<String,String> map = userServiceImpl.loginByPassWord(username, password,role);
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
	
	//通过短信验证码登录
	@RequestMapping({"/login/code"})
	public String loginByCode(Model model,
						 @RequestParam(value="phoneNum") String phoneNum,
						 @RequestParam(value="code") String code,
						 @RequestParam(value="role",defaultValue="1") int role,
						 @RequestParam(value="next",required=false) String next,
						 @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
						 HttpServletResponse response) {
		
		try {
			Map<String,String> map = userServiceImpl.loginByCode(phoneNum, code,role);
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
