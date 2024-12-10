// src/form/WaitingForm.java

package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;



public class WaitingRoomForm extends JFrame {
    private final String username;
    private JPanel mainPanel;
    private String personalRoomName = null; // 개인 채팅방 이름 저장
    private boolean hasLeftPersonalRoom = false; // 개인 채팅방 나가기 여부
    private static final String STATE_FILE = "user_state.properties"; // 상태 저장 파일

    public WaitingRoomForm(String username) {
        this.username = username;

        // 상태 복원
        loadState();

        setTitle("Draw Chat");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        resetMainPanel(); // 초기화된 메인 패널 로드
        add(mainPanel);
        setVisible(true);
    }

    public void resetMainPanel() {
        mainPanel.removeAll();

        // 상단에 로그인된 아이디 표시
        JLabel userLabel = new JLabel(username + "님 반갑습니다.", SwingConstants.CENTER);
        userLabel.setFont(new Font("고딕", Font.BOLD, 18));
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(userLabel, BorderLayout.NORTH);

        // 중앙에 Room 버튼 및 나의 채팅방 버튼 설정
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        roomPanel.setBackground(Color.DARK_GRAY);

        // 기본 ChatRoom 버튼 추가
        for (int i = 1; i <= 4; i++) {
            String roomName = "Room" + i;
            JButton roomButton = createStyledButton(roomName, 22, new Dimension(400, 80));
            roomButton.addActionListener(e -> openChatRoom(roomName));
            roomPanel.add(roomButton);
            roomPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 간격 추가
        }

        // 나의 채팅방 버튼
        JButton myRoomButton = createStyledButton("나의 채팅방", 22, new Dimension(400, 80));
        myRoomButton.addActionListener(e -> openPersonalRoom());
        roomPanel.add(myRoomButton);

        mainPanel.add(roomPanel, BorderLayout.CENTER);

        // 하단 버튼 패널 (왼쪽에서 오른쪽으로 정렬)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton createRoomButton = createStyledButton("채팅방 만들기", 14, new Dimension(120, 40));
        createRoomButton.addActionListener(e -> createPersonalRoom());

        JButton joinRoomButton = createStyledButton("채팅방 참여하기", 14, new Dimension(120, 40));
        joinRoomButton.addActionListener(e -> joinPersonalRoom());

        JButton userInfoButton = createStyledButton("사용자 정보", 14, new Dimension(120, 40));
        userInfoButton.addActionListener(e -> openUserInfo());

        JButton logoutButton = createStyledButton("로그아웃", 14, new Dimension(120, 40));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(createRoomButton);
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(userInfoButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JButton createStyledButton(String text, int fontSize, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(new Font("고딕", Font.PLAIN, fontSize));
        button.setBackground(new Color(255, 140, 0)); // 오렌지색 배경
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void openChatRoom(String roomName) {
        mainPanel.removeAll();
        mainPanel.add(new ChatRoomForm(username, roomName, this), BorderLayout.CENTER); // this 전달
        revalidate();
        repaint();
    }
    
    private void openPersonalRoom() {
        if (personalRoomName == null) {
            JOptionPane.showMessageDialog(this, "채팅방을 만들어야 합니다.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (hasLeftPersonalRoom) {
            JOptionPane.showMessageDialog(this, "나의 채팅방에 다시 참여하려면 새로 만들어야 합니다.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ChatRoomForm chatRoom = new ChatRoomForm(username, personalRoomName, this);
    
            // 참여자 동기화
            List<String> participants = chatRoom.getParticipants(); // getParticipants() 사용
            if (!participants.contains(username)) {
                participants.add(username);
            }
    
            openChatRoom(personalRoomName); // 채팅방 열기
        }
    }
    

    private void createPersonalRoom() {
        if (personalRoomName != null && !hasLeftPersonalRoom) {
            JOptionPane.showMessageDialog(this, "이미 채팅방이 만들어진 상태입니다.");
            return;
        }
        String roomName = JOptionPane.showInputDialog(this, "채팅방 이름을 입력하세요:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            personalRoomName = roomName.trim();
            hasLeftPersonalRoom = false; // 채팅방 나가기 상태 초기화
            saveState(); // 상태 저장
            JOptionPane.showMessageDialog(this, "나의 채팅방이 생성되었습니다: " + personalRoomName);
        } else {
            JOptionPane.showMessageDialog(this, "유효한 이름을 입력하세요.");
        }
    }

    private void joinPersonalRoom() {
        String targetUser = JOptionPane.showInputDialog(this, "상대방 아이디를 입력하세요:");
        if (targetUser == null || targetUser.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "유효한 아이디를 입력하세요.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // 상대방 상태 파일에서 개인 채팅방 정보 로드
        String targetRoomName = loadTargetRoom(targetUser);
        if (targetRoomName == null) {
            JOptionPane.showMessageDialog(this, targetUser + "님이 생성한 채팅방이 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ChatRoomForm chatRoom = new ChatRoomForm(username, targetRoomName, this);
    
            // 상대방 ID를 참여자로 추가
            if (!chatRoom.getParticipants().contains(targetUser)) { // 여기서 getter 사용
                chatRoom.getParticipants().add(targetUser);
            }
            openChatRoom(targetRoomName); // 채팅방 열기
        }
    }


    private String loadTargetRoom(String targetUser) {
        String targetStateFile = targetUser + "_state.properties";
        try (FileInputStream fis = new FileInputStream(targetStateFile)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties.getProperty("personalRoomName", null);
        } catch (IOException e) {
            return null; // 파일이 없거나 오류 발생 시 null 반환
        }
    }

    private void openUserInfo() {
        mainPanel.removeAll();
        mainPanel.add(new UserInfoForm(username, mainPanel, this), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void logout() {
        saveState(); // 로그아웃 시 상태 저장
        JOptionPane.showMessageDialog(this, "로그아웃합니다.");
        dispose();
        new LoginForm(null); // 로그인 화면으로 돌아가기
    }

    public void saveState() { //ChatRoomForm에서 접근하기 위해 public으로 정의
        try (FileOutputStream fos = new FileOutputStream(STATE_FILE)) {
            Properties properties = new Properties();
            properties.setProperty("personalRoomName", personalRoomName == null ? "" : personalRoomName);
            properties.setProperty("hasLeftPersonalRoom", String.valueOf(hasLeftPersonalRoom));
            properties.store(fos, "User State");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        try (FileInputStream fis = new FileInputStream(STATE_FILE)) {
            Properties properties = new Properties();
            properties.load(fis);
            String roomName = properties.getProperty("personalRoomName", "");
            personalRoomName = roomName.isEmpty() ? null : roomName; // 빈 문자열이면 null로 처리
            hasLeftPersonalRoom = Boolean.parseBoolean(properties.getProperty("hasLeftPersonalRoom", "false"));
        } catch (IOException e) {
            // 파일이 없으면 기본 상태 유지
            personalRoomName = null;
            hasLeftPersonalRoom = false;
        }
    }

    public void setLeftPersonalRoom(boolean hasLeft) {
        this.hasLeftPersonalRoom = hasLeft;
    }

    public boolean hasLeftPersonalRoom() {
        return hasLeftPersonalRoom;
    }

    public String getPersonalRoomName() {
        return personalRoomName;
    }

    public void setPersonalRoomName(String personalRoomName) {
        this.personalRoomName = personalRoomName;
    }
    


   
}
