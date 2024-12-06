package form;

import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class WaitingForm extends JFrame {
    public WaitingForm(String username, Socket socket) {
        setTitle("Waiting Room");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        // 환영 메시지
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

        // 채팅방 입장 버튼
        RoundedButton chatRoomButton = new RoundedButton("Enter Chat Room");
        chatRoomButton.addActionListener(e -> {
            dispose(); // 웨이팅폼 닫기
            new ChatRoomForm(username, socket); // 채팅방 폼 열기
        });

        // 로그아웃 버튼
        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose(); // 웨이팅폼 닫기
            new LoginForm(socket); // 로그인 폼으로 돌아가기
        });

        // 패널에 추가
        panel.add(welcomeLabel);
        panel.add(chatRoomButton);
        panel.add(logoutButton);

        add(panel);
        setVisible(true);
    }
}
