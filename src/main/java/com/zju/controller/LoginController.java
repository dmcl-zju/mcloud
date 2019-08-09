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
	
	
	//�����ȡ�ֻ���֤��,ע���ʱ��������Ƿ�ע�����û�вŷ�
	//value="/docter/binders",produces="text/html;charset=UTF-8"
	@RequestMapping(value="/regcode",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getRegCode(@RequestParam(value="phoneNum") String phoneNum,
			 				 @RequestParam(value="role",defaultValue="1") int role) {
		User user = null;
		//�и�ע��
		if(role==1) {
			user = userServiceImpl.getUserByPhoneNum(phoneNum);
		}else if(role==2){
			user = userServiceImpl.getDocterByPhoneNum(phoneNum);
		}else {
			return WendaUtil.getJSONString(1, "��ɫ�쳣");
		}
	
		if(user==null) {
			Map<String,String> map = phoneServiceImpl.sendMessage(phoneNum);
			if(map.get("code").equals("0")) {
				//���ŷ��ͳɹ�
				return WendaUtil.getJSONString(0);
			}else {
				//���ŷ���ʧ��
				String msg = map.get("msg");
				//System.out.println("���ŷ���ʧ�ܣ�"+msg);
				return WendaUtil.getJSONString(1,"���ŷ���ʧ�ܣ�"+msg);
			}
		}else {
			return WendaUtil.getJSONString(1, "�ֻ����Ѿ�ע��");
		}
	}

	
	//�����ȡ�ֻ���֤�룬��¼��ʱ��У�����ǲ�����ע�ᣬ�вŷ�
	@RequestMapping(value="/logincode",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getLoginCode(@RequestParam(value="phoneNum") String phoneNum,
			                   @RequestParam(value="role",defaultValue="1") int role) {
		
		User user = null;
		//�и�ע��
		if(role==1) {
			user = userServiceImpl.getUserByPhoneNum(phoneNum);
		}else if(role==2){
			user = userServiceImpl.getDocterByPhoneNum(phoneNum);
		}else {
			return WendaUtil.getJSONString(1, "��ɫ�쳣");
		}
	
		
		if(user!=null) {
			Map<String,String> map = phoneServiceImpl.sendMessage(phoneNum);
			if(map.get("code").equals("0")) {
				//���ŷ��ͳɹ�
				return WendaUtil.getJSONString(0);
			}else {
				//���ŷ���ʧ��
				String msg = map.get("msg");
				//System.out.println("���ŷ���ʧ�ܣ�"+msg);
				return WendaUtil.getJSONString(1,"���ŷ���ʧ�ܣ�"+msg);
			}
		}else {
			return WendaUtil.getJSONString(1, "�ֻ��Ż�δע��");
		}
		
		//return WendaUtil.getJSONString(1, "���Ż�ȡ�쳣");
	}
	
	
	//ע����뾭����֤����֤���ܳɹ�
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
	
	//ͨ�������¼�������ж����ֻ��Ż����û���
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
	
	//ͨ��������֤���¼
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
