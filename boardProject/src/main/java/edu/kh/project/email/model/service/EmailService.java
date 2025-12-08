package edu.kh.project.email.model.service;

import java.util.Map;

public interface EmailService {

	/** 이메일 보내기 서비스
	 * @param type : 무슨 이메일을 발송할 지 구분할 key로 쓰임
	 * @param email
	 * @return 
	 */
	String sendEmail(String type, String email);

	/** 입력받은 이메일, 인증 번호가 DB에 있는지 조회하는 서비스
	 * @param map (email, authKey)
	 * @return
	 */
	int checkAuthKey(Map<String, String> map);

}
