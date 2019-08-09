package com.zju.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zju.mapper.DocterMapper;
import com.zju.mapper.GravidaInfoMapper;
import com.zju.mapper.ReportMapper;
import com.zju.mapper.TicketMapper;
import com.zju.mapper.UserMapper;
import com.zju.model.GravidaInfo;
import com.zju.model.Report;
import com.zju.model.Ticket;
import com.zju.model.User;
import com.zju.service.UserService;
import com.zju.utils.JedisAdapter;
import com.zju.utils.Rediskeyutil;
import com.zju.utils.WendaUtil;


@Service()
public class UserServiceImpl implements UserService{
	
	@Resource
	UserMapper userMapper;
	
	@Resource
	DocterMapper docterMapper;
	
	@Resource
	TicketMapper ticketMapper;
	
	@Resource
	GravidaInfoMapper gravidaInfoMapper;
	
	@Resource 
	ReportMapper reportMapper;
	
	@Resource
	JedisAdapter jedisAdapter; 
	

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		return userMapper.selById(id);
	}

	@Override
	public Map<String, String> regist(String username, String password,int role,String phoneNum,String code) {
		
		Random random = new Random();
		Map<String,String> map = new HashMap<>();
		//注册的简单验证
		if(StringUtils.isBlank(username)) {
			map.put("msg","用户名不能为空");
			return map;
		}
		
		if(StringUtils.isBlank(password)) {
			map.put("msg","密码不能为空");
			return map;
		}
		
		if(StringUtils.isBlank(phoneNum)) {
			map.put("msg","手机号不能为空");
			return map;
		}
		
		if(StringUtils.isBlank(code)) {
			map.put("msg","验证码不能为空");
			return map;
		}
		
		if(role==1) {
			//孕妇注册
			User user = null;
			//进行手机号重复验证
			user = userMapper.selByPhoneNum(phoneNum);
			if(user!=null) {
				map.put("msg", "手机号已被注册");
				return map;
			}
			//验证码检验
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//验证通过
				map.put("msg", "无效验证码");
				return map;
			}
			
			//用户名重复验证
			user = userMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "用户名已经存在");
				return map;
			}
			
			//进行注册
			user = new User();
			//增加角色
			user.setRole(role);
			//加入手机号
			user.setPhoneNum(phoneNum);
			
			user.setName(username);
			//随机给的头像
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			//生成随机字符做salt
			String salt = UUID.randomUUID().toString().substring(0, 5);
			user.setSalt(salt);
			//原始密码和salt相加后再进行MD5加密
			user.setPassword(WendaUtil.MD5(password+salt));
			
			int res = userMapper.insUser(user);
			if(res<1) {
				map.put("erro","数据库插入错误");
				return map;
			}
			//注册成功---向上层抛出ticket
			//int userId = userMapper.selByName(username).getId();
			//String ticket = addTicket(userId,role);
			user = docterMapper.selByName(username);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//医生注册
			User user = null;
			//手机号重复验证
			user = docterMapper.selByPhoneNum(phoneNum);
			if(user!=null) {
				map.put("msg", "手机号已被注册");
				return map;
			}
			//验证码检验
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//验证不通过
				map.put("msg", "无效验证码");
				return map;
			}
		
			user = docterMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "用户名已经存在");
				return map;
			}
			
			//进行注册
			user = new User();
			//增加角色
			user.setRole(role);
			//增加手机
			user.setPhoneNum(phoneNum);
			user.setName(username);
			//随机给的头像
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			//生成随机字符做salt
			String salt = UUID.randomUUID().toString().substring(0, 5);
			user.setSalt(salt);
			//原始密码和salt相加后再进行MD5加密
			user.setPassword(WendaUtil.MD5(password+salt));
			int res = docterMapper.insUser(user);
			if(res<1) {
				System.out.println("医生注册失败");
				map.put("erro","数据库插入错误");
				return map;
			}
			//注册成功---向上层抛出ticket
			//String ticket = addTicket(docterMapper.selByName(username).getId(),role);
			
			user = docterMapper.selByName(username);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else {
			map.put("msg","角色错误");
			return map;
		}
	}
	
	//用户登录---验证码
	@Override
	public Map<String, String> loginByCode(String phoneNum, String code, int role) {
		// TODO Auto-generated method stub
		
		Map<String,String> map = new HashMap<>();
		//注册的简单验证
		if(StringUtils.isBlank(phoneNum)) {
			map.put("msg","手机号不能为空");
			return map;
		}
		if(StringUtils.isBlank(code)) {
			map.put("msg","验证码不能为空");
			return map;
		}
		if(role==1) {
			User user = null;
			//进行手机号重复验证
			user = userMapper.selByPhoneNum(phoneNum);
			if(user==null) {
				map.put("msg", "手机号还未注册");
				return map;
			}
			//验证码检验
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//验证无法通过
				map.put("msg", "无效验证码");
				return map;
			}
			//登陆成功---向上层抛出ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;	
		}else if(role==2) {
			User user = null;
			//进行手机号重复验证
			user = docterMapper.selByPhoneNum(phoneNum);
			if(user==null) {
				map.put("msg", "手机号还未注册");
				return map;
			}
			//验证码检验
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//验证无法通过
				map.put("msg", "无效验证码");
				return map;
			}
			//登陆成功---向上层抛出ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;	
		}else {
			//非法
			map.put("msg","角色错误");
			return map;
		}
	}
	
	
	//用户登录---密码
	@Override
	public Map<String, String> loginByPassWord(String username, String password,int role) {
		Random random = new Random();
		Map<String,String> map = new HashMap<>();
		//注册的简单验证
		if(StringUtils.isBlank(username)) {
			map.put("msg","用户名不能为空");
			return map;
		}
		
		if(StringUtils.isBlank(password)) {
			map.put("msg","密码不能为空");
			return map;
		}
		
		//判断是不是电话
		boolean isPhone = false;
		char first = username.charAt(0);
		if(first>='0' && first<='9') {
			isPhone = true;
		}
		
		
		if(role==1) {
			//孕妇登陆
			//用户名重复验证
			User user = null; 
			if(isPhone) {
				user = userMapper.selByPhoneNum(username);
			}else {
				user = userMapper.selByName(username);
			}
			if(user==null) {
				map.put("msg", "用户不存在");
				return map;
			}
			//如果用户存在就验证密码
			if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
				map.put("msg", "密码错误");
				return map;
			}
			//登陆成功---向上层抛出ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//医生登陆
			//用户名重复验证
			User user = null; 
			if(isPhone) {
				user = docterMapper.selByPhoneNum(username);
			}else {
				user = docterMapper.selByName(username);
			}
			//用户名重复验证
			if(user==null) {
				map.put("msg", "用户不存在");
				return map;
			}
			//如果用户存在就验证密码
			if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
				map.put("msg", "密码错误");
				return map;
			}
			//登陆成功---向上层抛出ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else {
			//非法
			map.put("msg","角色错误");
			return map;
		}
	}
	
	//生成令牌的函数----通过redis实现
	private String addTicket(User user) {
		//uuid作为key
		String ticket = UUID.randomUUID().toString().replaceAll("-","");
		String ticketKey = Rediskeyutil.getTicketKey(ticket);
		String userJson = JSONObject.toJSONString(user);
		//先设置三天有效时间
		jedisAdapter.setEX(ticketKey, userJson, 60*60*24*3);
		return ticket;

	}
	
	//退出登录 redis实现
 	@Override
	public Map<String, String> logout(String ticket) {
 		Map<String, String> map = new HashMap<>();
 		String ticketKey = Rediskeyutil.getTicketKey(ticket);
 		if(jedisAdapter.exists(ticketKey)) {
 			jedisAdapter.del(ticketKey);
 		}
		return map;
	}
	
