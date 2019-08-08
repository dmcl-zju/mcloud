package com.zju.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zju.mapper.FhrDataMapper;
import com.zju.model.FhrData;
import com.zju.service.DataService;

@Service
public class DataServiceImpl implements DataService {

	@Resource
	FhrDataMapper fhrDataMapper;
	
	@Override
	public FhrData getDataById(int id) {
		// TODO Auto-generated method stub
		return fhrDataMapper.selById(id);
	}

	@Override
	public int addData(FhrData fhrData) {
		// TODO Auto-generated method stub
		return fhrDataMapper.insFhrDataAll(fhrData);
	}

	@Override
	public int updValue(FhrData fhrData) {
		// TODO Auto-generated method stub
		return fhrDataMapper.updValue(fhrData);
	}

}
