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
	
	
}
