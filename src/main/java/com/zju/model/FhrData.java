package com.zju.model;

import java.util.Date;

/**
 * 	̥������
 * @author lin
 *
 */
public class FhrData {
	private int id;
	private int userId;
	//����ʱ��
	private Date mTime;
	//�ϴ�ʱ��
	private Date upTime;
	//���ݴ洢·��
	private String data;
	//�������
	private double fhrBase;
	private double fhrStv;
	private double ucBase;
	private double fhrUp;
	private double fhrDown;
	//ϵͳ����������
	private int score;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Date getmTime() {
		return mTime;
	}
	public void setmTime(Date mTime) {
		this.mTime = mTime;
	}
	public Date getUpTime() {
		return upTime;
	}
	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public double getFhrBase() {
		return fhrBase;
	}
	public void setFhrBase(double fhrBase) {
		this.fhrBase = fhrBase;
	}
	public double getFhrStv() {
		return fhrStv;
	}
	public void setFhrStv(double fhrStv) {
		this.fhrStv = fhrStv;
	}
	public double getUcBase() {
		return ucBase;
	}
	public void setUcBase(double ucBase) {
		this.ucBase = ucBase;
	}
	public double getFhrUp() {
		return fhrUp;
	}
	public void setFhrUp(double fhrUp) {
		this.fhrUp = fhrUp;
	}
	public double getFhrDown() {
		return fhrDown;
	}
	public void setFhrDown(double fhrDown) {
		this.fhrDown = fhrDown;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "FhrData [id=" + id + ", userId=" + userId + ", mTime=" + mTime + ", upTime=" + upTime + ", data=" + data
				+ ", fhrBase=" + fhrBase + ", fhrStv=" + fhrStv + ", ucBase=" + ucBase + ", fhrUp=" + fhrUp
				+ ", fhrDown=" + fhrDown + ", score=" + score + "]";
	}
	
	
}