//	//生成令牌的函数----通过数据库方式实现
//	private String addTicket(int userId,int role) {
//		Ticket loginTicket = new Ticket();
//		
//		//增加role
//		loginTicket.setRole(role);
//		
//		loginTicket.setUserId(userId);
//		loginTicket.setStatus(0);
//		Date expired = new Date();
//		//设置3天有效期
//		expired.setTime(expired.getTime()+1000*3600*24*3);
//		loginTicket.setExpired(expired);
//		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
//		ticketMapper.insTicket(loginTicket);
//		return loginTicket.getTicket();
//	}
	
//	//数据库版本的退出登录
//	@Override
//	public Map<String, String> logout(String ticket) {
//		Map<String, String> map = new HashMap<>();
//		int res = ticketMapper.updTicket(ticket, 1);
//		if(res<1) {
//			map.put("msg", "退出失败");
//		}
//		return map;
//	}
	@Override
	public User getUserByName(String name) {
		
		return userMapper.selByName(name);
	}

	
////////////////////////////////////////////////增加查询功能///////////////////////////////////////////////////////////////////
	@Override
	public User getUserByIdAndRole(int id, int role) {
		
		if(role==2) {
			//去医生表中查找
			return docterMapper.selById(id);
		}else if(role==1) {
			//去用户库中查询
			return userMapper.selById(id);
		}
		// TODO Auto-generated method stub
		return null;
	}
	
