package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatRoomForm extends JFrame {
    private Socket socket;
    private final String username;
    private final String roomName;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private JButton sendButton;

    // 생성자
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
        sendButton = new JButton("Send");
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

        // 채팅 기록을 서버로부터 가져와서 표시하는 부분
        loadChatHistory();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
        
            // 서버에 메시지 전송, 채팅방 이름과 사용자 이름도 함께 전송
            out.println("MESSAGE " + roomName + " " + message + " " + username); 
            chatArea.append(username + ": " + message + "\n");  // 채팅 화면에 추가
            inputField.setText("");  // 메시지 전송 후 입력창 비우기

            // 메시지 전송 후 버튼을 다시 활성화
            sendButton.setEnabled(true);
        }
    }

    private void leaveRoom() {
        out.println("LEAVE");  // 서버에 나가겠다고 알리기
        dispose();  // 창 닫기
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");  // 받은 메시지 채팅 화면에 추가
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 채팅 기록을 서버로부터 가져와서 표시하는 메서드
    private void loadChatHistory() {
        // 서버로부터 해당 채팅방의 기록을 요청하고, 기록을 받아서 표시하는 코드 작성
        // 예시로 채팅 기록을 요청하는 메세지를 서버로 전송
        out.println("LOAD_HISTORY " + roomName); 

        // 여기서는 서버에서 채팅 기록을 받을 때마다 화면에 추가하는 방식입니다.
        // 서버에서 채팅 기록을 전송하면 listenForMessages()에서 처리됩니다.
    }

    // 채팅방을 닫고 다시 열 때 소켓을 유지하도록 함
    public static void openChatRoom(String username, Socket existingSocket, String roomName) {
        // 기존 소켓을 재사용하여 새 창을 여는 메서드
        new ChatRoomForm(username, existingSocket, roomName);
    }
}
