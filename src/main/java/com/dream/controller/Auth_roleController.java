package com.dream.controller;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.model.Auth_role;
import com.dream.service.Auth_functionService;
import com.dream.service.Auth_roleService;
import com.util.DateUtils;
import com.util.StrUtils;
import com.util.common.Json;

@Controller
@RequestMapping("/role")
public class Auth_roleController {
	@Resource
	private Auth_roleService auth_roleService;
	
	@Resource
	private Auth_functionService auth_functionService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model, HttpSession session,
			HttpServletRequest request) {
		return "/role/index_role";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> list(HttpSession session,
			HttpServletRequest request) {
		String page = StrUtils.GetString(request.getParameter("page"));
		String pagesize = StrUtils.GetString(request.getParameter("rows"));
		String sortname = StrUtils.GetString(request.getParameter("sort"));
		String sortorder = StrUtils.GetString(request.getParameter("order"));
		if ("".equals(page)) {
			page = "1";
		}
		if ("".equals(pagesize)) {
			pagesize = "30";
		}
		if ("".equals(sortname)) {
			sortname = "id";
		}
		if ("".equals(sortorder)) {
			sortorder = "asc";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		int p = (Integer.valueOf(page) - 1) * Integer.valueOf(pagesize);
		map.put("limit", p + "," + pagesize);
		map.put("orderby", sortname + " " + sortorder);
		return auth_roleService.select_list(session, map);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, HttpSession session,
			HttpServletRequest request) {
		String edit_type = request.getParameter("edit_type");
		String id = StrUtils.GetString(request.getParameter("id"));
		if ("add".equalsIgnoreCase(edit_type)) {
			Auth_role auth_role = new Auth_role();
			auth_role.setStatus("0");
			auth_role.setCreate_time(DateUtils.DateToStr(new Date(), ""));
			auth_role.setUpdate_time(auth_role.getCreate_time());
			model.addAttribute("role", auth_role);
			model.addAttribute("edit_type", "insert");
		} else if ("edit".equalsIgnoreCase(edit_type)) {
			Auth_role auth_role = auth_roleService.select(id);
			model.addAttribute("role", auth_role);
			model.addAttribute("edit_type", "update");
		}
		return "/role/form_role";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Json save(Auth_role auth_role, HttpSession session,
			HttpServletRequest request) {
		Json j = new Json();
		j.setState("error");
		j.setMsg("save_fail");
		j.setMsg_desc("保存失败！");
		int ret = auth_roleService.save(auth_role, session);
		if (ret>0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("save_success");
			j.setMsg_desc("保存成功！");
		}
		return j;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Json delete(HttpSession session,HttpServletRequest request) {
		Json j = new Json();
		String id = StrUtils.GetString(request.getParameter("id"));
		j.setState("error");
		j.setMsg("delete_fail");
		j.setMsg_desc("删除失败！");
		int ret = auth_roleService.delete(id);
		if (ret > 0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("delete_success");
			j.setMsg_desc("删除成功！");
		}
		return j;
	}
}
