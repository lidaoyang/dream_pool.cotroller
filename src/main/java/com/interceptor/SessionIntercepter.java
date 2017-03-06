package com.interceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dream.model.Auth_function;
import com.dream.model.Auth_user;
import com.dream.service.Auth_functionService;
import com.util.DateUtils;
import com.util.StrUtils;

public class SessionIntercepter implements HandlerInterceptor {

	@Resource
	private Auth_functionService auth_functionService;
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {

	}

	/**
	 * 前置拦截器，如果请求的session中没有manage对象，就跳到登录页面
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
	        String url = request.getRequestURL().toString();
		if (url.endsWith(".css") || url.endsWith(".js")
				|| url.contains("images") || url.contains("fonts")
				|| url.endsWith("login") || url.endsWith("logout")
				|| url.endsWith("to_login") || url.endsWith(".html")
				|| url.endsWith(".json") || url.endsWith("/home/index")) {
			return true;
		}
		HttpSession session = request.getSession();
		String contentpath = request.getContextPath();
		String uri = request.getRequestURI();
		String rip = request.getRemoteAddr();
		ServletContext application= request.getServletContext();
		Object access_times_obj = application.getAttribute("access_times");
		if (access_times_obj == null) {
			HashMap<String, ArrayList<Long>> access_times = new HashMap<String, ArrayList<Long>>();
			ArrayList<Long> times = new ArrayList<Long>();
			times.add(new Date().getTime());
			access_times.put(rip+":"+uri, times);
			application.setAttribute("access_times", access_times);
		}else {
			HashMap<String, ArrayList<Long>> access_times = (HashMap<String, ArrayList<Long>>)access_times_obj;
			ArrayList<Long> times = access_times.get(rip+":"+uri);
			if (times==null) {
				times = new ArrayList<Long>();
				times.add(new Date().getTime());
			}else if(times.size()==1){
				times.add(new Date().getTime());
			}else {
				times.remove(0);
				times.add(new Date().getTime());
			}
			access_times.put(rip+":"+uri, times);
			if (times.size()==2) {
				long time1 = times.get(0);
				long time2 = times.get(1);
//				System.out.println("["+DateUtils.DateToStr(new Date(), "yyyy-MM-dd HH:mm:ss")+"]=====================================["+rip+":"+uri+"]=====================用时["+(time2-time1)+"]");
				if (time2-time1<=400) {
//					System.out.println("["+DateUtils.DateToStr(new Date(), "yyyy-MM-dd HH:mm:ss")+"]======wwwww===============================["+rip+":"+uri+"]=====================用时["+(time2-time1)+"]");
//					System.out.println("Access timeout");
					response.sendRedirect(contentpath+"/jsp/error/error_timeout.jsp");
					return false;
				}
			}
		}
		Object obj = session.getAttribute("auth_user");
		if (obj!=null) {
			Auth_user user = (Auth_user)obj;
			if ("admin".equals(user.getName())) {
				return true;
			}
			String m_url = uri.replace(contentpath, "");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", m_url);
			map.put("user_id", user.getId());
			int count = Integer.valueOf(auth_functionService.select_count2(session, map));
			if (count==0) {
				response.sendRedirect(contentpath+"/jsp/error/error_403.jsp");
				return false;
			}
			return true;
		}else {
			response.sendRedirect(contentpath+"/user/to_login");
			return false;
		}
	}
}
