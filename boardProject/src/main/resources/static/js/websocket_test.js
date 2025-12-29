// websocket_test.js
// 웹소켓 테스트

// 1. SockJS 라이브러리 추가 : common.html에서 추가

// 2. SockJS 객체 생성
const testSock = new SockJS("/testSock");
// - WebSocket 통신을 할 수 있게끔 해주는 객체를 생성함과 동시에 자동으로 
// http://localhost/testSock으로 연결 요청을 보냄

// 3. 생성된 SockJS를 이용해서 서버에 메시지 전달
const sendMessageFn = (name, str) => {
	//JSON을 이용해서 데이터를 TEXT 형태로 전달
	const obj = {
		"name" : name,
		"str" : str
	};
	testSock.send(JSON.stringify(obj));
}
// sendMessageFn("홍길동", "누구세요")

// 4. 서버로부터 클라이언트에게 
// 웹소켓을 이용한 메시지가 전달된 경우
testSock.addEventListener("message", (e) => {
	// e : 이벤트 객체
	// e.data : 서버로부터 전달된 메시지
	const msg = JSON.parse(e.data);
	console.log(`${msg.name}의 메세지 : ${msg.str}`);
});
