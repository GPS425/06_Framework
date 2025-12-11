package edu.kh.project.myPage.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

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

	/** 파일 정보를 DB에 삽입하는 SQL (insert)
	 * @param uf
	 * @return
	 */
	int insertUploadFile(UploadFile uf);

	/** 파일 목록 조회 SQL
	 * @param memberNo
	 * @return
	 */
	List<UploadFile> fileList(int memberNo);

	/** 프로필 이미지 변경 SQL
	 * @param member
	 * @return
	 */
	int profile(Member member);

}
