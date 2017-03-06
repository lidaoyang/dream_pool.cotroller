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

import com.dream.model.Auth_role_function;
import com.dream.service.Auth_role_functionService;
import com.util.DateUtils;
import com.util.StrUtils;
import com.util.common.Json;

@Controller
@RequestMapping("/role_function")
public class Auth_role_functionController {
	@Resource
	private Auth_role_functionService auth_role_functionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model, HttpSession session,
			HttpServletRequest request) {
		return "/auth_role_function/index_auth_role_function";
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
			pagesize = "15";
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
		return auth_role_functionService.select_list(session, map);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, HttpSession session,
			HttpServletRequest request) {
		String edit_type = request.getParameter("edit_type");
		String url = null;
		if ("add".equalsIgnoreCase(edit_type)) {
			url = "/auth_role_function/form_auth_role_function";
		} else if ("edit".equalsIgnoreCase(edit_type)) {
			url = "/auth_role_function/form_auth_role_function";
		}
		return url;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Json save(HttpSession session, HttpServletRequest request) {
		String add_flag = StrUtils.GetString(request.getParameter("add_flag"));
		String roleId = StrUtils.GetString(request.getParameter("roleId"));
		String nodes = StrUtils.GetString(request.getParameter("nodes"));
		Json j = new Json();
		if ("".equals(roleId)) {
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("roleId_is_null");
			j.setMsg_desc("请选择角色！");
			return j;
		}
		if ("".equals(nodes)) {
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("nodes_is_null");
			j.setMsg_desc("请选择菜单项！");
			return j;
		}
		JSONArray nodeJarr = new JSONArray();
		try {
			nodeJarr = JSONArray.fromObject(nodes);
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setState("error");
			j.setMsg("nodes_is_error\r\n"+e.getMessage());
			j.setMsg_desc("菜单项数据格式错误！");
			return j;
		}
		StringBuilder funIds = new StringBuilder("");
		ArrayList<Auth_role_function> rflist = new ArrayList<Auth_role_function>();
		for (int i = 0; i < nodeJarr.size(); i++) {
			JSONObject nodeJo = nodeJarr.getJSONObject(i);
			if ("true".equals(add_flag)) {
				Auth_role_function auth_role_function = new Auth_role_function();
				auth_role_function.setEdit_type("insert");
				auth_role_function.setCreate_time(DateUtils.DateToStr(new Date(), ""));
				auth_role_function.setRole_id(roleId);
				auth_role_function.setStatus("");
				auth_role_function.setUpdate_time(auth_role_function.getCreate_time());
				auth_role_function.setFunction_id(nodeJo.getString("id"));
				rflist.add(auth_role_function);
			}else if ("false".equals(add_flag)){
				if (i<nodeJarr.size()-1) {
					funIds.append(nodeJo.getString("id")).append(",");
				}else {
					funIds.append(nodeJo.getString("id"));
				}
			}
		}
		int ret = 0;
		if ("true".equals(add_flag)) {
			ret = auth_role_functionService.insertBatch(rflist);
		}else if ("false".equals(add_flag)){
			ret = auth_role_functionService.deleteRoleIdFunId(roleId, funIds.toString());
		}
		if (ret>0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("save_success");
			j.setMsg_desc("保存成功！");
		}else {
			j.setState("error");
			j.setMsg("save_fail");
			j.setMsg_desc("保存失败！");
		}
		return j;
	}
}
