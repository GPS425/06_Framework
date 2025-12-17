package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
public class BoardController {
	
	@Autowired
	private BoardService service;

	/** 게시글 목록 조회
	 * 	
	 * {boardCode}
	 *  - /board/xxx  : /board 이하 1레벨 자리에 어떤 주소값이 들어오든 모두 이 메서드에 매핑
	 *  
	 *  {boardCode:[0-9]+}
	 *  - /board/ 이하 1레벨 자리에 숫자로 된 요청 주소가 작성되어 있을 때만 동작
	 *  >> 정규표현식을 이용하여 제한
	 *  
	 *  - [0-9]  : 한 칸에 0~9 사이 숫자 입력이 가능
	 *  - [0-9]+ : 모든 숫자 
	 *  
	 *  @param boardCode : 게시판 종류 구분 번호(1,2,3)
	 *  @param cp : 현재 조회 요청한 페이지 번호(없으면 1)
	 *  @param paramMap(검색 시 이용) : 제출된 파라미터가 모두 저장된 Map
	 *  								(검색 시 key, query 담겨있음)
	 *  								ex) {key=t, query=폭탄}
	 * 
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}")
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
								@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
								Model model,
								@RequestParam Map<String, Object> paramMap 
								) {
		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;
		
		// 검색이 아닌 경우 >> paramMap은 {}
		if(paramMap.get("key") == null) {
			
			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(boardCode, cp);
			
		} else { // 검색인 경우 
			// paramMap에 key라는 k에 접근하면 매핑된 value 반환
			// ex) {key=w, query=짱구}
			// >> w 반환됨

			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			// >> paramMap은 {key=w, query="짱구", boardCode=1}
			
			// 검색(내가 검색하고 싶은 게시글 목록 조회) 서비스 호출
			map = service.searchList(paramMap, cp);
		}
		
		// model에 결과값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		// src/main/resources/templates/board/boardList.html로 forward
		return "board/boardList";
	}
	
	// 상세 조회 요청 주소
	// /board/1/2000
	// /board/2/1557 왜이렇게빨리끝내나요IG
	
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardCode") int boardCode,
							@PathVariable("boardNo") int boardNo,
							@SessionAttribute(value = "loginMember", required = false) Member loginMember,
							Model model,
							RedirectAttributes ra,
							HttpServletRequest req,	// 요청에 담긴 쿠키 얻어오기
							HttpServletResponse resp// 쿠키 응답하기
							) {
		
		// 게시글 상세 조회 서비스 호출
		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// 로그인 상태인 경우에만 memberNo를 map 추가
		// LIKE_CHECK 시 이용(로그인한 사람이 좋아요 누른 게시글인지 체크하기 위함)
		if(loginMember != null) {
			map.put("memberNo",loginMember.getMemberNo());
		}
		
		// 2) 서비스 호출
		Board board = service.selectOne(map);
		
		// log.debug("조회된 board : " + board);
		
		String path = null;
		
		// 조회 결과가 없는 경우 
		if(board == null) {
			path = "redirect:/board/" + boardCode;	// 내가 현재 보고있는 게시판목록으로 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			
		} else {	// 조회 결과가 있는 경우
			// ---------------쿠키를 이용한 조회수 증가-----------------
			// 비회원 또는 로그인한 회원의 글이 아닌 경우
			if(loginMember == null || board.getMemberNo() != loginMember.getMemberNo()) {
				
				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				
				Cookie c = null;
				
				for(Cookie temp : cookies) {
					
					// 쿠키 중에 "readBoardNo"가 존재할 때
					if(temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
				}
				
				int result = 0;	// 조회수 증가 결과를 저장할 변수
				
				// "readBoardNo"가 쿠키에 없을 때
				if(c == null) {
					// 새 쿠키 생성("readBoardNo", [게시글번호])
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					result = service.updateReadCount(boardNo);
					
					
				} else {	// "readBoardNo"가 쿠키에 있을 때
					//      k         :           v
					// readBoardNo    :  [2][30][400][2000]
					
					// 현재 글을 처음 읽는 경우
					if(c.getValue().indexOf("[" + boardNo + "]") == -1) {
						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "[" + boardNo + "]");
						// ex) [2][30][400][2000][4000]
						result = service.updateReadCount(boardNo);
					}
				}
				// 조회 수 증가 성공 / 조회 성공 시
				if(result > 0) {
					// 앞서 조회했던 board의 readCount 값을
					// result 값을 다시 세팅
					board.setReadCount(result);
					
					// 쿠키 적용 경로 설정
					// "/" 이하 경로 요청 시 쿠키 서버로 전달될 수 있도록
					c.setPath("/");
					
					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();
					
					// 다음날 자정 지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					
					// 다음날 자정까지 남은 시간 계산(초단위)
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					
					// 쿠키 수명 설정
					c.setMaxAge((int)secondsUntilNextDay);
					
					resp.addCookie(c);	// 응답 객체를 이용해서 클라이언트에게 전달
					
					
				}
			}
			
			
			
			// ---------------쿠키를 이용한 조회수 증가 끗-----------------
			
			path = "board/boardDetail";
			// src/main/resources/templates/board/boardDetail.html 로 forward
			
			// board - 게시글 일반 내용 + imageList + commentList
			model.addAttribute("board", board);	
			
			// 조회된 이미지 목록(imageList)이 있을 경우
			if( !board.getImageList().isEmpty()) {
				
				BoardImg thumbnail = null;
				
				// imageList의 0번 인덱스 == IMG_ORDER가 가장 빠른 순서
				
				// 만약 이미지 목록의 0번째 요소의 IMG_ORDER가 0이면 == 썸네일
				if(board.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = board.getImageList().get(0);
				}
				
				// thumbnail 변수에는
				// - 이미지 목록의 0번째 요소가 썸네일이면 썸네일 이미지의 Board 객체
				// 썸네일이 아니라면 null
				model.addAttribute("thumbnail", thumbnail);
				
				// start라는 keu에 thumbnail이 null이 아닐때 1 저장, null이면 0 저장
				model.addAttribute("start", thumbnail != null ? 1 : 0);
				// 썸네일 있을 때 : start=1
				// 썸네일 없을 때(일반 이미지만 있거나, 등록된 이미지가 아예 없을 때)
				// : start=0
			}
		}
		return path;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
