package service;

import entity.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private List<Message> messages;

    public ChatService() {
        this.messages = new ArrayList<>();
    }

    public void sendMessage(String sender, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());

        messages.add(message);
        System.out.println("Message sent: " + content);
    }

    public List<Message> getMessages() {
        return messages;
    }
}
