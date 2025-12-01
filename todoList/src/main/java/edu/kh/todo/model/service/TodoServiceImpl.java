package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dao.TodoDAO;
import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;

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
	
	@Autowired
	private TodoMapper mapper;
	
	@Override
	public String testTitle() {
		return dao.testTitle();
	}

	@Override
	public Map<String, Object> selectAll() {
		
		// 1. 할 일 목록 조회
		List<Todo> todoList = mapper.selectAll();
		
		// 2. 완료된 할 일 개수 조회
		int completeCount = mapper.getCompleteCount();
		
		// 3. 위 2개 결과값을 Map으로 묶어서 반환하기
		Map<String, Object> map = new HashMap<>();
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		return map;
	}

	@Override
	public int addTodo(String todoTitle, String todoContent) {
		
		// 마이바이스에서 SQL에 전달할 수 있는 파라미터 개수는 단 1개
		// >> TodoMapper에 생성될 추상메서드의 매개변수도 1개
		// >> todoTitle, todoContent 2개인 파라미터를 전달하려면
		// Todo DTO로 묶어서 전달하면 된다!
		// 1. Todo DTO 객체를 만든다. (상자 준비)
		Todo todo = new Todo();

		// 2. 받은 두 개의 데이터를 객체 안에 저장한다. (상자에 물건 담기)
		todo.setTodoTitle(todoTitle);       // 제목을 DTO 필드에 넣음
		todo.setTodoContent(todoContent);   // 내용을 DTO 필드에 넣음

		// 3. Mapper (SQL 실행기)한테 포장된 'Todo 객체' 하나를 던져준다. (상자 하나 접수)
		// 이제 mapper는 'todo' 객체 하나만 파라미터로 받게 됨.
		return mapper.addTodo(todo);
	}

	@Override
	public Todo todoDetail(int todoNo) {
		
		return mapper.todoDetail(todoNo);
	}

	@Override
	public int todoDelete(int todoNo) {
		return mapper.todoDelete(todoNo);
	}
	
}
