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
		//ע��ļ���֤
		if(StringUtils.isBlank(username)) {
			map.put("msg","�û�������Ϊ��");
			return map;
		}
		
		if(StringUtils.isBlank(password)) {
			map.put("msg","���벻��Ϊ��");
			return map;
		}
		
		if(StringUtils.isBlank(phoneNum)) {
			map.put("msg","�ֻ��Ų���Ϊ��");
			return map;
		}
		
		if(StringUtils.isBlank(code)) {
			map.put("msg","��֤�벻��Ϊ��");
			return map;
		}
		
		if(role==1) {
			//�и�ע��
			User user = null;
			//�����ֻ����ظ���֤
			user = userMapper.selByPhoneNum(phoneNum);
			if(user!=null) {
				map.put("msg", "�ֻ����ѱ�ע��");
				return map;
			}
			//��֤�����
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//��֤ͨ��
				map.put("msg", "��Ч��֤��");
				return map;
			}
			
			//�û����ظ���֤
			user = userMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "�û����Ѿ�����");
				return map;
			}
			
			//����ע��
			user = new User();
			//���ӽ�ɫ
			user.setRole(role);
			//�����ֻ���
			user.setPhoneNum(phoneNum);
			
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
			//int userId = userMapper.selByName(username).getId();
			//String ticket = addTicket(userId,role);
			user = docterMapper.selByName(username);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//ҽ��ע��
			User user = null;
			//�ֻ����ظ���֤
			user = docterMapper.selByPhoneNum(phoneNum);
			if(user!=null) {
				map.put("msg", "�ֻ����ѱ�ע��");
				return map;
			}
			//��֤�����
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//��֤��ͨ��
				map.put("msg", "��Ч��֤��");
				return map;
			}
		
			user = docterMapper.selByName(username);
			if(user!=null) {
				map.put("msg", "�û����Ѿ�����");
				return map;
			}
			
			//����ע��
			user = new User();
			//���ӽ�ɫ
			user.setRole(role);
			//�����ֻ�
			user.setPhoneNum(phoneNum);
			user.setName(username);
			//�������ͷ��
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			//��������ַ���salt
			String salt = UUID.randomUUID().toString().substring(0, 5);
			user.setSalt(salt);
			//ԭʼ�����salt��Ӻ��ٽ���MD5����
			user.setPassword(WendaUtil.MD5(password+salt));
			int res = docterMapper.insUser(user);
			if(res<1) {
				System.out.println("ҽ��ע��ʧ��");
				map.put("erro","���ݿ�������");
				return map;
			}
			//ע��ɹ�---���ϲ��׳�ticket
			//String ticket = addTicket(docterMapper.selByName(username).getId(),role);
			
			user = docterMapper.selByName(username);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else {
			map.put("msg","��ɫ����");
			return map;
		}
	}
	
	//�û���¼---��֤��
	@Override
	public Map<String, String> loginByCode(String phoneNum, String code, int role) {
		// TODO Auto-generated method stub
		
		Map<String,String> map = new HashMap<>();
		//ע��ļ���֤
		if(StringUtils.isBlank(phoneNum)) {
			map.put("msg","�ֻ��Ų���Ϊ��");
			return map;
		}
		if(StringUtils.isBlank(code)) {
			map.put("msg","��֤�벻��Ϊ��");
			return map;
		}
		if(role==1) {
			User user = null;
			//�����ֻ����ظ���֤
			user = userMapper.selByPhoneNum(phoneNum);
			if(user==null) {
				map.put("msg", "�ֻ��Ż�δע��");
				return map;
			}
			//��֤�����
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//��֤�޷�ͨ��
				map.put("msg", "��Ч��֤��");
				return map;
			}
			//��½�ɹ�---���ϲ��׳�ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;	
		}else if(role==2) {
			User user = null;
			//�����ֻ����ظ���֤
			user = docterMapper.selByPhoneNum(phoneNum);
			if(user==null) {
				map.put("msg", "�ֻ��Ż�δע��");
				return map;
			}
			//��֤�����
			if(!jedisAdapter.exists(phoneNum) || !jedisAdapter.get(phoneNum).equals(code)) {
				//��֤�޷�ͨ��
				map.put("msg", "��Ч��֤��");
				return map;
			}
			//��½�ɹ�---���ϲ��׳�ticket
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;	
		}else {
			//�Ƿ�
			map.put("msg","��ɫ����");
			return map;
		}
	}
	
	
	//�û���¼---����
	@Override
	public Map<String, String> loginByPassWord(String username, String password,int role) {
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
		
		//�ж��ǲ��ǵ绰
		boolean isPhone = false;
		char first = username.charAt(0);
		if(first>='0' && first<='9') {
			isPhone = true;
		}
		
		
		if(role==1) {
			//�и���½
			//�û����ظ���֤
			User user = null; 
			if(isPhone) {
				user = userMapper.selByPhoneNum(username);
			}else {
				user = userMapper.selByName(username);
			}
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
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else if(role==2) {
			//ҽ����½
			//�û����ظ���֤
			User user = null; 
			if(isPhone) {
				user = docterMapper.selByPhoneNum(username);
			}else {
				user = docterMapper.selByName(username);
			}
			//�û����ظ���֤
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
			//String ticket = addTicket(user.getId(),role);
			String ticket = addTicket(user);
			map.put("ticket", ticket);
			return map;
		}else {
			//�Ƿ�
			map.put("msg","��ɫ����");
			return map;
		}
	}
	
	//�������Ƶĺ���----ͨ��redisʵ��
	private String addTicket(User user) {
		//uuid��Ϊkey
		String ticket = UUID.randomUUID().toString().replaceAll("-","");
		String ticketKey = Rediskeyutil.getTicketKey(ticket);
		String userJson = JSONObject.toJSONString(user);
		//������������Чʱ��
		jedisAdapter.setEX(ticketKey, userJson, 60*60*24*3);
		return ticket;

	}
	
	//�˳���¼ redisʵ��
 	@Override
	public Map<String, String> logout(String ticket) {
 		Map<String, String> map = new HashMap<>();
 		String ticketKey = Rediskeyutil.getTicketKey(ticket);
 		if(jedisAdapter.exists(ticketKey)) {
 			jedisAdapter.del(ticketKey);
 		}
		return map;
	}
	
//	//�������Ƶĺ���----ͨ�����ݿⷽʽʵ��
//	private String addTicket(int userId,int role) {
//		Ticket loginTicket = new Ticket();
//		
//		//����role
//		loginTicket.setRole(role);
//		
//		loginTicket.setUserId(userId);
//		loginTicket.setStatus(0);
//		Date expired = new Date();
//		//����3����Ч��
//		expired.setTime(expired.getTime()+1000*3600*24*3);
//		loginTicket.setExpired(expired);
//		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
//		ticketMapper.insTicket(loginTicket);
//		return loginTicket.getTicket();
//	}
	
//	//���ݿ�汾���˳���¼
//	@Override
//	public Map<String, String> logout(String ticket) {
//		Map<String, String> map = new HashMap<>();
//		int res = ticketMapper.updTicket(ticket, 1);
//		if(res<1) {
//			map.put("msg", "�˳�ʧ��");
//		}
//		return map;
//	}
	@Override
	public User getUserByName(String name) {
		
		return userMapper.selByName(name);
	}

	
////////////////////////////////////////////////���Ӳ�ѯ����///////////////////////////////////////////////////////////////////
	@Override
	public User getUserByIdAndRole(int id, int role) {
		
		if(role==2) {
			//ȥҽ�����в���
			return docterMapper.selById(id);
		}else if(role==1) {
			//ȥ�û����в�ѯ
			return userMapper.selById(id);
		}
		// TODO Auto-generated method stub
		return null;
	}
	
/////////////////////////////////////////////////����Ϊҽ�����///////////////////////////////////////////////////////////	
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
/////////////////////////////////////////����Ϊ�и����/////////////////////////////////////////////////////////
	
	//�����и�������Ϣ
	@Override
	public int setInfo(GravidaInfo Info) {
		
		//�ȼ��һ����û������û������ݣ��о͸��£�û�о��²���һ��
		Map<String,String> map = new HashMap<>();
		
		GravidaInfo oldInfo = gravidaInfoMapper.selGravidaInfo(Info.getUserId());
		if(oldInfo==null) {
			//˵��û������û������ݣ��ͽ��в���
			int res = gravidaInfoMapper.insGravidaInfo(Info);
			return res;
		}else {
			//˵���Ѿ����ھ�ֱ�ӽ������ݸ���
			int res = gravidaInfoMapper.updInfo(Info);
			return res;
		}
	}
	
	//��ȡ�и�������Ϣ
	@Override
	public GravidaInfo getGravidaInfo(int userId) {
		// TODO Auto-generated method stub
		return gravidaInfoMapper.selGravidaInfo(userId);
	}
	
	//��ʱû���õ�
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
///////////////////////�����ֻ���֤����
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
