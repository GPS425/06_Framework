package edu.kh.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Filter : 요청, 응답 시 걸러내거나 추가할 수 있는 객체
 * 
 * [필터 클래스 생성 방법]
 * 1. jakarta.servlet.Filter 인터페이스 상속 받기
 * 2. doFilter() 메서드 오버라이딩
 * 
 */

// 로그인이 되어있지 않은 경우 특정 페이지 접근 불가하도록 필터링함
public class LoginFilter implements Filter{
	
	// 필터 동작을 정의하는 메서드
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// ServletRequest : HttpServletRequest의 부모 타입
		// ServletResponse : HttpServletResponse의 부모 타입
		
		// session이 필요함 > Why? > loginMember가 session에 담김
		
		// HttpServletRequest 형태(자식형태)로 다운캐스팅
		HttpServletRequest req = (HttpServletRequest)request;
		// HttpServletResponse 형태(자식형태)로 다운캐스팅
		HttpServletResponse resp = (HttpServletResponse)response;
		
		// 현재 들어온 요청의 URI를 가져옴
		String path = req.getRequestURI(); // /myPage/profile
		
		// 요청 URI가 "/myPage/profile" 로 시작하는지 확인
		if(path.startsWith("/myPage/profile/")) {
			// 필터를 통과하도록 함
			// FilterChain : 다음 필터 또는 DispatcherServlet과 연결된 객체
			chain.doFilter(request, response);
			// 필터를 통과한 후 return
			return;
		}
		
		// session 객체 얻어오기
		HttpSession session = req.getSession();
		
		// 세션에서 로그인한 회원 정보를 꺼내욤
		// loginMember 있는지 null인지 판단
		if(session.getAttribute("loginMember") == null) {
			// 로그인이 되어있지 않은 상태
			
			// loginError 재요청(redirect)
			resp.sendRedirect("/loginError");
			
		} else {
			// 로그인이 되어있는 상태
			
			// 다음 필터로, 또는 다음 필터 없다면 DispatcherServlet으로 요청, 응답 전달
			chain.doFilter(request, response);
			
		}
	}
}
