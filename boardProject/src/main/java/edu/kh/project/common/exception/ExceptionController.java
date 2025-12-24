package edu.kh.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/*
 * 스프링 예외 처리 방법(우선 순위별로 작성)
 * 
 * 1. 메서드에서 직접 처리하는 방식(try-catch, throws)
 * 
 * 2. Controller 클래스에서 클래스 단위로 모아서 처리
 * (@ExceptionHandler 어노테이션을 지닌 메서드를 해당 클래스에 작성)
 *  
 * 3. 별도의 클래스를 만들어 프로젝트 단위로 모아서 처리
 * (@ControllerAdvice 어노테이션을 지닌 클래스를 작성)
 * 
 */
@ControllerAdvice	// 전역적 예외처리 활성화 어노테이션
public class ExceptionController {
	
	// @ExceptionHandler(예외종류) : 어떤 예외를 다룰건지 작성
	// 예외 종류
	// SQLException.class - SQL 관련 예외만 처리
	// IOException.class - 입출력 관련 예외만 처리
	// .. 등
	
	@ExceptionHandler(NoResourceFoundException.class)
	public String notFound() {	// 404 에러 발생 시
		return "error/404";
	}
	
	// 프로젝트에서 발생하는 모든 종류의 예외를 500으로 처리함
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Model model, Exception e) {
		return "error/500";
	}
	
}
