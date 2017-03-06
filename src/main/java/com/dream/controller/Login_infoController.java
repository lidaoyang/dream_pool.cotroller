package com.dream.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.model.Login_info;
import com.dream.service.Login_infoService;
import com.util.StrUtils;
import com.util.common.Json;

@Controller
@RequestMapping("/login_info")
public class Login_infoController {
	@Resource
	private Login_infoService login_infoService;
	
	@RequestMapping(value = "/",method = RequestMethod.GET)	
	public String index(Model model,HttpSession session, HttpServletRequest request) {
		return "/login_info/index_login_info";
	}
	
	@RequestMapping(value = "/list",method = RequestMethod.GET)	
	@ResponseBody
	public HashMap<String, Object> list(HttpSession session, HttpServletRequest request) {
		String page = StrUtils.GetString(request.getParameter("page"));
		String pagesize = StrUtils.GetString(request.getParameter("rows"));
		String sortname = StrUtils.GetString(request.getParameter("sort"));
		String sortorder = StrUtils.GetString(request.getParameter("order"));
		if ("".equals(page)) {
			page = "1";
		}
		if ("".equals(pagesize)) {
			pagesize = "15";
		}
		if ("".equals(sortname)) {
			sortname="id";
		}
		if ("".equals(sortorder)) {
			sortorder ="desc";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		int p = (Integer.valueOf(page)-1)*Integer.valueOf(pagesize);
		map.put("limit", p+","+pagesize);
		map.put("orderby", sortname + " " + sortorder);
		return login_infoService.select_list(session, map);
	}
	
	@RequestMapping(value = "/form",method = RequestMethod.GET)	
  public String form(Model model,HttpSession session, HttpServletRequest request) {
		String edit_type = request.getParameter("edit_type");
		String url = null;
	if ("add".equalsIgnoreCase(edit_type)) {
		url = "/login_info/form_login_info";
	} else if ("edit".equalsIgnoreCase(edit_type))  {
		url = "/login_info/form_login_info";
	}
		return url;
 }
	
	@RequestMapping(value = "/save",method = RequestMethod.POST)	
	@ResponseBody
 public Json save(Login_info login_info,HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		login_infoService.save(login_info, session);
		j.setSuccess(true);
		j.setMsg("操作成功！");
		return j;
	 }
 }
