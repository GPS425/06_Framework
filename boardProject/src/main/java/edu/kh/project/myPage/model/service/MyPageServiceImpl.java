package edu.kh.project.myPage.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MyPageServiceImpl implements MyPageService{
	
	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		// A^^^B^^^C 형태로 가공
		
		// 주소가 입력되었을 때
		if(!inputMember.getMemberAddress().equals(",,")) {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
			// 주소가 입력되지 않았을 때
			inputMember.setMemberAddress(null);
		}
		
		// inputMember : 수정 닉네임, 수정 전화번호, 수정 주소, 회원 번호
		
		return mapper.updateInfo(inputMember);
	}

	@Override
	public int changePw(String currentPw, String newPw, int memberNo) {
	    
	    // 1. [DB 조회] 현재 로그인한 회원의 "암호화된 비밀번호"를 가져옴
	    // why? BCrypt 검사를 하려면 DB에 있는 정답지(암호문)가 필요하니까.
	    String pw = mapper.selectPw(memberNo);
	    
	    // 2. [비밀번호 검증] 입력한 현재 비번이 맞는지 확인
	    // - currentPw : 사용자가 입력한 쌩 비밀번호 (예: "1234")
	    // - pw        : DB에서 가져온 암호화된 비밀번호 (예: "$2a$10$...")
	    // matches()가 내부적으로 소금 쳐서 비교함. 틀리면 !가 true가 돼서 들어옴.
	    if( !bcrypt.matches(currentPw, pw) ) {
	        return 0; // 비밀번호 불일치 -> 0 반환하고 함수 종료(빠꾸)
	    }
	    
	    // 3. [새 비밀번호 암호화] 검증 통과했으니 변경할 비번 암호화
	    // - newPw : 사용자가 입력한 새 쌩 비밀번호 (예: "5678")
	    // - encode() : 이걸 DB에 저장할 수 있게 외계어($2a$10$...)로 바꿈
	    String newEncPw = bcrypt.encode(newPw);
	    
	    // 4. [포장] DB에 보낼 택배 상자(Member 객체) 세팅
	    Member member = new Member();
	    member.setMemberNo(memberNo); // 누가 (WHERE 조건에 쓸 번호)
	    member.setMemberPw(newEncPw); // 뭘로 (SET에 쓸 암호화된 새 비번)
	    
	    // 5. [DB 수정] 포장한 상자를 Mapper에게 던져서 UPDATE 실행
	    // - 성공 시 1, 실패 시 0 반환됨
	    return mapper.changePw(member);
	}
}
