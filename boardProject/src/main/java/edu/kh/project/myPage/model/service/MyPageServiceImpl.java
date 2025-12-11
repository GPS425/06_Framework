package edu.kh.project.myPage.model.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Service
@Transactional(rollbackFor = Exception.class)
// @Slf4j
public class MyPageServiceImpl implements MyPageService{
	
	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		// A^^^B^^^C 형태로 가공
		
		// 주소가 입력되었을 때
		if(!inputMember.getMemberAddress().equals(",,")) {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
			// 주소가 입력되지 않았을 때
			inputMember.setMemberAddress(null);
		}
		
		// inputMember : 수정 닉네임, 수정 전화번호, 수정 주소, 회원 번호
		
		return mapper.updateInfo(inputMember);
	}

	@Override
	public int changePw(String currentPw, String newPw, int memberNo) {
	    
	    // 1. [DB 조회] 현재 로그인한 회원의 "암호화된 비밀번호"를 가져옴
	    // why? BCrypt 검사를 하려면 DB에 있는 정답지(암호문)가 필요하니까.
	    String pw = mapper.selectPw(memberNo);
	    
	    // 2. [비밀번호 검증] 입력한 현재 비번이 맞는지 확인
	    // - currentPw : 사용자가 입력한 쌩 비밀번호 (예: "1234")
	    // - pw        : DB에서 가져온 암호화된 비밀번호 (예: "$2a$10$...")
	    // matches()가 내부적으로 소금 쳐서 비교함. 틀리면 !가 true가 돼서 들어옴.
	    if( !bcrypt.matches(currentPw, pw) ) {
	        return 0; // 비밀번호 불일치 -> 0 반환하고 함수 종료(빠꾸)
	    }
	    
	    // 3. [새 비밀번호 암호화] 검증 통과했으니 변경할 비번 암호화
	    // - newPw : 사용자가 입력한 새 쌩 비밀번호 (예: "5678")
	    // - encode() : 이걸 DB에 저장할 수 있게 외계어($2a$10$...)로 바꿈
	    String newEncPw = bcrypt.encode(newPw);
	    
	    // 4. [포장] DB에 보낼 택배 상자(Member 객체) 세팅
	    Member member = new Member();
	    member.setMemberNo(memberNo); // 누가 (WHERE 조건에 쓸 번호)
	    member.setMemberPw(newEncPw); // 뭘로 (SET에 쓸 암호화된 새 비번)
	    
	    // 5. [DB 수정] 포장한 상자를 Mapper에게 던져서 UPDATE 실행
	    // - 성공 시 1, 실패 시 0 반환됨
	    return mapper.changePw(member);
	}

	// 회원 탈퇴 서비스
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 1. 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String encPw = mapper.selectPw(memberNo);
		
		// 2. 입력받은 비밀번호 & 암호화된 DB 비밀번호 같은지 비교
		// 다를 경우
		if(!bcrypt.matches(memberPw, encPw)) {
			return 0;
		}
		
		// 3. 일치한다면
		return mapper.secession(memberNo);
	}

	// 파일 업로드 테스트 1
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		
		if(uploadFile.isEmpty()) { // 업로드한 파일이 없을 경우
			return null;
			
		}	
		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명으로 서버에 저장
		uploadFile.transferTo(new File("C:/uploadFiles/test/" + uploadFile.getOriginalFilename()));
		
		// C:/uploadFiles/test/해피캣.jpg
		
		// 웹에서 해당 파일에 접근할 수 있는 경로를 만들어 반환
		// 이미지가 최종 저장된 서버 컴퓨터상의 경로
		// C:/uploadFiles/test/파일명.jpg
		
		// 클라이언트가 브라우저에서 해당 이미지를 보기 위해 요청하는 경로
		// ex) <img src="경로">
		// /myPage/file/파일명.jpg >> <img src="/myPage/file/파일명.jpg">
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
		
	}

	// 파일 업로드 테스트 2 (서버 저장, DB 저장)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws Exception{
		
		// 업로드된 파일이 없다면
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		
		// MultipartFile이 제공하는 메서드
		// - isEmpty() : 업로드된 파일이 없을 경우 True, 있을 경우 False 반환
		// - getSize() : 파일 크기 반환(Byte)
		// - getOriginalFileName() : 원본 파일명 반환
		// - transferTo(경로) : 메모리 또는 임시저장 경로에 업로드된 파일을 원하는 경로에 실제 전송
		//						(서버의 어떤 폴더에 저장을 할 지 지정할 수 있음)
		
		
		// 업로드된 파일이 있다면
		// 1. 서버에 저장될 서버 폴더 경로 만들기
		// 파일이 저장될 서버 폴더 경로
		String folderPath = "C:/uploadFiles/test/";
		
		// 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소(요청 주소)
		String webPath = "/myPage/file/";
		
		// 2. DB에 전달할 데이터를 DTO로 묶어서 INSERT
		// webPath, memberNo, 원본파일명, 변경된파일명
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());

		// Builder 패턴을 이용해서 UploadFile 객체 생성해보기
		// 장점 1) 반복되는 참조변수명, set 구문 생략
		// 장점 2) method chaining을 이용하여 한 줄로 작성 가능
		UploadFile uf = UploadFile.builder().memberNo(memberNo).filePath(webPath)
				.fileOriginalName(uploadFile.getOriginalFilename()).fileRename(fileRename)
				.build();
		
		int result = mapper.insertUploadFile(uf);
		
		// 3. 삽입(INSERT) 성공 시 파일을 지정된 서버 폴더에 저장
		if(result == 0) return 0;	// 삽입 실패 시
		
		// 삽입 성공 시
		// C:/uploadFiles/test/변경된파일명 으로 파일을 서버컴퓨터에 저장
		uploadFile.transferTo(new File(folderPath + fileRename));
		// C:/uploadFiles/test/20251211100330_00001.jpg
		
		return result;
	}
}
