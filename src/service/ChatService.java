// src/service/ChatService.java

package service;

import java.util.*;
import java.io.*;

/**
 * ChatService 클래스는 채팅방을 생성, 관리, 검색하는 역할을 합니다.
 * 기본 채팅방은 서버 시작 시 자동으로 생성됩니다.
 */
public class ChatService {
    private final Map<String, ChatRoom> chatRooms = new HashMap<>();
    private final Map<String, String> userPersonalRoomMap = new HashMap<>(); // 사용자별 개인 채팅방

    public ChatService() {
        // 기본 채팅방 생성
        for (int i = 1; i <= 4; i++) {
            createRoom("ChatRoom" + i);
        }
    }

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param roomName 생성할 채팅방 이름
     * @return 생성된 ChatRoom 객체
     */
    public synchronized ChatRoom createRoom(String roomName) {
        if (chatRooms.containsKey(roomName)) {
            return null; // 이미 존재하는 채팅방
        }
        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRooms.put(roomName, chatRoom);
        return chatRoom;
    }

    /**
     * 개인 채팅방을 생성합니다. 한 사용자는 하나의 개인 채팅방만 가질 수 있습니다.
     *
     * @param creatorUsername 생성자의 사용자 이름
     * @return 생성된 ChatRoom 객체
     */
    public synchronized ChatRoom createPersonalRoom(String creatorUsername) {
        if (userPersonalRoomMap.containsKey(creatorUsername)) {
            return null; // 이미 개인 채팅방을 생성한 경우
        }
        String roomName = creatorUsername + "_Room";
        ChatRoom personalRoom = createRoom(roomName);
        if (personalRoom != null) {
            userPersonalRoomMap.put(creatorUsername, roomName);
        }
        return personalRoom;
    }

    /**
     * 메시지를 저장합니다.
     *
     * @param roomName 채팅방 이름
     * @param message 저장할 메시지
     */
    public void saveMessage(String roomName, String message) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        if (chatRoom != null) {
            chatRoom.saveMessage(message);
        } else {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomName);
        }
    }

    /**
     * 특정 채팅방의 채팅 기록을 반환합니다.
     *
     * @param roomName 채팅방 이름
     * @return 채팅 기록
     */
    public List<String> getChatHistory(String roomName) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        if (chatRoom != null) {
            return chatRoom.getChatHistory();
        } else {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomName);
        }
    }

    /**
     * 모든 채팅방을 반환합니다.
     *
     * @return 채팅방 이름과 ChatRoom 객체의 맵
     */
    public Map<String, ChatRoom> getAllRooms() {
        return chatRooms;
    }

    /**
     * ChatRoom 클래스는 채팅방 데이터를 관리합니다.
     */
    public static class ChatRoom {
        private final String roomName;
        private final List<String> messages = new ArrayList<>();
        private final File chatHistoryFile;

        public ChatRoom(String roomName) {
            this.roomName = roomName;
            this.chatHistoryFile = new File("chat_history/" + roomName + ".txt");
            try {
                if (!chatHistoryFile.exists()) {
                    chatHistoryFile.getParentFile().mkdirs();
                    chatHistoryFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 메시지를 저장합니다.
         *
         * @param message 저장할 메시지
         */
        public void saveMessage(String message) {
            messages.add(message);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatHistoryFile, true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 채팅 기록을 반환합니다.
         *
         * @return 채팅 기록
         */
        public List<String> getChatHistory() {
            List<String> history = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return history;
        }

        public String getRoomName() {
            return roomName;
        }
    }
}
