package com.zju.service.impl;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.zju.mapper.TicketMapper;
import com.zju.mapper.UserMapper;
import com.zju.model.Ticket;
import com.zju.model.User;
import com.zju.service.UserService;
import com.zju.utils.WendaUtil;


@Service()
public class UserServiceImpl implements UserService{
	
	@Resource
	UserMapper userMapper;
	@Resource
	TicketMapper ticketMapper;
	

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		return userMapper.selById(id);
	}

	@Override
	public Map<String, String> regist(String username, String password) {
		Random random = new Random();
		Map<String,String> map = new HashMap<>();
		//ע��ļ���֤
		if(StringUtils.isBlank(username)) {
			map.put("msg","�û�������Ϊ��");
			return map;
		}
		
		if(StringUtils.isBlank(password)) {
			map.put("msg","���벻��Ϊ��");
			return map;
		}
		
		//�û����ظ���֤
		User user = userMapper.selByName(username);
		if(user!=null) {
			map.put("msg", "�û����Ѿ�����");
			return map;
		}
		
		user = new User();
		//����ע��
		user.setName(username);
		//�������ͷ��
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
		//��������ַ���salt
		String salt = UUID.randomUUID().toString().substring(0, 5);
		user.setSalt(salt);
		//ԭʼ�����salt��Ӻ��ٽ���MD5����
		user.setPassword(WendaUtil.MD5(password+salt));
		int res = userMapper.insUser(user);
		if(res<1) {
			map.put("erro","���ݿ�������");
			return map;
		}
		//ע��ɹ�---���ϲ��׳�ticket
		String ticket = addTicket(userMapper.selByName(username).getId());
		map.put("ticket", ticket);
		return map;
	}

	@Override
	public Map<String, String> login(String username, String password) {
		Random random = new Random();
		Map<String,String> map = new HashMap<>();
		//ע��ļ���֤
		if(StringUtils.isBlank(username)) {
			map.put("msg","�û�������Ϊ��");
			return map;
		}
		
		if(StringUtils.isBlank(password)) {
			map.put("msg","���벻��Ϊ��");
			return map;
		}
		
		//�û����ظ���֤
		User user = userMapper.selByName(username);
		if(user==null) {
			map.put("msg", "�û�������");
			return map;
		}
		//����û����ھ���֤����
		if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
			map.put("msg", "�������");
			return map;
		}
		//��½�ɹ�---���ϲ��׳�ticket
		String ticket = addTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}
	
	
	
	
	private String addTicket(int userId) {
		Ticket loginTicket = new Ticket();
		
		loginTicket.setUserId(userId);
		loginTicket.setStatus(0);
		Date expired = new Date();
		//����3����Ч��
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
			map.put("msg", "�˳�ʧ��");
		}
		return map;
	}

}
