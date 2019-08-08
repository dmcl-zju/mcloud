package com.zju.service;

import com.zju.model.FhrData;

public interface DataService {
	
	public FhrData getDataById(int id);
	
	public int addData(FhrData fhrData);
	
	public int updValue(FhrData fhrData);
}
