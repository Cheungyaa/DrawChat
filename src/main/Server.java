package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final String CHAT_HISTORY_DIR = "chat_history";  // 채팅 기록 저장 경로
    private static Map<String, Set<ClientHandler>> rooms = new HashMap<>(); // 채팅방별 클라이언트 목록 관리

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("서버가 실행 중입니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();  // 클라이언트 핸들러 시작
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;
        private String roomName;
        
        // 채팅 기록 파일
        private File chatHistoryFile;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                // 사용자 이름 받기
                userName = in.readLine();  // 사용자가 처음 연결될 때 사용자명 받기
                out.println("Hello " + userName);

                // 채팅방 선택 받기
                roomName = in.readLine();  // 클라이언트가 원하는 채팅방을 입력받음
                synchronized (rooms) {
                    rooms.putIfAbsent(roomName, new HashSet<>());
                    rooms.get(roomName).add(this);
                }

                // 채팅 기록 로딩
                loadChatHistory();

                // 클라이언트로부터 메시지 처리
                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(message);  // 채팅방 내 모든 클라이언트에게 메시지 전송
                    saveChatHistory(message);  // 메시지를 채팅 기록에 저장
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    synchronized (rooms) {
                        rooms.get(roomName).remove(this);  // 클라이언트가 나가면 방에서 제거
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (rooms) {
                for (ClientHandler client : rooms.get(roomName)) {
                    client.out.println(userName + ": " + message);
                }
            }
        }

        private void saveChatHistory(String message) {
            if (chatHistoryFile == null) {
                chatHistoryFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatHistoryFile, true))) {
                writer.write(userName + ": " + message);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadChatHistory() {
            chatHistoryFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
            if (chatHistoryFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.println(line);  // 이전 메시지를 클라이언트로 전송
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
