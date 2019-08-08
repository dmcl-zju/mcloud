package com.zju.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zju.mapper.ReportMapper;
import com.zju.model.Report;
import com.zju.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {
	
	@Resource
	ReportMapper reportMapper;

	/**
	 * 插入一份诊断报告
	 */
	@Override
	public int addReport(Report report) {
		
		// TODO Auto-generated method stub
		return reportMapper.insReport(report);
	}

}
