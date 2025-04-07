// src/handler/ClientHandler.java

package handler;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ClientHandler는 각 클라이언트를 처리하며, 채팅방과 상호작용합니다.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Map<String, Set<ClientHandler>> rooms;
    private final String chatHistoryDir;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String roomName;
    private File chatHistoryFile;

    public ClientHandler(Socket socket, Map<String, Set<ClientHandler>> rooms, String chatHistoryDir) {
        this.socket = socket;
        this.rooms = rooms;
        this.chatHistoryDir = chatHistoryDir;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 사용자 이름 받기
            out.println("Enter your username:");
            username = in.readLine();
            out.println("Hello " + username);

            // 채팅방 선택 받기
            out.println("Available rooms: " + rooms.keySet());
            out.println("Enter room name to join or type 'CREATE' for a personal room:");
            roomName = in.readLine();

            if (roomName.equalsIgnoreCase("CREATE")) {
                roomName = createPersonalRoom(username);
            } else {
                joinRoom(roomName);
            }

            // 채팅 기록 로딩
            loadChatHistory();

            // 클라이언트 메시지 처리
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("LEAVE")) {
                    leaveRoom();
                    break;
                }
                broadcastMessage(message);
                saveChatHistory(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private String createPersonalRoom(String creatorUsername) {
        String personalRoomName = creatorUsername + "_Room";
        synchronized (rooms) {
            if (!rooms.containsKey(personalRoomName)) {
                rooms.put(personalRoomName, new HashSet<>());
                out.println("Personal room created: " + personalRoomName);
            } else {
                out.println("You already have a personal room.");
            }
        }
        joinRoom(personalRoomName);
        return personalRoomName;
    }

    private void joinRoom(String roomName) {
        synchronized (rooms) {
            rooms.putIfAbsent(roomName, new HashSet<>());
            rooms.get(roomName).add(this);
        }
        out.println("Joined room: " + roomName);
    }

    private void leaveRoom() {
        synchronized (rooms) {
            if (rooms.containsKey(roomName)) {
                rooms.get(roomName).remove(this);
                out.println("You have left the room: " + roomName);
            }
        }
    }

    private void broadcastMessage(String message) {
        synchronized (rooms) {
            for (ClientHandler client : rooms.get(roomName)) {
                client.out.println(username + ": " + message);
            }
        }
    }

    private void saveChatHistory(String message) {
        if (chatHistoryFile == null) {
            chatHistoryFile = new File(chatHistoryDir, roomName + ".txt");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatHistoryFile, true))) {
            writer.write(username + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        chatHistoryFile = new File(chatHistoryDir, roomName + ".txt");
        if (chatHistoryFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line); // 이전 메시지를 클라이언트로 전송
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanup() {
        try {
            leaveRoom();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
#주석처리기반시스템추가
