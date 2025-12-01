package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller	// 요청/응답 제어 역할 명시 + Bean 등록
@Slf4j	// 로그 객체 자동생성(Lombok 제공)
public class MainController {
	
	@Autowired // 타입이 같거나 상속관계를 DI 함
	private TodoService service;
	
	@RequestMapping("/")
	public String mainPage(Model model) {
		
		log.debug("service : " + service);
		
		// todoNo 가 1인 todo의 제목 조회하여 request scope 추가
		String testTitle = service.testTitle();
		model.addAttribute("testTitle", testTitle);
		
		
		
		// src/main/resources/templates/common/main.html
		return "common/main";
	}
}
