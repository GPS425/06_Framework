package edu.kh.todo.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dao.TodoDAO;

// @Transactional
// >> 트랜잭션 처리를 수행하라고 지시하는 어노테이션
// 정상 코드 수행 시 Commit
// 기본값 : Service 내부 코드 수행 중 RuntimeException 발생 시 rollback
// rollbackFor = Exception.class >> 어떠한 예외가 발생하더라도 rollback
@Transactional(rollbackFor = Exception.class)
@Service	// 비즈니스 로직(데이터 가공, 트랜잭션 처리 등) 역할 명시 + Bean 등록
public class TodoServiceImpl implements TodoService{

	@Autowired // TodoDAO와 같은 타입/상속관계 Bean 의존성 주입(DI)
	private TodoDAO dao;
	
	@Override
	public String testTitle() {
		return dao.testTitle();
	}
	
}
