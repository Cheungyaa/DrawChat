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

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

        RoundedButton chatRoomButton = new RoundedButton("Enter Chat Room");
        chatRoomButton.addActionListener(e -> {
            dispose();
            new ChatRoomForm(username, socket);
        });

        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm(socket);
        });

        panel.add(welcomeLabel);
        panel.add(chatRoomButton);
        panel.add(logoutButton);

        add(panel);
        setVisible(true);
    }
}
