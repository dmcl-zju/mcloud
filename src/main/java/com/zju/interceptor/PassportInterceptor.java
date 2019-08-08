package com.zju.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zju.mapper.DocterMapper;
import com.zju.mapper.TicketMapper;
import com.zju.mapper.UserMapper;
import com.zju.model.HostHolder;
import com.zju.model.Ticket;
import com.zju.model.User;

@Component
public class PassportInterceptor implements HandlerInterceptor {

	@Resource
	TicketMapper ticketMapper;
	
	@Resource
	UserMapper userMapper;
	
	
	@Resource
	DocterMapper docterMapper;
	
	@Resource
	HostHolder hostHolder;
		
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		String ticket = null;
		if(request.getCookies()!=null) {
			for(Cookie cookie:request.getCookies()) {
				if(cookie.getName().equals("ticket")) {
					ticket = cookie.getValue();
					break;
				}
			}
		}
		
		if(null != ticket) {
			Ticket loginTicket = ticketMapper.selByticket(ticket);
			if(loginTicket==null || loginTicket.getStatus()!=0 || loginTicket.getExpired().before(new Date())) {
				//System.out.println("�ǵ�½�û�"+loginTicket);
				return true;
			}
			//������˵���ǵ�½�û������ж���ҽ�������и�
			if(loginTicket.getRole()==1) {
				//�и�
				User user = userMapper.selById(loginTicket.getUserId());
				hostHolder.set(user);
				//System.out.println("�и��û�"+user.getName()+"��½������");
			}else if(loginTicket.getRole()==2) {
				//ҽ��
				User user = docterMapper.selById(loginTicket.getUserId());
				hostHolder.set(user);
				//System.out.println("ҽ���û�"+user.getName()+"��½������");
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		if(modelAndView != null) {
			modelAndView.addObject("user", hostHolder.get());
		}
		
		//System.out.println("��Ⱦǰִ����");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		hostHolder.clean();
		//System.out.println("���շ���ִ����");
	}

}
