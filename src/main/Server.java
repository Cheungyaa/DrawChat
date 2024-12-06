package main;

import service.AuthService;
import controller.AuthController;
import controller.ChatController;
import controller.DrawingController;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        AuthService authService = new AuthService();

        // 데이터베이스 연결 상태 확인
        if (authService.checkConnection()) {
            System.out.println("서버 시작: 데이터베이스 연결 성공");
        } else {
            System.out.println("서버 시작: 데이터베이스 연결 실패");
            return; // 데이터베이스 연결 실패 시 서버 실행 중지
        }

        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("서버가 실행중입니다.");

            while (true) {
                // 클라이언트 연결 대기
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트와 연결되었습니다.");

                // 각 클라이언트에 대한 처리 스레드 시작
                new Thread(() -> {
                    try {
                        // 각 컨트롤러 처리
                        new AuthController(clientSocket).handle();
                        new ChatController(clientSocket).handle();
                        new DrawingController(clientSocket).handle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 서버 종료 시 리소스 정리
            AuthService.cleanup();
        }
    }
}
