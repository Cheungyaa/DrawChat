package controller;

import service.ChatService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatController {
    private Socket socket;
    private ChatService chatService;

    public ChatController(Socket socket) {
        this.socket = socket;
        this.chatService = new ChatService();
    }

    public void handle() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());

        String request = in.readLine();
        if (request.startsWith("MESSAGE")) {
            String[] parts = request.split(" ");
            chatService.sendMessage(parts[1], parts[2]);
            out.write("MESSAGE_SENT\n");
        }
        out.flush();
    }
}
