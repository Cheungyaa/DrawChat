// src/form/ChatRoomForm.java

package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomForm extends JPanel {
    private final String username;
    private final String roomName;
    private final WaitingRoomForm parentFrame;
    private JTextArea chatArea;
    private JTextField inputField;
    private final static List<String> participants = new ArrayList<>();
    private static final String CHAT_HISTORY_DIR = "chat_history"; // 채팅 기록 저장 디렉토리
    private boolean hasLeft = false; // 나가기 상태 체크
    private static final int MAX_ROOM_NAME_LENGTH = 10; // 채팅방 이름 최대 표시 길이

    public ChatRoomForm(String username, String roomName, WaitingRoomForm parentFrame) {
        this.username = username;
        this.roomName = roomName;
        this.parentFrame = parentFrame; // WaitingRoomForm의 인스턴스 사용

        setLayout(new BorderLayout());

        // 상단에 채팅방 이름과 버튼들
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String displayRoomName = getTruncatedRoomName(roomName); // 축약된 채팅방 이름 가져오기
        JLabel roomLabel = new JLabel("채팅방: " + displayRoomName);

        // 중앙에 채팅 내용 표시
        chatArea = new JTextArea(); //chatArea 초기화
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 버튼들 추가
        JButton drawButton = new JButton("그림그리기");
        drawButton.addActionListener(e -> openDrawingTool());

        JButton leaveButton = new JButton("나가기");
        leaveButton.addActionListener(e -> leaveChatRoom());

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> returnToRoomList());

        JButton participantsButton = new JButton("참여자 보기");
        participantsButton.addActionListener(e -> showParticipants());

        topPanel.add(roomLabel);
        topPanel.add(drawButton);
        topPanel.add(leaveButton);
        topPanel.add(backButton);
        topPanel.add(participantsButton);

        add(topPanel, BorderLayout.NORTH);

        // 하단에 메시지 입력 필드와 보내기 버튼
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("보내기");
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 참여자 목록에 자신 추가 및 채팅방 입장 메시지
        if (!participants.contains(username)) {
            participants.add(username); 
        }
        chatArea.append(username + "님이 입장했습니다.\n");
    
        // 채팅 기록 로드
        if (!hasLeft) {
            loadChatHistory();
        }

    }

    private String getTruncatedRoomName(String roomName) {
        if (roomName.length() > MAX_ROOM_NAME_LENGTH) {
            return roomName.substring(0, MAX_ROOM_NAME_LENGTH - 3) + "...";
        }
        return roomName;
    }

    private void returnToRoomList() {
        parentFrame.resetMainPanel(); // WaitingRoomForm의 메인 화면으로 돌아가기
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            chatArea.append(username + ": " + message + "\n");
            saveMessageToHistory(username + ": " + message); // 채팅 기록 저장
            inputField.setText("");
        }
    }

    private void loadChatHistory() {
        File chatHistoryFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
        if (chatHistoryFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    chatArea.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveMessageToHistory(String message) {
        File chatHistoryDir = new File(CHAT_HISTORY_DIR);
        if (!chatHistoryDir.exists()) {
            chatHistoryDir.mkdirs();
        }

        File chatHistoryFile = new File(chatHistoryDir, roomName + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatHistoryFile, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void leaveChatRoom() {
        hasLeft = true;
        participants.remove(username); // 자신을 참여자 목록에서 제거
        chatArea.append(username + "님이 나갔습니다.\n");
    
        // 상태 동기화 및 개인 채팅방 초기화
        if (roomName.equals(parentFrame.getPersonalRoomName())) {
            parentFrame.setLeftPersonalRoom(true);
            parentFrame.setPersonalRoomName(null);
        }
    
        // 채팅 기록 삭제
        File chatHistoryFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
        if (chatHistoryFile.exists()) {
            chatHistoryFile.delete();
        }
    
        // 상태 저장
        parentFrame.saveState();
    
        JOptionPane.showMessageDialog(this, roomName + " 채팅방을 나갔습니다.");
        returnToRoomList(); // 메인 화면으로 돌아감
    }

    private void openDrawingTool() {
        // 그림 그리기 기능: 추후 구현 가능
        JOptionPane.showMessageDialog(this, "그림 그리기 기능은 준비 중입니다!");
    }

    public void showParticipants() {
        JOptionPane.showMessageDialog(this, "참여자 목록:\n" + String.join("\n", participants), "참여자", JOptionPane.INFORMATION_MESSAGE);
    }

    public static List<String> getParticipants() {
        return participants;
    }
    
}