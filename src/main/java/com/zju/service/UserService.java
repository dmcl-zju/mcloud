package com.zju.service;

import java.util.Map;

import com.zju.model.User;

public interface UserService {
	
	//��ID��ȡ�û�����
	public User getUserById(int id);
	
	//ͨ���û�������
	public User getUserByName(String name);
	
	//�û�ע��
	public Map<String,String> regist(String username,String password);
	
	//�û���¼
	public Map<String,String> login(String username,String password);
	
	//�û��˳���¼
	public Map<String,String> logout(String ticket);
	
}
