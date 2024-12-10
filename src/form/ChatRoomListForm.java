// src/form/ChatRoomListForm.java

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
    private boolean personalRoomCreated = false; // 개인 채팅방 생성 여부
    private static final String CHAT_HISTORY_DIR = "chat_history";  // 채팅 기록 디렉토리
    private final WaitingRoomForm parentFrame; // WaitingRoomForm 참조

    public ChatRoomListForm(String username, Socket socket, WaitingRoomForm parentFrame) {
        this.username = username;
        this.socket = socket;
        this.parentFrame = parentFrame;

        // 설정
        setTitle("Chat Rooms - " + username);
        setSize(500, 500);  // 화면 크기 조정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 채팅방 목록
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(roomList);

        // 기본 채팅방 추가
        for (int i = 1; i <= 4; i++) {
            roomListModel.addElement("ChatRoom" + i);
        }

        // 개인 채팅방 만들기 버튼
        JButton createPersonalRoomButton = new JButton("나의 채팅방 만들기");
        createPersonalRoomButton.addActionListener(e -> handleCreatePersonalRoom());

        // 사용자 정보 버튼
        JButton userInfoButton = new JButton("사용자 정보");
        userInfoButton.addActionListener(e -> openUserInfo());

        // 로그아웃 버튼
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> logout());

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
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        topPanel.add(new JLabel("Welcome, " + username + "!"));
        bottomPanel.add(createPersonalRoomButton);
        bottomPanel.add(userInfoButton);
        bottomPanel.add(logoutButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void handleCreatePersonalRoom() {
        if (personalRoomCreated) {
            JOptionPane.showMessageDialog(this, "이미 채팅방이 만들어진 상태입니다.");
        } else {
            String personalRoomName = username + "_Room";
            roomListModel.addElement(personalRoomName);
            personalRoomCreated = true;
            JOptionPane.showMessageDialog(this, "나의 채팅방이 생성되었습니다: " + personalRoomName);
        }
    }

    private void openChatRoom(String roomName) {
        parentFrame.resetMainPanel(); // WaitingRoomForm의 초기화 메서드 호출
        parentFrame.add(new ChatRoomForm(username, roomName, parentFrame)); // ChatRoomForm 추가
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void openUserInfo() {
        JOptionPane.showMessageDialog(this,
                "ID: " + username + "\n이름: 홍길동\n(추가 정보 기능은 구현 중입니다!)",
                "사용자 정보", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "로그아웃합니다.");
        dispose();
        new LoginForm(socket); // 기존 LoginForm으로 돌아가기
    }
}
