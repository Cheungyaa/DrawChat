package main;

import handler.ClientHandler;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 9090; // 서버 포트
    private static final String CHAT_HISTORY_DIR = "chat_history";  // 채팅 기록 저장 경로
    private static final Map<String, Set<ClientHandler>> rooms = new HashMap<>(); // 채팅방별 클라이언트 목록 관리
    private static final CopyOnWriteArrayList<ObjectOutputStream> drawingClients = new CopyOnWriteArrayList<>(); // 그림판 클라이언트 목록

    public static void main(String[] args) {
        // 기본 채팅방 생성
        initializeDefaultRooms();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 실행 중입니다...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 수락

                // 클라이언트 핸들러 생성
                ClientHandler clientHandler = new ClientHandler(clientSocket, rooms, CHAT_HISTORY_DIR);

                // 그림판 기능 활성화 확인
                if (isDrawingClient(clientSocket)) {
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    drawingClients.add(out); // 그림판 클라이언트 목록에 추가
                    // 그림판 클라이언트에 대한 스레드 처리
                    new Thread(new DrawingClientHandler(clientSocket, out)).start();
                } else {
                    // 일반 채팅 클라이언트 처리
                    new Thread(clientHandler).start();
                }
            }
        } catch (IOException e) {
            System.err.println("서버 연결 실패: " + e.getMessage());
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

    /**
     * 그림판 클라이언트를 처리하는 스레드 클래스입니다.
     */
    private static class DrawingClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectOutputStream out;

        public DrawingClientHandler(Socket clientSocket, ObjectOutputStream out) {
            this.clientSocket = clientSocket;
            this.out = out;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                while (true) {
                    // 그림판 데이터 수신
                    Object data = in.readObject();

                    // 모든 클라이언트에 브로드캐스트
                    broadcastDrawingData(data);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("그림판 클라이언트 연결 종료.");
            } finally {
                // 예외가 발생하거나 연결이 종료되면 클라이언트 목록에서 제거
                synchronized (drawingClients) {
                    drawingClients.remove(out);
                }
            }
        }
    }

    /**
     * 그림판 데이터를 브로드캐스트합니다.
     */
    private static void broadcastDrawingData(Object data) {
        synchronized (drawingClients) {
            for (ObjectOutputStream out : drawingClients) {
                try {
                    out.writeObject(data); // 데이터 전송
                    out.flush(); // 출력 스트림 비우기
                } catch (IOException e) {
                    // 전송 오류 시 해당 클라이언트를 목록에서 제거
                    drawingClients.remove(out);
                }
            }
        }
    }

    /**
     * 그림판 클라이언트 여부를 확인합니다.
     *
     * @param clientSocket 클라이언트 소켓
     * @return 그림판 클라이언트 여부
     */
    private static boolean isDrawingClient(Socket clientSocket) {
        // 예시 조건: 포트 번호가 짝수일 경우 그림판 클라이언트로 처리
        return clientSocket.getPort() % 2 == 0;
    }
}
