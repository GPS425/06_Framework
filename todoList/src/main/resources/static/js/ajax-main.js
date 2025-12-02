// HTML 상 요소 얻어와서 변수에 저장
// 할 일 개수 관련 요소
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");
const reloadBtn = document.querySelector("#reloadBtn");
// 할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");
// 할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");
// 할 일 상세 조회 관련 요소
const popupLayer = document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");
const popupClose = document.querySelector("#popupClose");
// 상세 조회 팝업레이어 관련 버튼 요소
const changeComplete = document.querySelector("#changeComplete");
const updateView = document.querySelector("#updateView");
const deleteBtn = document.querySelector("#deleteBtn");
// 수정 레이어 관련 요소
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");

/*
fetch() API
- 비동기 요청을 수행하는 최신 JS API중 하나.

- Promise 는 비동기 작업의 결과를 처리하는 방법으로
- 어떤 결과가 올 지는 모르지만 반드시 결과를 보내주겠다는 약속.
>> 비동기 작업이 맞이할 완료 또는 실패와 그 결과값을 나타냄.
>> 비동기 작업이 완료되었을 때 실행할 콜백함수를 지정하고,
   해당 작업의 성공 또는 실패 여부를 처리할 수 있도록 함.

Promise 객체는 세 가지 상태를 가짐
- 대기(pending) : 비동기 작업이 아직 완료되지 않은 상태
- 이행(fulfilled) : 비동기 작업이 성공적으로 완료된 상태
- 거부(rejected) : 비동기 작업이 실패한 상태


*/

// 전체 Todo 개수 조회 및 html 화면상에 출력하는 함수
function getTotalCount() {
  // 비동기로 서버에 전체 Todo 개수를 조회하는 요청
  // fetch() API로 코드 작성
  fetch("/ajax/totalCount")  // 서버로 "/ajax/totalCount"로 GET 요청
  // 첫 번째 then (응답을 처리하는 역할)
  .then(response => {
    // 서버에서 응답을 받으면, 이 응답(response)을 텍스트 형식으로 변환하는 콜백함수
    // 매개변수 response : 비동기 요청에 대한 응답이 담긴 객체
    
    // response.text() : 응답 데이터를 문자열/숫자 형태로 변환한 결과를 가지는
    //                   Promise 객체 반환
    return response.text();

  // 두 번째 then (첫 번째에서 변환된 데이터를 처리하는 역할)
  }).then(result => { // 첫 번째 콜백함수가 완료된 후 호출되는 콜백함수
    // 매개변수로 전달되어진 데이터(result)를 받아서 
    // 어떤식으로 처리할 지 정의
    // >> #totalCount인 span 태그의 내용으로 result 값 삽입
    totalCount.innerText = result;
  });
}

// 완료된 할 일 갯수 조회 및 html 화면상에 출력하는 함수
function getCompleteCount() {

  fetch("/ajax/completeCount")
  .then( response => response.text() )
  .then( result => {

    // #completeCount 요소에 내용으로 result 값 출력
    completeCount.innerText = result;
  });
}

// 할 일 추가 버튼 클릭 시 동작
addBtn.addEventListener("click", () => {
  if(todoTitle.value.trim().length === 0 || todoContent.value.trim().length === 0) {
    alert("제목이나 내용은 비어있을 수 없습니다!");
    return;
  }

  // POST 방식 fetch 비동기 요청 보내기
  // - 요청 주소 : "/ajax/add"
  // - 데이터 전달 방식(method) : "POST"
  // - 전달 데이터 : todoTitle, todoContent 값
  // JS <-> JAVA
  // JSON ( JavaScript Object Notation ) : 데이터를 표현하는 문법
  /*
    { "name" : "홍길동",
    "age" : 20,
    "skill" : ["javscript", "java"] } 
  */

  
  // todoTitle과 todoContent를 저장한 JS 객체
  const param = {
    // key : value
    "todoTitle" : todoTitle.value,
    "todoContent" : todoContent.value
  } 

  fetch("/ajax/add", {
    // key : value
    method : "POST", // POST 방식 요청
    headers : { "Content-Type" : "application/json" }, // 요청 데이터의 형식을 JSON 으로 지정
    body : JSON.stringify(param) // 요청본문 : param이라는 JS 객체를 JSON(string)으로 변환
  })
  .then( resp => resp.text() )  
  .then( result => {  
    if(result > 0) {   // 추가 성공
      alert("추가 성공");
      // 추가 성공했다면 작성했던 제목, 내용 인풋 지우기
      todoTitle.value = "";
      todoContent.value = "";

      // 할 일이 새롭게 추가되었으므로
      // >> 전체 Todo 갯수 조회하는 함수 재호출
      getTotalCount();
      
      // >> 전체 Todo 목록 조회하는 함수 재호출 예정(바로 아래 함수)
      selectTodoList();
    } else {  // 추가 실패
      alert("비응신 코딩 접어라 ㅋㅋㅋ");
    }
  });
});

// 비동기로 할 일 전체 목록을 조회 & HTML 화면에 출력까지 하는 함수
const selectTodoList = () => {
  fetch("/ajax/selectList")
  .then( resp => resp.json() )  // 응답 결과를 JSON으로 받음
  .then(todoList => {
    // 매개변수 todoList:
    // 첫 번째 then에서 resp.text() / resp.json() 했냐에 따라
    // 단순 텍스트이거나, JS Object일 수 있음.
    console.log(todoList);
    // --------------------------------------------
    // 기존에 출력되어 있던 할 일 목록을 모두 비우기
    tbody.innerHTML = "";
      // tbody에 tr/td 요소를 생성해서 내용 추가
      for(let todo of todoList) { // 향상된 for문
        // tr 태그 생성
        const tr = document.createElement("tr"); // <tr></tr>
        // JS 객체에 존재하는 key 모음 배열 생성
        const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];
        for(let key of arr) {
          const td = document.createElement("td"); // <td></td>
          // 제목인 경우
          if(key === 'todoTitle') {
            const a = document.createElement("a"); // a태그 생성
            a.innerText = todo[key]; // todo["todoTitle"]
            a.href = "/ajax/detail?todoNo=" + todo.todoNo;
            td.append(a);
            tr.append(td);
            // a태그 클릭 시 페이지 이동 막기(비동기 요청 사용을 위해)
            a.addEventListener("click", e => {
              e.preventDefault(); // 기본 이벤트 방지
              // 할 일 상세 조회 비동기 요청 함수 호출
              selectTodo(e.target.href);
            });
            continue;
          }
          // 제목이 아닌 경우
          td.innerText = todo[key]; // todo['todoNo']
          tr.append(td); // tr의 마지막요소 현재 td 추가하기
        }
        // tbody 의 자식으로 tr 추가
        tbody.append(tr);
      }
  })
}

getTotalCount();
getCompleteCount();
selectTodoList();


