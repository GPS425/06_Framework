package edu.kh.project.myPage.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	/** 회원 수정 SQL 실행
	 * @param inputMember
	 * @return
	 */
	int updateInfo(Member inputMember);

	/** 로그인한 회원 확인 SQL 실행
	 * @param memberNo
	 * @return
	 */
	String selectPw(int memberNo);

	/** 비밀번호 변경 SQL 실행
	 * @param member
	 * @return
	 */
	int changePw(Member member);

	/** 회원 탈퇴 SQL(update)
	 * @param memberNo
	 * @return
	 */
	int secession(int memberNo);

}
