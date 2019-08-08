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
	

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		return userMapper.selById(id);
	}

	@Override
	public Map<String, String> regist(String username, String password,int role) {
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
		
		if(role != 1 || role !=2) {
			
		}
		
		if(role==1) {
			//孕妇注册
			//用户名重复验证
			User user = userMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "用户名已经存在");
				return map;
			}
			
			//进行注册
			user = new User();
			//增加角色
			user.setRole(role);
			
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
			int userId = userMapper.selByName(username).getId();
			String ticket = addTicket(userId,role);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//医生注册
			User user = docterMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "用户名已经存在");
				return map;
			}
			
			//进行注册
			user = new User();
			//增加角色
			user.setRole(role);
			
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
				map.put("erro","数据库插入错误");
				return map;
			}
			//注册成功---向上层抛出ticket
			String ticket = addTicket(docterMapper.selByName(username).getId(),role);
			map.put("ticket", ticket);
			return map;
		}else {
			map.put("msg","角色错误");
			return map;
		}
	}

	@Override
	public Map<String, String> login(String username, String password,int role) {
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
		
		if(role==1) {
			//用户登陆
			//用户名重复验证
			User user = userMapper.selByName(username);
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
			String ticket = addTicket(user.getId(),role);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//医生登陆
			//用户名重复验证
			User user = docterMapper.selByName(username);
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
			String ticket = addTicket(user.getId(),role);
			map.put("ticket", ticket);
			return map;
		}else {
			//非法
			map.put("msg","角色错误");
			return map;
		}
	}
	

	private String addTicket(int userId,int role) {
		Ticket loginTicket = new Ticket();
		
		//增加role
		loginTicket.setRole(role);
		
		loginTicket.setUserId(userId);
		loginTicket.setStatus(0);
		Date expired = new Date();
		//设置3天有效期
		expired.setTime(expired.getTime()+1000*3600*24*3);
		loginTicket.setExpired(expired);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
		ticketMapper.insTicket(loginTicket);
		return loginTicket.getTicket();
	}

	@Override
	public Map<String, String> logout(String ticket) {
		Map<String, String> map = new HashMap<>();
		int res = ticketMapper.updTicket(ticket, 1);
		if(res<1) {
			map.put("msg", "退出失败");
		}
		return map;
	}

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

	
	
	
	
	
	

}
