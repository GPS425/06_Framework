package edu.kh.project.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChattingRoom {
	private int chattingRoomNo;	// 채팅방 번호
	private String lastMessage;	// 채팅방의 마지막 메세지
	private String sendTime; 	// 마지막 메세지 보낸 시간
	private int targetNo;		// 채팅방 대상자 회원 번호
	private String targetNickname;	// 대상자 닉네임
	private String targetProfile;	// 대상자 프로필 이미지 경로
	private int notReadCount;	// 채팅방 읽지않은 메세지 개수
}
