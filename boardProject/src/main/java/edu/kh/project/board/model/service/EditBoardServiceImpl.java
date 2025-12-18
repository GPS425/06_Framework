package edu.kh.project.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties")
@Transactional(rollbackFor = Exception.class)
// @Slf4j
public class EditBoardServiceImpl implements EditBoardService{
	
	private final EditBoardMapper mapper;

	@Value("${my.board.web-path}")
	private String webPath;
	
	@Value("${my.board.folder-path}")
	private String folderPath;
	
	// 게시글 작성 서비스
	@Override
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws Exception{
		
		// 1. 게시글 부분(inputBoard)을 먼저
		// BOARD 테이블 INSERT 하기
		// >> INSERT된 게시글의 번호 반환받기
		
		int result = mapper.boardInsert(inputBoard);
		
		// result의 결과 행의 갯수로 다음 과정 진행 여부 결정
		// 삽입 실패 시 return 0;
		if(result == 0) return 0;
		
		// 삽입 성공 시 
		// 삽입된 게시글의 번호를 변수로 저장
		int boardNo = inputBoard.getBoardNo();
		// >> mapper.xml 에서 <selectKey> 태그를 이용해서 생성된 boardNo가
		// inputBoard에 이미 세팅되어있는 상태(얕은 복사)
		
		// 2. 업로드된 이미지가 실제로 존재할 경우
		// 업로드된 이미지만 별도로 저장하여 
		// BOARD_IMG 테이블에 삽입하는 코드 작성
		
		// 실제 업로드된 이미지만 모아둘 List 생성
		List<BoardImg> uploadList = new ArrayList<>();
		
		// images 리스트에서 하나씩 꺼내어 파일이 있는지 검사
		for(int i = 0; i < images.size(); i++) {
			
			// 실제 파일이 제출된 경우
			if(!images.get(i).isEmpty()) {
				
				// 원본명
				String originalName = images.get(i).getOriginalFilename();
				
				// 변경명
				String rename = Utility.fileRename(originalName);
				
				// 모든 값을 저장할 BoardImg DTO로 객체 생성
				BoardImg img = BoardImg.builder()
							.imgOriginalName(originalName)
							.imgRename(rename)
							.imgPath(webPath)
							.boardNo(boardNo)
							.imgOrder(i)
							.uploadFile(images.get(i))
							.build();
				
				uploadList.add(img);
			}
		}
		
		// uploadList가 비어있다 == 실제로 제출된 파일이 하나도 없다
		if(uploadList.isEmpty()) {
			return boardNo;
		}
		
		// 제출된 파일이 하나라도 있다면?
		// >> "BOARD_IMG" 테이블 INSERT + 서버에 실제로 파일 저장(transferTo())
		
		result = mapper.insertUploadList(uploadList);
		// result == 삽입된 행의 갯수 == uploadList.size()
		
		// 다중 INSERT 성공 확인
		if(result == uploadList.size()) {
			// 서버에 파일 저장
			for(BoardImg img : uploadList) {
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
			}
			
			
		} else {
			// 부분적으로 삽입 실패
			// ex) uploadList에 2개 저장
			// > 1개 삽입 성공, 1개 삽입 실패 시
			// >> 전체 서비스 실패로 판단함
			// 이전에 삽입된 내용 모두 rollback
			
			// rollback 하는 법
			// == RuntimeException 강제 발생(@Transactional 기본 RuntimeException)
			throw new RuntimeException();
			
			
		}
		
		
		return boardNo;
	}

}
