package form;

import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatRoomForm extends JFrame {
    private final Socket socket;
    private final String username;
    private final JTextArea messageArea;
    private final JTextField messageInput;
    private PrintWriter out;  // 서버로 메시지를 보내기 위한 PrintWriter

    public ChatRoomForm(String username, Socket socket) {
        this.username = username;
        this.socket = socket;

        // 설정
        setTitle("Chat Room - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 메시지 출력 영역
        messageArea = new JTextArea();
        messageArea.setEditable(false);

        // 메시지 입력 영역
        messageInput = new JTextField();
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

        // 전송 버튼
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

        // 입력 패널
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(messageArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        // 서버 연결 초기화
        initializeServerCommunication();
    }

    private void initializeServerCommunication() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);  // 서버로 메시지 전송 준비
            // 서버로부터 메시지를 받는 스레드 생성
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Scanner in = new Scanner(socket.getInputStream());
                        while (in.hasNextLine()) {
                            String message = in.nextLine();
                            // 서버로부터 받은 메시지 출력
                            messageArea.append(message + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(username + ": " + message);  // 서버로 메시지 전송
            messageArea.append("You: " + message + "\n");  // 자기 자신에게 메시지 출력
        }
    }
}
