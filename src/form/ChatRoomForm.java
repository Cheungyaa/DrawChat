package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatRoomForm extends JFrame {
    private final Socket socket;
    private final String username;
    private final String roomName;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ChatRoomForm(String username, Socket socket, String roomName) {
        this.username = username;
        this.socket = socket;
        this.roomName = roomName;

        setTitle("Chat Room - " + roomName);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 채팅 내용 표시 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // 메시지 입력 필드
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        // 버튼: 채팅방 나가기
        JButton leaveButton = new JButton("Leave");
        leaveButton.addActionListener(e -> leaveRoom());

        // 레이아웃 설정
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(leaveButton, BorderLayout.WEST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        setVisible(true);

        // 서버와 연결하여 메시지 보내기
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);  // 사용자 이름 보내기
            out.println(roomName);  // 채팅방 이름 보내기
            listenForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            chatArea.append(username + ": " + message + "\n");
            inputField.setText("");  // 메시지 전송 후 입력창 비우기
        }
    }

    private void leaveRoom() {
        out.println("LEAVE");  // 서버에 나가겠다고 알리기
        dispose();  // 창 닫기
    }

    private void listenForMessages() {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
