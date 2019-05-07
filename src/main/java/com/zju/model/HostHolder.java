package com.zju.model;

import org.springframework.stereotype.Component;

/**
 * �����̵߳�����ͨ��
 * @author lin
 *
 */

@Component
public class HostHolder {
	private ThreadLocal<User> users = new ThreadLocal<>();
	
	public User get() {

		return users.get();
		
	}
	public void set(User user) {
		users.set(user);
	}
	public void clean() {
		users.remove();
	}
}
