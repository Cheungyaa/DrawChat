package service;

import entity.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private List<Message> messages;

    public ChatService() {
        this.messages = new ArrayList<>();
    }

    // 메시지 전송
    public void sendMessage(String sender, String content, String roomName) {
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());

        // 메시지를 메모리에도 저장
        messages.add(message);

        // 메시지를 DB에 저장
        saveMessage(roomName, sender, content);  // 채팅방 이름을 파라미터로 전달
        System.out.println("Message sent: " + content);
    }

    // 저장된 메시지 가져오기
    public List<Message> getMessages() {
        return messages;
    }

    // 메시지 DB에 저장
    public void saveMessage(String roomName, String sender, String messageContent) {
        String sql = "INSERT INTO chat_messages (room_name, sender, message, timestamp) VALUES (?, ?, ?, ?)";

        // MySQL 접속 정보
        String dbUrl = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/";  
        String dbUser = "admin";  // MySQL 사용자명
        String dbPassword = "asdf4567";  // MySQL 비밀번호

        // DB 연결 및 메시지 저장
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomName);  // 채팅방 이름
            stmt.setString(2, sender);    // 메시지 보낸 사람 (sender)
            stmt.setString(3, messageContent);   // 저장할 메시지 내용
            stmt.setLong(4, System.currentTimeMillis());  // 메시지 전송 시간 (타임스탬프)

            stmt.executeUpdate();  // 쿼리 실행
            System.out.println("Message saved to DB: " + messageContent);

        } catch (SQLException e) {
            e.printStackTrace();  // 에러 처리
        }
    }

    // DB에서 메시지 로딩 (채팅 기록)
    public List<Message> loadChatHistory(String roomName) {
        List<Message> chatHistory = new ArrayList<>();

        String sql = "SELECT sender, message, timestamp FROM chat_messages WHERE room_name = ? ORDER BY timestamp ASC";

        // MySQL 접속 정보
        String dbUrl = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/";  
        String dbUser = "admin";  // MySQL 사용자명
        String dbPassword = "asdf4567";  // MySQL 비밀번호

        // DB 연결 및 메시지 로딩
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomName);  // 채팅방 이름

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setSender(rs.getString("sender"));
                    message.setContent(rs.getString("message"));
                    message.setTimestamp(rs.getLong("timestamp"));
                    chatHistory.add(message);  // 채팅 기록에 추가
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();  // 에러 처리
        }

        return chatHistory;
    }
}
