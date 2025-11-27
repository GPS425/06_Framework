package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// instance : 개발자가 직접 new 연산자를 통해 만든 객체, 관리하는 객체

// bean : Spring Container가 만들고, 관리하는 객체
//-> IOC(제어의 반전) : 객체의 생성 및 생명주기의 권한이 개발자가 아닌
// 프레임워크에게 있다.

// 서버 실행 시 Spring Container가 Component Scan(Bean Scanning) 을 수행하여
// @Component, @Repository, @Service, @Controller 어노테이션이 붙은 클래스를 
// 모두 찾아 Bean으로 등록(객체로 생성)

@Controller // 요청/응답을 제어하는 역할인 컨트롤러임을 명시 + Bean 등록(즉, 바로 객체화가 된다는 것)
// @RequestMapping("/test") // /test로 오는 요청을 다 받아주겠다는 의미(클래스 레벨)
public class TestController {
	
	// 기존 Servlet : 클래스 단위로 하나의 요청만 처리 가능했음
	// Spring : 메서드 단위로 요청 처리 가능

	// @RequestMapping("요청주소")
	// - 요청 주소를 처리할 클래스 or 메서드를 매핑하는 어노테이션

	// 1) 클래스와 메서드에 함께 작성하는 방법
	// - 공통 주소를 매핑한다. ->
	// ex) /test/insert, /test/update .. 등

	/*
	 * @RequestMapping("/insert") public void methodA() { // /test/insert 요청을
	 * methodA가 처리하겠다 }
	 * 
	 * @RequestMapping("/update") public void methodB() { // /test/updatet 요청을
	 * methodB가 처리하겠다. }
	 * 
	 * @RequestMapping("/select") public void methodC() { // /test/select 요청을
	 * methodC가 처리하겠다. }
	 */

	// 2) 메서드에만 작성하는 방법 :
	// - 요청 주소와 해당 메서드를 매핑
	// - GET/POST 가리지 않고 매핑을 해줌
	// (속성을 통해서 지정 가능 or 다른 어노테이션 이용 가능)

	// @RequestMapping(value = "/test", method = RequestMethod.GET)
	// Get방식만 처리하겠다는 의미
	@RequestMapping("/test") // /test 요청 시 testMethod가 매핑하여 처리함
	// GET/POST 가리지 않고 다 받음
	public String testMethod() {
		System.out.println("/test 요청 받음");
		
		// Controller 메서드의 반환형은 앞으로 String일 것!
		/*
		 * Controller 메서드의 반환형이 String인 이유!
		 * -> 메서드에서 반환되는 문자열이
		 * forward할 html 파일의 경로가 되기 때문!
		 * 
		 * Thymeleaf : JSP 대신 사용하는 템플릿 엔진(html 형태)
		 * 
		 * classpath : == src/main/resources
		 * 접두사 : classpath:/templates/ ⭐⭐⭐ 
		 * 접미사 : .html
		 * */
		// src/main/resources/templates/test.html <- 이게 경로가 된다는 의미
		return "test"; // 접두사 + 반환값 + 접미사 경로의 html로 forward 하게 됌
	}
}
