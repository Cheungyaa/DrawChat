// src/controller/AuthController.java

package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import service.AuthService;

public class AuthController {
    private Socket socket;
    private AuthService authService;

    // 생성자
    public AuthController(Socket socket) {
        this.socket = socket;
        this.authService = new AuthService();
    }

    // 클라이언트 요청 처리
    public void handle() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // 클라이언트로부터 데이터 읽기
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());  // 클라이언트로 데이터 보내기

        String request = in.readLine();  // 클라이언트 요청 읽기

        if (request != null) {
            if (request.startsWith("REGISTER")) {
                // REGISTER 요청 처리
                String[] parts = request.split(" ");
                if (parts.length == 8) {
                    String name = parts[1];
                    String username = parts[2];
                    String password = parts[3];
                    String email = parts[4];
                    String phone = parts[5];
                    String address = parts[6];
                    String detailAddress = parts[7];
                    
                    // 사용자 등록
                    authService.register(username, password, name, email, phone, address, detailAddress);  
                    out.write("환영합니다! 회원가입이 완료되었습니다.\n");  // 등록 성공 응답
                } else {
                    out.write("회원 정보를 모두 입력해주세요.\n");  // 잘못된 요청 응답
                }
            } else if (request.startsWith("LOGIN")) {
                // LOGIN 요청 처리
                String[] parts = request.split(" ");
                if (parts.length == 3) {
                    String username = parts[1];
                    String password = parts[2];
                    if (authService.login(username, password, socket)) {  // 로그인 인증
                        out.write("어서오세요!\n");  // 로그인 성공 응답
                    } else {
                        out.write("아이디와 비밀번호를 다시 확인해주세요.\n");  // 로그인 실패 응답
                    }
                } else {
                    out.write("다시 시도해주세요.\n");  // 잘못된 요청 응답
                }
            } else {
                out.write("잘못된 요청입니다. 다시 시도해주세요.\n");  // 잘못된 요청 처리
            }
        }
        out.flush();  // 응답 전송
    }
}
