package com.dream.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.model.Auth_user_role;
import com.dream.service.Auth_user_roleService;
import com.util.DateUtils;
import com.util.StrUtils;
import com.util.common.Json;

@Controller
@RequestMapping("/authorize")
public class Auth_user_roleController {
	@Resource
	private Auth_user_roleService auth_user_roleService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model, HttpSession session,
			HttpServletRequest request) {
		return "/user_role/index_user_role";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> list(HttpSession session,
			HttpServletRequest request) {
		String page = StrUtils.GetString(request.getParameter("page"));
		String pagesize = StrUtils.GetString(request.getParameter("rows"));
		String sortname = StrUtils.GetString(request.getParameter("sort"));
		String sortorder = StrUtils.GetString(request.getParameter("order"));
		String roleId = StrUtils.GetString(request.getParameter("roleId"));
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
			sortorder = "desc";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		int p = (Integer.valueOf(page) - 1) * Integer.valueOf(pagesize);
		map.put("limit", p + "," + pagesize);
		map.put("orderby", sortname + " " + sortorder);
		if (!"".equals(roleId)) {
			map.put("role_id", roleId);
		}
		return auth_user_roleService.select_list(session, map);
	}


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Json save( HttpSession session,HttpServletRequest request) {
		Json j = new Json();
		String roleId = StrUtils.GetString(request.getParameter("roleId"));
		String users = StrUtils.GetString(request.getParameter("users"));
		if ("".equals(roleId)) {
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("roleId_is_null");
			j.setMsg_desc("用户角色ID为空！");
			return j;
		}
		JSONArray userJarr = new JSONArray();
		try {
			userJarr = JSONArray.fromObject(users);
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("users_is_error\r\n"+e.getMessage());
			j.setMsg_desc("用户数据格式错误！");
			return j;
		}
		ArrayList<Auth_user_role> user_roles = new ArrayList<Auth_user_role>();
		for (int i = 0; i < userJarr.size(); i++) {
			JSONObject user = userJarr.getJSONObject(i);
			Auth_user_role auth_user_role = new Auth_user_role();
			auth_user_role.setEdit_type("insert");
			auth_user_role.setRole_id(roleId);
			auth_user_role.setUser_id(user.getString("id"));
			auth_user_role.setCreate_time(DateUtils.DateToStr(new Date(), ""));
			auth_user_role.setUpdate_time(auth_user_role.getCreate_time());
			user_roles.add(auth_user_role);
		}
		int ret = 0;
		if (user_roles.size()>0) {
			ret = auth_user_roleService.insertBatch(user_roles);
		}else {
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("users_is_null");
			j.setMsg_desc("请选择一个用户！");
		}
		if (ret>0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("save_success");
			j.setMsg_desc("保存成功！");
		}else {
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("save_fail");
			j.setMsg_desc("保存失败！");
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
		int ret = auth_user_roleService.delete(id);
		if (ret > 0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("delete_success");
			j.setMsg_desc("删除成功！");
		}
		return j;
	}
}
