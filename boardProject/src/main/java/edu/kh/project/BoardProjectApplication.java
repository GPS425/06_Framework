package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling	// Spring 스케줄러를 이용하기 위한 활성화 어노테이션
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})	
// Spring Security에서 기본 제공하는 로그인 페이지를 이용하지 않겠다
public class BoardProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectApplication.class, args);
	}

}
