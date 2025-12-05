// 쿠키에 저장된 이메일 input창에 뿌려놓기

// 쿠키에서 매개변수로 전달받은 key가 일치하는 value 얻어오는 함수
  const getCookie = (key) => {
  const cookies = document.cookie; // "K=V; K=V; ....."

  // console.log(cookies); // saveId=user01@kh.or.kr; testKey=testValue

  // cookies 문자열을 배열 형태로 변환
  const cookieList = cookies.split("; ") // ["K=V", "K=V"...]
          .map( el => el.split("=") );  // ["K", "V"]..

  //console.log(cookieList);
  // [ ['saveId', 'user01@kh.or.kr'], 
  // ['testKey', 'testValue'] ]
  
    // 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후
    //					결과 값으로 새로운 배열을 만들어서 반환

    // 배열 -> 객체로 변환 (그래야 다루기 쉽다)
    
    const obj = {}; // 비어있는 객체 선언
    
    for(let i=0; i < cookieList.length; i++) {
      const k = cookieList[i][0]; // key 값
      const v = cookieList[i][1]; // value 값
      obj[k] = v; // 객체에 추가
      // obj["saveId"] = "user01@kh.or.kr";
      // obj["testKey"]  = "testValue";
    }
    
    //console.log(obj); // {saveId: 'user01@kh.or.kr', testKey: 'testValue'}
    
    return obj[key]; // 매개변수로 전달받은 key와
            // obj 객체에 저장된 key가 일치하는 요소의 value값 반환
    
  }


// 이메일 작성 input 태그 요소
const loginEmail = document.querySelector("#loginForm input[name='memberEmail']");

//로그인 창의 이메일 input 태그가 화면상에 존재할 때
if(loginEmail != null) {  // 로그인이 안 되어있는 화면일 때
  // 쿠키 중 key 값이 "saveId"인 쿠키의 value 얻어오기
  const saveId = getCookie("saveId");
  
}