package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Spring EL 같은 경우 DTO 객체 출력할 때 getter 가 필수 작성 되어있어야 함!
// -> ${Student.name} == ${Student.getName()}
// 내부적으로 해당 DTO의 Getter를 호출하고 있기 때문!

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	private String studentNo; 	// 학생번호
	private String name; 		// 이름
	private int age;			// 나이
}
