// 목록으로 버튼 동작(메인페이지로 이동)

const goToList = document.querySelector("#goToList");

goToList.addEventListener("click", () => {
  location.href = "/";
});

// 삭제 버튼 클릭 시 동작
const deleteBtn = document.querySelector("#deleteBtn");
deleteBtn.addEventListener("click", (e) => {
  
  if(confirm("정말 삭제하시겠습니까?")) {
    // 삭제 요청
    location.href = `/todo/delete?todoNo=${e.target.dataset.todoNo}`;
  }
});

// ----------------------------------------------------
// 완료 여부 버튼 클릭 시 동작
const completeBtn = document.querySelector(".complete-btn");
completeBtn.addEventListener("click", (e) => {
  // 요소.dataset : data-* 속성에 저장된 값 반환
  // data-todo-no 세팅한 속성값 얻어오기
  // (html) data-todo-no >> js(카멜케이스) dataset.todoNo
  const todoNo = e.target.dataset.todoNo;

  let complete = e.target.innerText;  // 기존 완료 여부 값 얻어오기("Y" or "N")

  // Y -> N, N -> Y로 변경
  complete = (complete === 'Y') ? 'N' : 'Y';

  // 완료 여부 수정 요청하기
  location.href = `/todo/changeComplete?todoNo=${todoNo}&complete=${complete}`;
                // /todo/changeComplete?todoNo=1&complete=Y

});

// ----------------------------------------------------
// 수정 버튼 클릭 시 동작
const updateBtn = document.querySelector("#updateBtn");

updateBtn.addEventListener("click", (e) => {
  const todoNo = e.target.dataset.todoNo;
  location.href = `/todo/update?todoNo=${todoNo}`;
});

