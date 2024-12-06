package form;

import etc.RoundedButton;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;

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

    private AuthService authService;
    private Socket socket;

    // 생성자
    public SignUpForm(Socket socket) {
        this.socket = socket;  // 소켓 초기화
        authService = new AuthService(); // AuthService 객체 초기화

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
            String name = nameField.getText();
            String id = idField.getText();
            String password = new String(pwField.getPassword());
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String detailAddress = detailAddressField.getText();

            // 사용자 등록 처리
            if (name.isEmpty() || id.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || detailAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
            } else {
                try {
                    // 소켓을 사용하여 회원가입 처리
                    authService.register(id, password, name, email, phone, address, detailAddress); // 소켓 전달
                    JOptionPane.showMessageDialog(this, "환영합니다!");
                    dispose(); // 회원가입 후 창 닫기
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "회원가입 실패 다시시도해주세요: " + ex.getMessage());
                    ex.printStackTrace();
                }
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
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // 기타 기존 메소드들

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
}
