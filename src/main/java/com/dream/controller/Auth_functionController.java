package com.dream.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.model.Auth_function;
import com.dream.model.Auth_user;
import com.dream.service.Auth_functionService;
import com.util.DateUtils;
import com.util.StrUtils;
import com.util.common.Json;
import com.util.common.Node;
import com.util.common.NodeAttribute;
import com.util.common.Tree;

@Controller
@RequestMapping("/function")
public class Auth_functionController {
	@Resource
	private Auth_functionService auth_functionService;
	
	@RequestMapping(value = "/index",method = RequestMethod.GET)	
	public String index(Model model,HttpSession session, HttpServletRequest request) {
		return "/function/index_function";
	}
	
	@RequestMapping(value = "/{parentId}/children",method = RequestMethod.GET)	
	@ResponseBody
	public HashMap<String, Object> get_children(HttpSession session, HttpServletRequest request,
			@PathVariable String parentId) {
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
			sortname="serial_num";
		}
		if ("".equals(sortorder)) {
			sortorder ="asc";
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		int p = (Integer.valueOf(page)-1)*Integer.valueOf(pagesize);
		map.put("parent_id", parentId);
		map.put("limit", p+","+pagesize);
		map.put("orderby", sortname + " " + sortorder);
		return auth_functionService.select_list(session, map);
	}
	@RequestMapping(value = "/getFunctionTree",method = RequestMethod.GET)	
	@ResponseBody
	public List<Node> getFunctionTree(HttpSession session, HttpServletRequest request) {
		String roleId = StrUtils.GetString(request.getParameter("roleId"));
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("orderby", "id");
		//map.put("type", "1");
		ArrayList<Auth_function> functions = auth_functionService.select_class(session, map);
		ArrayList<Auth_function> myfunctions = new ArrayList<Auth_function>();
		if (!"".equals(roleId)) {
			map.clear();
			map.put("role_id", roleId);
			myfunctions = auth_functionService.select_custclass2(session, map);
		}
		List<Node> nodes = new LinkedList<Node>();
		Node root = null;
		for (Auth_function f : functions) {
			boolean checked = false;
			for (Auth_function fn : myfunctions) {
				if (f.getId().equals(fn.getId())) {
					checked = true;
					break;
				}
			}
			Node node = new Node(f.getId(), f.getParent_id(), f.getName(),
					"open",checked, new NodeAttribute(f.getId(),null == f.getUrl() ? "" : f.getUrl()),
					f.getSerial_num());
			nodes.add(node);
			if ("-1".equals(f.getParent_id())) {
				root = node;
			}
		}
		List<Node> result = new ArrayList<Node>();
		if (root!=null) {
			Tree tree = new Tree(nodes, root);
			result = tree.build();
		}
		return result;
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String form(Model model, HttpSession session,
			HttpServletRequest request) {
		String edit_type = request.getParameter("edit_type");
		String id = StrUtils.GetString(request.getParameter("id"));
		if ("add".equalsIgnoreCase(edit_type)) {
			Auth_function auth_function = new Auth_function();
			auth_function.setIs_navigation("0");
			auth_function.setSerial_num("0");
			auth_function.setCreate_time(DateUtils.DateToStr(new Date(), ""));
			auth_function.setUpdate_time(auth_function.getCreate_time());
			model.addAttribute("function", auth_function);
			model.addAttribute("edit_type", "insert");
		} else if ("edit".equalsIgnoreCase(edit_type)) {
			Auth_function auth_function = auth_functionService.select(id);
			model.addAttribute("function", auth_function);
			model.addAttribute("edit_type", "update");
		}
		return "/function/form_function";
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Json delete(HttpSession session,HttpServletRequest request) {
		Json j = new Json();
		String id = StrUtils.GetString(request.getParameter("id"));
		j.setState("error");
		j.setMsg("delete_fail");
		j.setMsg_desc("删除失败！");
		int ret = auth_functionService.delete(id);
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
	public Json save(Auth_function auth_function, HttpSession session,
			HttpServletRequest request) {
		Json j = new Json();
		j.setState("error");
		j.setMsg("save_fail");
		j.setMsg_desc("保存失败！");
		if (auth_function.getIs_navigation()==null) {
			auth_function.setIs_navigation("0");
		}
		int ret = auth_functionService.save(auth_function, session);
		if (ret > 0) {
			j.setSuccess(true);
			j.setState("success");
			j.setMsg("save_success");
			j.setMsg_desc("保存成功！");
		}
		return j;
	}
 }
