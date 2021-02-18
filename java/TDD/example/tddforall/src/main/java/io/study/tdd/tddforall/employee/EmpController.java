package io.study.tdd.tddforall.employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class EmpController {
	@GetMapping(value = "/step2/home")
	public String getStep1Test(Locale locale, Model model){
		log.debug("Step1Controller, GetMapping");

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/hh/mm/ss");
		String strNowDateTime = now.format(formatter);

		model.addAttribute("serverTime", strNowDateTime);

		return "step2/home";
	}
}
