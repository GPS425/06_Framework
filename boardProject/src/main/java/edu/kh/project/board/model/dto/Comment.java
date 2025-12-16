package edu.kh.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
	private int commentNo;
	private String commentContent;
	private String commentWriteDate;
	private String commentDelFl;
	private int boardNo;
	private int memberNo;
	private int parentCommentNo;
	
	// 댓글 조회 시 MEMBER 테이블과 JOIN해서 가져올 데이터 담을 필드
	private String profileImg; 	// 회원 프로필
	private String memberNickname; // 닉네임
	
}
