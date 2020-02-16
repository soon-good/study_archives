package com.sec.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index(){
		return "index";
	}

	@GetMapping("/home")
	public String home(){
		return "home";
	}

	@GetMapping("/welcome")
	public String welcome(){
		return "welcome";
	}
}
