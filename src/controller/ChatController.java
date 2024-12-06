package controller;

import service.ChatService;

import java.io.*;
import java.net.Socket;

public class ChatController {
    private final Socket socket;
    private final ChatService chatService;
    private final String chatHistoryDir;  // 채팅 기록 디렉토리 경로

    public ChatController(Socket socket, String chatHistoryDir) {
        this.socket = socket;
        this.chatHistoryDir = chatHistoryDir;
        this.chatService = new ChatService();
    }

    public void handle() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        // 클라이언트로부터 요청을 받음
        String request = in.readLine();
        if (request.startsWith("JOIN")) {
            String roomName = request.split(" ")[1];
            sendChatHistory(roomName, out);  // 채팅방에 입장할 때 채팅 기록 보내기
        } else if (request.startsWith("MESSAGE")) {
            // MESSAGE 요청 처리
            String[] parts = request.split(" ");
            String roomName = parts[1];
            String message = parts[2];
            chatService.saveMessage(roomName, message);  // 메시지 저장
            out.write("MESSAGE_SENT\n");
        }
        out.flush();
    }

    // 채팅방 기록을 클라이언트에 전송
    private void sendChatHistory(String roomName, OutputStreamWriter out) throws IOException {
        File roomFile = new File(chatHistoryDir, roomName + ".txt");
        if (roomFile.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(roomFile))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.write(line + "\n");
                }
            }
        }
    }
}
