package com.zju.service;

import java.util.List;
import java.util.Map;

import com.zju.model.GravidaInfo;
import com.zju.model.Report;
import com.zju.model.User;

public interface UserService {
	
	//由ID获取用户对象
	public User getUserById(int id);
	
	//通过用户名查找
	public User getUserByName(String name);
	
	//用户注册
	public Map<String,String> regist(String username,String password,int role);
	
	//用户登录
	public Map<String,String> login(String username,String password,int role);
	
	//用户退出登录
	public Map<String,String> logout(String ticket);
	
	//获取所有的医生用户
	public List<Map<String,Object>> getAllDocter();
	
	//获取所有的医生用户
    public User getDocterById(int id);
    
    //获取所有的医生背景资料
    public Map<String,Object> getDocterBackById(int id);
    
    //更新孕妇基本信息
    public Map<String,String> updateInfo(GravidaInfo Info);
    
    //获取孕妇基本信息
    public GravidaInfo getGravidaInfo(int userId);
    
    //新建孕妇基本信息
    public int setInfo(GravidaInfo Info);
    
    //获取孕妇报告列表
    public List<Report> getUserReportList(int userId); 
    
    //获取孕妇某一个诊断报告
    public Report getReportDetail(int id);
    
   /////////////////////////////////////////////////////////////
    //增加查询操作
    public User getUserByIdAndRole(int id,int role);
    
	
}
