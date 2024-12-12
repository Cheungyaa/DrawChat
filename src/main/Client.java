package main;

import form.LoginForm;
import form.DrawingForm;  // DrawingForm 임포트

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 서버에 연결
                Socket socket = new Socket("127.0.0.1", 9090); // 서버 주소와 포트 설정

                // 로그인 폼을 띄우고 로그인 성공 후 작업 정의
                LoginForm loginForm = new LoginForm(socket);
                loginForm.setVisible(true);

                

            } catch (IOException e) {
                System.err.println("서버 연결 실패: " + e.getMessage());
                e.printStackTrace(); // 더 자세한 예외 정보 출력
            }
        });
    }

    // 로그인 성공 후 동작 정의
    private static void onLoginSuccess(Socket socket, LoginForm loginForm) {
        // 로그인 폼 닫기
        SwingUtilities.invokeLater(loginForm::dispose);

        // 로그인 성공 메시지 출력
        System.out.println("로그인 성공!");

        // 그림판 열기
        openDrawingBoard(socket);

        // 서버로부터 실시간 데이터 수신 스레드 시작
        new Thread(() -> listenToServer(socket)).start();
    }

    // 서버로부터 실시간 데이터 수신
    private static void listenToServer(Socket socket) {
        try {
            // 서버에서 보내는 객체 데이터를 읽기 위한 InputStream 생성
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // 서버에서 보내는 데이터를 읽음
                Object data = in.readObject();

                // 서버로부터 받은 데이터를 처리하고 UI에 반영
                // 예: 채팅 메시지나 그림판 데이터를 받아 처리
                System.out.println("서버로부터 데이터 수신: " + data);

                // 예시로, 데이터를 처리하는 UI 업데이트 함수 호출 (사용자 정의)
                // 예를 들어: updateChat(data) 또는 updateDrawing(data)
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("서버 연결 종료.");
        }
    }

    // 그림판 열기
    private static void openDrawingBoard(Socket socket) {
        SwingUtilities.invokeLater(() -> {
            // 그림판 화면으로 이동
            DrawingForm drawingForm = new DrawingForm();
            drawingForm.setVisible(true);
        });
    }
}