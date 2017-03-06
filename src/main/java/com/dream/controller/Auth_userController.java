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

import com.dream.model.Auth_user;
import com.dream.service.Auth_userService;
import com.util.DateUtils;
import com.util.StrUtils;
import com.util.common.Common;
import com.util.common.Json;

@Controller
@RequestMapping("/user")
public class Auth_userController {
	@Resource
	private Auth_userService auth_userService;

	@RequestMapping(value = "/to_login", method = RequestMethod.GET)
	public String to_login(HttpSession session, HttpServletRequest request) {
		return "user/login";
	}
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpSession session, HttpServletRequest request) {
		return "user/index_user_list";
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Json login(HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		String username = StrUtils.GetString(request.getParameter("username"));
		String password = StrUtils.GetString(request.getParameter("password"));
		if ("".equals(username)) {
			j.setState("error");
			j.setSuccess(false);
			j.setMsg("username_is_null");
			j.setMsg_desc("用户名不能为空！");
			return j;
		}
		if ("".equals(password)) {
			j.setState("error");
			j.setSuccess(false);
			j.setMsg("password_is_null");
			j.setMsg_desc("密码不能为空！");
			return j;
		}
		Auth_user user = auth_userService.select_uname(username);
		if (user == null) {
			j.setState("error");
			j.setSuccess(false);
			j.setMsg("username_non_existent");
			j.setMsg_desc("用户名或密码错误！");
			return j;
		}
		if (!Common.validate_password(password, user.getPwd())) {
			j.setState("error");
			j.setSuccess(false);
			j.setMsg("password_is_error");
			j.setMsg_desc("用户名或密码错误！");
			return j;
		}
		int ret = auth_userService.login(user);
		if (ret > 0) {
			j.setState("success");
			j.setSuccess(true);
			j.setMsg("login_success");
			j.setMsg_desc("登录成功！");
			user.setLogin_time(DateUtils.DateToStr(new Date(), ""));
			session.setAttribute("auth_user", user);
		}else {
			j.setState("error");
			j.setSuccess(true);
			j.setMsg("login_fail");
			j.setMsg_desc("登录失败！");
		}
		return j;
	}
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> list(HttpSession session, HttpServletRequest request) {
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
			sortorder = "asc";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		int p = (Integer.valueOf(page) - 1) * Integer.valueOf(pagesize);
		map.put("limit", p + "," + pagesize);
		map.put("orderby", sortname + " " + sortorder);
		String otherwhere = "name !='admin'";
		if (!"".equals(roleId)) {
			otherwhere = otherwhere+" and id not in(select user_id from auth_user_role where role_id='"+roleId+"')";
		}
		map.put("otherwhere", otherwhere);
		HashMap<String, Object> result = auth_userService.select_list(session, map);
		return result;
	}
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session, HttpServletRequest request) {
		Object obj = session.getAttribute("auth_user");
		if (obj!=null) {
			Auth_user user = (Auth_user)obj;
			auth_userService.logout(user);
			session.removeAttribute("auth_user");
		}
		return "user/login";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, HttpSession session,
			HttpServletRequest request) {
		String edit_type = request.getParameter("edit_type");
		String id = StrUtils.GetString(request.getParameter("id"));
		if ("add".equalsIgnoreCase(edit_type)) {
			Auth_user auth_user = new Auth_user();
			auth_user.setCreate_time(DateUtils.DateToStr(new Date(), ""));
			auth_user.setUpdate_time(auth_user.getCreate_time());
			model.addAttribute("user", auth_user);
			model.addAttribute("edit_type", "insert");
		} else if ("edit".equalsIgnoreCase(edit_type)) {
			Auth_user auth_user = auth_userService.select(id);
			model.addAttribute("user", auth_user);
			model.addAttribute("edit_type", "update");
		}
		return "user/form_user";
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Json delete(HttpSession session,HttpServletRequest request) {
		Json j = new Json();
		String id = StrUtils.GetString(request.getParameter("id"));
		j.setState("error");
		j.setMsg("delete_fail");
		j.setMsg_desc("删除失败！");
		int ret = auth_userService.delete(id);
		if (ret > 0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("delete_success");
			j.setMsg_desc("删除成功！");
		}
		return j;
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Json save(Auth_user auth_user, HttpSession session,
			HttpServletRequest request) {
		Json j = new Json();
		j.setState("error");
		j.setMsg("save_fail");
		j.setMsg_desc("保存失败！");
		
		String edit_type = auth_user.getEdit_type();
		if ("insert".equals(edit_type)) {
			String name = auth_user.getName();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			String count = auth_userService.select_count(session, map);
			if (Integer.valueOf(count)>0) {
				j.setState("error");
				j.setMsg("name_is_exist");
				j.setMsg_desc("用户名已存在！");
				return j;
			}
			auth_user.setPwd(Common.create_encrypted_password("123456"));
		}else {
			String pwd = StrUtils.GetString(auth_user.getPwd());
			if (!"".equals(pwd)) {
				auth_user.setPwd(Common.create_encrypted_password(pwd));
			}
		}
		int ret = auth_userService.save(auth_user, session);
		if (ret > 0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("save_success");
			j.setMsg_desc("保存成功！");
		}
		return j;
	}
	@RequestMapping(value = "/modifyPwd", method = RequestMethod.POST)
	@ResponseBody
	public Json modifyPwd(HttpSession session,HttpServletRequest request) {
		Json j = new Json();
		String oldpwd = StrUtils.GetString(request.getParameter("oldpwd"));
		String newpwd = StrUtils.GetString(request.getParameter("newpwd"));
		Object obj = session.getAttribute("auth_user");
		int ret = 0;
		if (obj!=null) {
			Auth_user user = (Auth_user)obj;
			if (!Common.validate_password(oldpwd, user.getPwd())) {
				j.setState("error");
				j.setMsg("oldpwd_is_error");
				j.setMsg_desc("原密码输入错误！");
				return j;
			}
			Auth_user auth_user  = new Auth_user();
			auth_user.setEdit_type("update");
			auth_user.setId(user.getId());
			auth_user.setPwd(Common.create_encrypted_password(newpwd));
			ret = auth_userService.save(auth_user, session);
		}
		if (ret>0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("modify_success");
			j.setMsg_desc("修改成功！");
		}else {
			j.setState("error");
			j.setMsg("modify_fail");
			j.setMsg_desc("修改失败！");
		}
		return j;
	}
}
