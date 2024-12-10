// src/form//LoginForm.java

package form;

import service.AuthService;
import etc.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import org.opencv.core.Mat;  // OpenCV Mat 클래스 추가

public class LoginForm extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private RoundedButton loginButton;
    private RoundedButton signUpButton;
    private RoundedButton faceLoginButton;  // 얼굴 인식 로그인 버튼 추가
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
                        new WaitingRoomForm(username); // 대기실 창으로 이동
                    } else {
                        JOptionPane.showMessageDialog(null, "ID 또는 Password가 잘못되었습니다.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 회원가입 버튼
        signUpButton = new RoundedButton("Sign Up");
        signUpButton.setBounds(210, 450, 90, 30); // 위치 조정
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 회원가입 창으로 이동, 소켓 전달
                new SignUpForm(socket); // 소켓 객체를 전달하여 SignUpForm 열기
            }
        });

        // 얼굴 인식 로그인 버튼 추가
        faceLoginButton = new RoundedButton("Face Login");
        faceLoginButton.setBounds(100, 500, 200, 30); // 위치 조정
        faceLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFaceLogin();  // 얼굴 인식 로그인 처리
            }
        });

        // 배경에 모든 컴포넌트를 추가
        backgroundLabel.add(idLabel);
        backgroundLabel.add(idField);
        backgroundLabel.add(pwLabel);
        backgroundLabel.add(pwField);
        backgroundLabel.add(loginButton);
        backgroundLabel.add(signUpButton);
        backgroundLabel.add(faceLoginButton);  // 얼굴 인식 로그인 버튼 추가

        // 프레임에 배경 추가
        setContentPane(backgroundLabel);
        setVisible(true);
    }

    // 얼굴 인식 로그인 처리
    private void handleFaceLogin() {
        String username = idField.getText();
        String password = new String(pwField.getPassword());

        // 아이디와 비밀번호가 입력되지 않은 경우
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID와 Password를 입력해주세요.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;  // 아이디와 비밀번호 입력하지 않으면 로그인 처리 안됨
        }

        // 얼굴 인식 시작
        Mat faceImage = startFaceRecognition();  // 얼굴 인식 함수 호출

        if (faceImage != null) {
            // 얼굴 인식된 이미지와 사용자가 입력한 정보로 로그인 처리
            if (verifyFaceWithDatabase(faceImage, username, password)) {
                JOptionPane.showMessageDialog(this, "얼굴 인식 로그인 성공!");
                dispose();  // 로그인 성공 후 창 닫기
                new WaitingRoomForm(username);  // 대기실 창으로 이동
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 일치하지 않습니다.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "얼굴을 인식할 수 없습니다. 다시 시도해주세요.", "Face Recognition Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 얼굴 인식을 위한 처리 (OpenCV 등을 이용)
    private Mat startFaceRecognition() {
        // OpenCV를 이용한 얼굴 인식 처리
        // 실제로 OpenCV를 이용해 카메라에서 얼굴을 인식하고 그 이미지를 반환하도록 구현해야 합니다.
        return null;  // 실제 얼굴 인식 처리 후 이미지 반환
    }

    // 데이터베이스에서 얼굴 인식과 사용자 정보를 비교하여 로그인 검증
    private boolean verifyFaceWithDatabase(Mat faceImage, String username, String password) {
        // 실제로 얼굴 이미지를 데이터베이스에 저장된 이미지와 비교하는 로직이 필요
        return true;  // 얼굴 인식 성공 시 true 반환 (실제 비교 로직 필요)
    }
}
