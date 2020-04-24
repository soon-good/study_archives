package com.sec.example.web;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

//	@GetMapping("/login")
//	public String goLoginPage(){
//		return "/login";
//	}

	@ResponseBody
	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody Map<String,Object> request){
		return null;
	}
}
