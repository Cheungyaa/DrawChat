package form;

import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SignUpForm extends JFrame {
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField detailAddressField;
    private RoundedButton signUpButton;
    private RoundedButton cancelButton;

    public SignUpForm(Socket socket) {
        setTitle("DrawChat - 회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 메인 패널 설정
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgImage = new ImageIcon("img/Login2.jpg");
                g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // 폼 패널 설정
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setOpaque(false); // 투명 설정
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 필드 추가
        formPanel.add(createLabel("이름:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(createLabel("아이디:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(createLabel("비밀번호:"));
        pwField = new JPasswordField();
        formPanel.add(pwField);

        formPanel.add(createLabel("이메일:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(createLabel("전화번호:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(createLabel("주소:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(createLabel("상세주소:"));
        detailAddressField = new JTextField();
        formPanel.add(detailAddressField);

        // 버튼 패널 설정
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        signUpButton = new RoundedButton("회원가입");
        cancelButton = new RoundedButton("취소");

        buttonPanel.add(signUpButton);
        buttonPanel.add(cancelButton);

        // 이벤트 설정
        signUpButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String id = idField.getText();
                String password = new String(pwField.getPassword());
                String email = emailField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();
                String detailAddress = detailAddressField.getText();

                // 서버로 데이터 전송
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("REGISTER " + name + " " + id + " " + password + " " + email + " " + phone + " " + address + " " + detailAddress);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();

                if ("SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(this, "회원가입 성공!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "회원가입 실패. 다시 시도하세요.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버와의 연결이 끊어졌습니다.");
            }
        });

        cancelButton.addActionListener(e -> dispose());

        // 메인 패널에 추가
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // 창 닫기 이벤트
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
}
