package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.common.config.SecurityConfig;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j // Simple logging facade for java
public class MemberServiceImpl implements MemberService{

    private final SecurityConfig securityConfig;
	
	// BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Autowired	// 등록된 Bean 중에서 같은 타입 or 상속관계인 Bean 의존성 주입
	private MemberMapper mapper;
	
    MemberServiceImpl(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }
	
	// 로그인 서비스
	@Override
	public Member login(Member inputMember) throws Exception{
		
		// 암호화 테스트해보기
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
//		String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
//		log.debug("bcryptPassword : " + bcryptPassword);
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원의 정보(+비밀번호) 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		
		// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null인 경우
		if (loginMember == null) return null;
		
		// 3. 입력받은 비밀번호(평문 : inputMember.getMemberPw())와
		// 암호화된 비밀번호(loginMember.getMemberPw())가 일치하는지 확인
		
		// bcrypt.matches(평문, 암호화된 문자) : 평문과 암호화가 내부적으로
		//										 일치한다고 판단되면 true, 아니면 false
		
		// 일치하지 않다면
		if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		} 
		
		// 로그인한 회원 정보에서 비밀번호 제거
		loginMember.setMemberPw(null);
			
		return loginMember;
	}

	// 이메일 중복 검사 서비스
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}

	// 닉네임 중복검사 서비스
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}

	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		// 1. 주소 배열 >> 하나의 문자열로 가공
		// 주소가 입력되지 않으면
		// inputMember.getMemberAddress() >> ",,"
		// memberAddress >> [,,]
		
		// 주소가 입력된 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			// String.join("구분자", 배열)
			// > 배열의 모든 요소 사이에 "구분자" 추가하여 하나의 문자열로 만들어 반환하는 메서드
			
			String address = String.join("^^^", memberAddress);
			// "12345^^^서울시중구^^^3층,302호"
			
			// inputMember의 주소값을 위에서 만든 주소로 세팅
			inputMember.setMemberAddress(address);
			
		} else { 		// 주소가 입력되지 않은 경우
			inputMember.setMemberAddress(null);	// null 저장
		}

		// 2. 비밀번호 암호화
		// inputMember 안의 memberPw : 평문
		// 비밀번호를 암호화하여 inputMember에 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// 회원 가입 매퍼 메서드 호출
		return mapper.signup(inputMember);
	}
	
	
}
