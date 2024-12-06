package form;

import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class ChatRoomForm extends JFrame {
    private final Socket socket;
    private final String username;

    public ChatRoomForm(String username, Socket socket) {
        this.username = username;
        this.socket = socket;

        setTitle("Chat Room - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 메시지 출력 영역
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);

        // 메시지 입력 영역
        JTextField messageInput = new JTextField();
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageInput.getText();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageInput.setText("");
                }
            }
        });

        // 버튼 추가
        RoundedButton sendButton = new RoundedButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageInput.getText();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageInput.setText("");
                }
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(messageArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void sendMessage(String message) {
        // 서버로 메시지 전송 로직
        System.out.println("Message sent: " + message); // TODO: 서버와 통신 구현
    }
}
