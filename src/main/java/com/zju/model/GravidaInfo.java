package com.zju.model;

import java.util.Date;

/**
 * 	�и���Ϣ
 * @author lin
 *
 */
public class GravidaInfo {
	private int id;
	//�û�id��ԭ���ı�Խ�
	private int userId;
	//��������������������û���
	private String name;
	private int age;
	private double weight;
	private double height;
	//����
	private int weeks;
	//���д���
	private int pregnantTimes;
	//Ԥ����
	private Date expectedDate;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public int getWeeks() {
		return weeks;
	}
	public void setWeeks(int weeks) {
		this.weeks = weeks;
	}
	
	public int getPregnantTimes() {
		return pregnantTimes;
	}
	public void setPregnantTimes(int pregnantTimes) {
		this.pregnantTimes = pregnantTimes;
	}
	public Date getExpectedDate() {
		return expectedDate;
	}
	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}
	@Override
	public String toString() {
		return "GravidaInfo [id=" + id + ", userId=" + userId + ", name=" + name + ", age=" + age + ", weight=" + weight
				+ ", height=" + height + ", weeks=" + weeks + ", pregnantTimes=" + pregnantTimes + ", expectedDate="
				+ expectedDate + "]";
	}
	
	
}
