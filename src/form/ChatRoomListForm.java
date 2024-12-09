package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.sql.*;

public class ChatRoomListForm extends JFrame {
    private final Socket socket;
    private final String username;
    private final DefaultListModel<JLabel> roomListModel;  // JLabel을 사용하여 채팅방 이름과 메시지를 표시
    private final JList<JLabel> roomList;
    private static final String DB_URL = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/drawchat"; // DB URL 수정
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "asdf4567";

    private JLabel selectedRoomLabel = null;  // 현재 선택된 채팅방을 추적

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
        JButton settingsButton = new JButton("채팅방삭제");
        settingsButton.addActionListener(e -> openSettings());  // 선택한 채팅방 삭제

        // 새 채팅방 생성 버튼
        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(e -> createNewRoom());  // 채팅방 생성

        // 채팅방 선택 시 해당 채팅방 열기
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JLabel selectedLabel = roomList.getSelectedValue();
                if (selectedLabel != null) {
                    selectedRoomLabel = selectedLabel;  // 선택된 채팅방을 추적
                }
            }
        });

        // 마우스 리스너 추가: 한 번 클릭 시 선택, 두 번 클릭 시 채팅방 입장
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickCount = e.getClickCount();
                if (clickCount == 1) {
                    // 한 번 클릭 시 선택된 채팅방 강조
                    JLabel selectedLabel = roomList.getSelectedValue();
                    if (selectedLabel != null) {
                        selectedRoomLabel = selectedLabel;  // 선택된 채팅방 추적
                    }
                } else if (clickCount == 2) {
                    // 두 번 클릭 시 해당 채팅방으로 입장
                    JLabel selectedLabel = roomList.getSelectedValue();
                    if (selectedLabel != null) {
                        String selectedRoom = selectedLabel.getText().split(" - ")[0];  // 채팅방 이름만 추출
                        openChatRoom(selectedRoom);
                    }
                }
            }
        });

        // 레이아웃 설정
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);  // 뒤로가기 버튼 추가

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(settingsButton);  // 삭제 버튼 추가

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(createRoomButton, BorderLayout.SOUTH);  // 채팅방 생성 버튼

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);

        add(panel);
        setVisible(true);

        // 채팅방 목록 초기화
        loadChatRoomsFromDatabase();
    }

    private void loadChatRoomsFromDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT room_name FROM chat_rooms";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    // 마지막 메시지를 함께 가져와서 채팅방 이름 옆에 표시
                    String lastMessage = getLastMessageForRoom(rs.getString("room_name"));
                    roomListModel.addElement(createRoomLabel(rs.getString("room_name"), lastMessage));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JLabel createRoomLabel(String roomName, String lastMessage) {
        // JLabel 생성 및 스타일링
        JLabel roomLabel = new JLabel(roomName + " - " + lastMessage);
        roomLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roomLabel.setForeground(Color.BLACK);

        // 새로운 메시지가 오면 텍스트 색상 변경 (예시: 빨간색)
        roomLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openChatRoom(roomName);
            }
        });

        return roomLabel;
    }

    private String getLastMessageForRoom(String roomName) {
        String lastMessage = "";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // chat_history -> chat_messages로 변경된 쿼리
            String query = "SELECT message FROM chat_messages WHERE room_name = ? ORDER BY timestamp DESC LIMIT 1";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, roomName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        lastMessage = rs.getString("message");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastMessage;
    }

    private void createNewRoom() {
        String roomName = JOptionPane.showInputDialog("Enter the new room name:");
        if (roomName != null && !roomName.isEmpty()) {
            roomListModel.addElement(createRoomLabel(roomName, ""));  // 새 채팅방 추가
            addChatRoomToDatabase(roomName);
            openChatRoom(roomName);  // 채팅방 열기
        }
    }

    private void addChatRoomToDatabase(String roomName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "INSERT INTO chat_rooms (room_name) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, roomName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openChatRoom(String roomName) {
        new ChatRoomForm(username, socket, roomName);  // 새로운 채팅방을 열기 위한 폼
    }

    private void goBack() {
        dispose();  // 현재 창 닫기
    }

    private void openSettings() {
        JLabel selectedLabel = roomList.getSelectedValue();
        if (selectedLabel != null) {
            String selectedRoom = selectedLabel.getText().split(" - ")[0];  // 채팅방 이름 추출
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the chat room: " + selectedRoom + "?",
                    "Delete Chat Room", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                deleteChatRoom(selectedRoom);  // 삭제 작업
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
        }
    }

    private void deleteChatRoom(String roomName) {
        // 삭제할 채팅방을 찾아서 삭제
        for (int i = 0; i < roomListModel.size(); i++) {
            JLabel label = roomListModel.getElementAt(i);
            if (label.getText().split(" - ")[0].equals(roomName)) {
                roomListModel.remove(i);  // 목록에서 제거
                break;
            }
        }
        // 데이터베이스에서 채팅방 삭제
        deleteChatRoomFromDatabase(roomName);
    }

    private void deleteChatRoomFromDatabase(String roomName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "DELETE FROM chat_rooms WHERE room_name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, roomName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
