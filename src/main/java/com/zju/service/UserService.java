package com.zju.service;

import java.util.List;
import java.util.Map;

import com.zju.model.GravidaInfo;
import com.zju.model.Report;
import com.zju.model.User;

public interface UserService {
	
	//��ID��ȡ�û�����
	public User getUserById(int id);
	
	//ͨ���û�������
	public User getUserByName(String name);
	
	//�û�ע��
	public Map<String,String> regist(String username,String password,int role);
	
	//�û���¼
	public Map<String,String> login(String username,String password,int role);
	
	//�û��˳���¼
	public Map<String,String> logout(String ticket);
	
	//��ȡ���е�ҽ���û�
	public List<Map<String,Object>> getAllDocter();
	
	//��ȡ���е�ҽ���û�
    public User getDocterById(int id);
    
    //��ȡ���е�ҽ����������
    public Map<String,Object> getDocterBackById(int id);
    
    //�����и�������Ϣ
    public Map<String,String> updateInfo(GravidaInfo Info);
    
    //��ȡ�и�������Ϣ
    public GravidaInfo getGravidaInfo(int userId);
    
    //�½��и�������Ϣ
    public int setInfo(GravidaInfo Info);
    
    //��ȡ�и������б�
    public List<Report> getUserReportList(int userId); 
    
    //��ȡ�и�ĳһ����ϱ���
    public Report getReportDetail(int id);
    
   /////////////////////////////////////////////////////////////
    //���Ӳ�ѯ����
    public User getUserByIdAndRole(int id,int role);
    
	
}
