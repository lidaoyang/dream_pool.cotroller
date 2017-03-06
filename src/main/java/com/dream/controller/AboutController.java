package com.dream.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {
	
	@RequestMapping("/server")	
	public String server(Model model,HttpSession session, HttpServletRequest request) {
		return "/about/server";
	}
	@RequestMapping("/risk")	
	public String risk_info(Model model,HttpSession session, HttpServletRequest request) {
		return "/about/risk_info";
	}
 }
