package edu.kh.project.myPage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.extern.slf4j.Slf4j;

// @SessionAttributes({"loginMember"})

/* @SessionAttributes의 역할
 * - Model에 추가된 속성 중 key 값이 일치하는 속성을 session scope로 변경하는 어노테이션
 * - 클래스 상단에 @SessionAttributes({"loginMember"})
 * 
 * @SessionAttribute 의 역할
 * - @SessionAttributes를 통해 session에 등록된 속성을 꺼내올 때 사용하는 어노테이션
 * - 메서드의 매개변수에 @SessionAttribute("loginMember") Member loginMember 작성
 */

@Controller
@RequestMapping("myPage")
@Slf4j
public class MyPageController {

	@Autowired
	private MyPageService service;

	// 내 정보 조회
	/**
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 Member 타입 매개변수 대입
	 * @return
	 */
	@GetMapping("info") // /myPage/info GET 방식 요청 매핑
	public String info(@SessionAttribute("loginMember") Member loginMember, Model model) {

		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 >> session scope에 등록된 상태(loginMember)
		// loginMember(memberAddress도 포함)
		// >> 만약 회원가입 당시 주소를 입력했다면 주소값 문자열(^^^ 구분자로 만들어진 문자열)
		// >> 회원가입 당시 주소를 미입력했다면 null

		String memberAddress = loginMember.getMemberAddress();
		// ?????^^^서울 ??구 ??로???길 ???^^^???동 ???호
		// or null

		if (memberAddress != null) { // 주소가 있을 경우에만 동작
			// 구분자 "^^^"를 기준으로
			// memberAddress 값을 쪼개어 String[] 로 반환
			String[] arr = memberAddress.split("\\^\\^\\^"); // \\: 일반 문자열로 인식되도록 함
			// [?????, 서울 ??구 ??로???길 ???, ???동 ???호]

			model.addAttribute("postcode", arr[0]); // 우편주소
			model.addAttribute("address", arr[1]); // 도로명 / 지번주소
			model.addAttribute("detailAddress", arr[2]); // 상세 주소

		}

		return "myPage/myPage-info";
	}

	// 프로필 이미지 변경 화면 이동
	@GetMapping("profile")
	public String profile() {
		return "myPage/myPage-profile";
	}

	// 비밀번호 변경 화면 이동
	@GetMapping("changePw") // /myPage/changePw Get 방식 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// 회원 탈퇴 화면 이동
	@GetMapping("secession") // /myPage/secession GET 방식 요청 매핑
	public String secession() {
		return "myPage/myPage-secession";
	}

	// 파일 테스트 화면으로 이동
	@GetMapping("fileTest") // /myPage/fileTest Get 방식 요청 매핑
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}

	// 파일 목록 조회 화면 이동
	@GetMapping("fileList") // /myPage/fileList GET 방식 요청 매핑
	public String fileList() {
		return "myPage/myPage-fileList";
	}

	/**
	 * 회원 정보 수정
	 * 
	 * @param inputMember   : 커맨드 객체(@ModelAttribute 생략됨) 제출된 memberNickname,
	 *                      memberTel 세팅된 상태
	 * @param memberAddress : 주소만 따로 배열형태로 얻어옴
	 * @param loginMember   : 로그인한 회원 정보(현재 로그인한 회원의 회원 번호(PK) 사용 예정)
	 * @return
	 */
	@PostMapping("info") // /myPage/info POST 방식 요청 매핑
	public String updateInfo(Member inputMember, @RequestParam("memberAddress") String[] memberAddress,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {

		// inputMember에 현재 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 수정된 회원의 닉네임, 수정된 회원의 전화번호, [주소], 회원번호

		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);

		String message = null;

		if (result > 0) { // 수정 성공
			message = "회원 정보 수정 성공!";

			// loginMember에 DB상 업데이트된 내용으로 세팅
			// >> loginMember는 세션에 저장된 로그인한 회원 정보가 저장되어있다
			// (로그인 할 당시의 기존 데이터)
			// > loginMember를 수정하면 세션에 저장된 로그인한 회원의 정보가 업데이트된다
			// == Session에 있는 회원 정보와 DB 데이터를 동기화

			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());

		} else { // 수정 실패
			message = "수정 실패 ㅉㅉ";

		}

		ra.addFlashAttribute("message", message);

		return "redirect:info"; // 재요청 경로 : /myPage/info GET 요청
	}

	@PostMapping("changePw") // HTML form 태그의 action="changePw" method="POST" 요청을 받음
	public String changePw(
	        // 1. [데이터 수집] HTML 화면에서 입력한 값 2개 가져오기 (name 속성값과 일치해야 함)
	        @RequestParam("currentPw") String currentPw, // 입력한 현재 비밀번호
	        @RequestParam("newPw") String newPw,         // 입력한 새 비밀번호
	        
	        // 2. [로그인 정보] 세션에 저장된 로그인한 회원의 정보를 통째로 가져옴 (누구인지 알아야 하니까)
	        // - "loginMember"라는 키값으로 세션에 저장된 객체를 loginMember 변수에 대입
	        @SessionAttribute("loginMember") Member loginMember, 
	        
	        // 3. [메세지 전달용] 리다이렉트 시 데이터를 잠깐 전달할 객체 (Model은 리다이렉트하면 데이터 날아감)
	        RedirectAttributes ra 
	        ) {
	    
	    // 4. [회원 번호 추출] 로그인한 정보에서 PK(번호)만 쏙 뽑음 (Service에 넘겨주려고)
	    int memberNo = loginMember.getMemberNo();

	    // 5. [서비스 호출] 실무자(Service)한테 "이 사람 비번 좀 바꿔줘" 하고 데이터 던짐
	    // - result : 성공하면 1, 실패(현재 비번 틀림)하면 0이 돌아옴
	    int result = service.changePw(currentPw, newPw, memberNo);

	    String path = null;    // 이동할 주소 저장할 변수
	    String message = null; // 알림 메세지 저장할 변수

	    // 6. [결과 처리] 서비스가 준 결과(0 또는 1)에 따라 분기 처리
	    if (result > 0) { // 변경 성공 (1)
	        message = "비밀번호가 변경 되었습니다";
	        path = "redirect:info"; // 내 정보 페이지(/myPage/info)로 강제 이동

	    } else { // 변경 실패 (0) - 주로 현재 비밀번호가 틀렸을 때
	        message = "현재 비밀번호가 일치하지 않습니다";
	        path = "redirect:changePw"; // 다시 비번 변경 페이지(/myPage/changePw)로 강제 이동(빠꾸)
	    }

	    // 7. [메세지 저장] 리다이렉트 된 페이지에서 딱 한 번만 쓸 수 있는 메세지 세팅
	    // - 그냥 Model에 담으면 redirect 하는 순간 데이터가 증발함
	    ra.addFlashAttribute("message", message);

	    return path; // 설정한 주소로 이동시킴
	}
}
