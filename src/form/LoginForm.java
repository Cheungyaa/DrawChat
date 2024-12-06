package form;

import service.AuthService;
import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class LoginForm extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private RoundedButton loginButton;
    private RoundedButton signUpButton;
    private Socket socket;
    private AuthService authService;

    public LoginForm(Socket socket) {
        this.socket = socket;
        this.authService = new AuthService();  // AuthService 객체 초기화
        
        setTitle("DrawChat - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        // 배경 이미지 설정
        ImageIcon backgroundIcon = new ImageIcon("img/Login1.jpg");
        Image scaledImage = backgroundIcon.getImage().getScaledInstance(400, 600, Image.SCALE_SMOOTH); // 크기 조정
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel backgroundLabel = new JLabel(resizedIcon);
        backgroundLabel.setLayout(null); // 자유 배치
        backgroundLabel.setBounds(0, 0, 400, 600);

        // ID와 Password 입력 필드
        JLabel idLabel = new JLabel("ID:");
        idLabel.setForeground(Color.BLACK); // 텍스트 색상 변경
        idLabel.setBounds(120, 360, 80, 30); // 아래로 이동

        idField = new JTextField();
        idField.setBounds(180, 360, 120, 30);

        JLabel pwLabel = new JLabel("Password:");
        pwLabel.setForeground(Color.BLACK); // 텍스트 색상 변경
        pwLabel.setBounds(100, 400, 80, 30); // 아래로 이동

        pwField = new JPasswordField();
        pwField.setBounds(180, 400, 120, 30);

        // 로그인 및 회원가입 버튼
        loginButton = new RoundedButton("Login");
        loginButton.setBounds(100, 450, 90, 30); // 위치 조정
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = idField.getText();
                String password = new String(pwField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ID와 Password를 입력해주세요.", "Login Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // AuthService를 통해 로그인 시도
                    boolean loginSuccess = authService.login(username, password, socket);
                    if (loginSuccess) {
                        JOptionPane.showMessageDialog(null, "로그인 성공!", "Login", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // 현재 창 닫기
                        new WaitingForm(username, socket); // 대기실 창으로 이동
                    } else {
                        JOptionPane.showMessageDialog(null, "ID 또는 Password가 잘못되었습니다.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        signUpButton = new RoundedButton("Sign Up");
        signUpButton.setBounds(210, 450, 90, 30); // 위치 조정
        signUpButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        // 여기에서 기존에 있는 소켓 객체를 전달하여 SignUpForm 열기
        new SignUpForm(socket); // 회원가입 창으로 이동
    }
});


        // 배경에 모든 컴포넌트를 추가
        backgroundLabel.add(idLabel);
        backgroundLabel.add(idField);
        backgroundLabel.add(pwLabel);
        backgroundLabel.add(pwField);
        backgroundLabel.add(loginButton);
        backgroundLabel.add(signUpButton);

        // 프레임에 배경 추가
        setContentPane(backgroundLabel);
        setVisible(true);
    }
}
