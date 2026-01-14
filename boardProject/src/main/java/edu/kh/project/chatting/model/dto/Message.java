package edu.kh.project.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
	private int messageNo;			// 메세지 번호
	private String messageContent;	// 메세지 내용
	private String readFl;			// 메세지 읽음 여부
	private int senderNo;			// 보낸 회원
	private int chattingRoomNo;		// 채팅방 번호
	private String sendTime;		// 보낸 시간
	private int targetNo;			// 웹소켓을 이용한 메시지 값 세팅 시 필요
	

}
