package edu.kh.project.admin.model.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

	private final AdminMapper mapper;
	private final BCryptPasswordEncoder bCrypt;
	
	// 관리자 로그인 서비스
	@Override
	public Member login(Member inputMember) {
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		if(loginMember == null) return null;
		
		if(!bCrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		loginMember.setMemberPw(null);
		
		return loginMember;
	}

	// 관리자 이메일 중복 여부 검사 서비스
	@Override
	public int checkEmail(String memberEmail) {
		
		return mapper.checkEmail(memberEmail);
	}

	// 관리자 계정 발급 서비스
	@Override
	public String createAdminAccount(Member member) {
		
		// 1. 영어(대소문자), 숫자도 포함하는 6자리 난수로 만든
		// 비밀번호를 평문, 암호화 한 값 구하기
		String rawPw = Utility.generatePassword();	// 평문 비밀번호 
		
		// 2. 평문 비밀번호 암호화하여 저장
		String encPw = bCrypt.encode(rawPw);
		
		// 3. member에 암호화된 비밀번호 세팅 (4dfRR3)
		member.setMemberPw(encPw);
		
		// 4. DB에 암호화된 비밀번호가 세팅된 member를 전달하여 계정 발급
		int result = mapper.createAdminAccount(member);
		
		// 5. 계정 발급이 정상처리 되었으면, 발급된 평문 비밀번호 리턴
		if(result > 0) return rawPw;
		else return null;
	}

	@Override
	public List<Member> adminAccountList() {
		
		return mapper.adminAccountList();
	}

}
