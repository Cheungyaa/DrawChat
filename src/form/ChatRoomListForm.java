package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatRoomListForm extends JFrame {
    private final Socket socket;
    private final String username;
    private final DefaultListModel<String> roomListModel;
    private final JList<String> roomList;
    private static final String CHAT_HISTORY_DIR = "chat_history";  // 채팅 기록 디렉토리

    public ChatRoomListForm(String username, Socket socket) {
        this.username = username;
        this.socket = socket;

        // 설정
        setTitle("Chat Rooms - " + username);
        setSize(500, 500);  // 화면 크기 조정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 채팅방 목록
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(roomList);

        // 좌측 상단 뒤로가기 버튼
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> goBack());  // 뒤로가기 기능 처리

        // 우측 상단 설정 버튼
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> openSettings());  // 설정 창 열기

        // 새 채팅방 생성 버튼
        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(e -> createNewRoom());  // 채팅방 생성

        // 채팅방 선택 시 해당 채팅방 열기
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedRoom = roomList.getSelectedValue();
                if (selectedRoom != null) {
                    openChatRoom(selectedRoom);
                }
            }
        });

        // 레이아웃 설정
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);  // 뒤로가기 버튼 추가

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(settingsButton);  // 설정 버튼 추가

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(createRoomButton, BorderLayout.SOUTH);  // 채팅방 생성 버튼

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);

        add(panel);
        setVisible(true);
    }

    private void createNewRoom() {
        String roomName = JOptionPane.showInputDialog("Enter the new room name:");
        if (roomName != null && !roomName.isEmpty()) {
            roomListModel.addElement(roomName);
            openChatRoom(roomName);  // 채팅방 열기
        }
    }

    private void openChatRoom(String roomName) {
        new ChatRoomForm(username, socket, roomName);  // 새로운 채팅방을 열기 위한 폼
    }

    private void goBack() {
        // 뒤로가기 기능 구현, 예를 들어 이전 화면으로 돌아가는 방식
        dispose();  // 현재 창 닫기 (다른 방법을 사용할 수도 있음)
    }

    private void openSettings() {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete the chat room: " + selectedRoom + "?", 
                    "Delete Chat Room", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                deleteChatRoom(selectedRoom);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
        }
    }

    private void deleteChatRoom(String roomName) {
        // 채팅방 삭제: 목록에서 제거하고, 해당 채팅방의 기록 삭제
        roomListModel.removeElement(roomName);
        File chatRoomHistoryFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
        if (chatRoomHistoryFile.exists()) {
            chatRoomHistoryFile.delete();  // 채팅 기록 파일 삭제
        }
        JOptionPane.showMessageDialog(this, "Chat room " + roomName + " deleted.");
    }

    // 서버 재시작 후 채팅 기록을 유지하도록 채팅 기록을 파일에 저장하는 메서드
    public void saveChatHistory(String roomName, String message) {
        try {
            File roomFile = new File(CHAT_HISTORY_DIR, roomName + ".txt");
            if (!roomFile.exists()) {
                roomFile.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomFile, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 새로운 메시지가 오면 채팅방 목록 항목의 색을 빨간색으로 변경
    public void highlightRoomWithNewMessage(String roomName) {
        for (int i = 0; i < roomListModel.getSize(); i++) {
            String room = roomListModel.getElementAt(i);
            if (room.equals(roomName)) {
                roomList.setSelectionBackground(Color.RED);  // 선택된 항목 배경을 빨간색으로 변경
                break;
            }
        }
    }
}