/////////////////////////////////////////////////以下为医生相关///////////////////////////////////////////////////////////	
	@Override
	public List<Map<String,Object>> getAllDocter() {
		// TODO Auto-generated method stub
		List<Map<String,Object>> list = new ArrayList<>();
		list = docterMapper.selAllBackground();
		return list;
	}
	
	
	@Override
	public User getDocterById(int id) {
		// TODO Auto-generated method stub
		return docterMapper.selById(id);
	}

	@Override
	public Map<String, Object> getDocterBackById(int id) {
		// TODO Auto-generated method stub
		return docterMapper.selBackgroundById(id);
	}
/////////////////////////////////////////以下为孕妇相关/////////////////////////////////////////////////////////
	
	//更新孕妇基本信息
	@Override
	public int setInfo(GravidaInfo Info) {
		
		//先检查一下有没有这个用户的数据，有就更新，没有就新插入一条
		Map<String,String> map = new HashMap<>();
		
		GravidaInfo oldInfo = gravidaInfoMapper.selGravidaInfo(Info.getUserId());
		if(oldInfo==null) {
			//说明没有这个用户的数据，就进行插入
			int res = gravidaInfoMapper.insGravidaInfo(Info);
			return res;
		}else {
			//说明已经存在就直接进行数据更新
			int res = gravidaInfoMapper.updInfo(Info);
			return res;
		}
	}
	
	//获取孕妇基本信息
	@Override
	public GravidaInfo getGravidaInfo(int userId) {
		// TODO Auto-generated method stub
		return gravidaInfoMapper.selGravidaInfo(userId);
	}
	
	//暂时没有用到
	@Override
	public Map<String, String> updateInfo(GravidaInfo Info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Report> getUserReportList(int userId) {
		// TODO Auto-generated method stub
		return reportMapper.selByUserId(userId);
	}

	@Override
	public Report getReportDetail(int id) {
		return reportMapper.selById(id);
	}
///////////////////////增加手机验证功能
	@Override
	public User getUserByPhoneNum(String phoneNum) {
		// TODO Auto-generated method stub
		return userMapper.selByPhoneNum(phoneNum);
	}

	@Override
	public User getDocterByPhoneNum(String phoneNum) {
		// TODO Auto-generated method stub
		return docterMapper.selByPhoneNum(phoneNum);
	}

	

	
	
	
	
	
	

}
