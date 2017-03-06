package com.dream.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dream.model.Auth_function;
import com.dream.model.Auth_user;
import com.dream.service.Auth_functionService;
import com.util.common.Node;
import com.util.common.NodeAttribute;
import com.util.common.Tree;

@Controller
@RequestMapping("/home")
public class DreamHomeController {
	
	@Resource
	private Auth_functionService auth_functionService;
	
	@RequestMapping("/index")	
	public String index(Model model,HttpSession session, HttpServletRequest request) {
		Object obj = session.getAttribute("auth_user");
		if (obj!=null) {
			Auth_user user = (Auth_user)obj;
			HashMap<String, Object> map = new HashMap<String, Object>();
			ArrayList<Auth_function> functions = new ArrayList<Auth_function>();
			map.put("orderby", "id");
			map.put("type", "1");
			if (!"admin".equals(user.getName())) {
				map.put("user_id", user.getId());		
				functions = auth_functionService.select_custclass(session, map);
			}else {
				functions = auth_functionService.select_class(session, map);
			}
			
			List<Node> nodes = new LinkedList<Node>();
			Node root = null;
			for (Auth_function f : functions) {
				Node node = new Node(f.getId(), f.getParent_id(), f.getName(),
						"open",false, new NodeAttribute(f.getId(),null == f.getUrl() ? "" : f.getUrl()),
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
			model.addAttribute("functions", result);
		}else {
			return "user/login";
		}
		return "home/index";
	}
 }
