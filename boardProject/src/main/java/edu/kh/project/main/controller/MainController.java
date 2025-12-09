package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
	
	@RequestMapping("/")	// / 요청 매핑
	public String mainPage() {

		// 접두사/접미사 제외
		// classpath : src/main/resources/templates/**common/main**.html
		return "common/main";	// forward 한다.
	}
	
	// LoginFilter에서 로그인하지 않았을 때 리다이렉트로 요청
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용해주세요~");
		return "redirect:/";
	}
}
