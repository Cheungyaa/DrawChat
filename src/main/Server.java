// src/main/Server.java

package main;

import handler.ClientHandler;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 9090; // 서버 포트
    private static final String CHAT_HISTORY_DIR = "chat_history";  // 채팅 기록 저장 경로
    private static final Map<String, Set<ClientHandler>> rooms = new HashMap<>(); // 채팅방별 클라이언트 목록 관리

    public static void main(String[] args) {
        // 기본 채팅방 생성
        initializeDefaultRooms();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 실행 중입니다...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, rooms, CHAT_HISTORY_DIR)).start(); // 클라이언트 핸들러 시작
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 기본 채팅방을 생성합니다.
     */
    private static void initializeDefaultRooms() {
        synchronized (rooms) {
            for (int i = 1; i <= 4; i++) {
                rooms.put("ChatRoom" + i, new HashSet<>());
            }
        }
        System.out.println("기본 채팅방 생성 완료: ChatRoom1 ~ ChatRoom4");
    }
}
