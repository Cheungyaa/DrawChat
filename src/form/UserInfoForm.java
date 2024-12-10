// src/form/UserInfoForm.java

package form;

import service.AuthService;
import dto.User;

import javax.swing.*;
import java.awt.*;

public class UserInfoForm extends JPanel {
    public UserInfoForm(String username, JPanel parentPanel, JFrame parentFrame) {
        setLayout(new BorderLayout());

        // 상단에 사용자 정보 표시
        JLabel userInfoLabel = new JLabel("현재 접속된 계정의 이름: " + username, SwingConstants.CENTER);
        userInfoLabel.setFont(new Font("고딕", Font.BOLD, 18)); // 텍스트 크기 조금 증가
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(userInfoLabel, BorderLayout.NORTH);

        // 버튼 배열 설정
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50)); // 패널 여백 조정
        buttonPanel.setBackground(Color.DARK_GRAY);

        // 버튼 생성 및 크기 조정
        JButton changePasswordButton = createStyledButton("비밀번호 변경", 18, new Dimension(300, 60));
        JButton updateInfoButton = createStyledButton("사용자 정보 변경", 18, new Dimension(300, 60));
        JButton deleteAccountButton = createStyledButton("회원 탈퇴", 18, new Dimension(300, 60));
        JButton backButton = createStyledButton("뒤로가기", 14, new Dimension(200, 40));

        // 버튼 간격 조정
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(updateInfoButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(deleteAccountButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            if (parentPanel != null) {
                parentPanel.removeAll();
                if (parentFrame instanceof WaitingRoomForm) {
                    ((WaitingRoomForm) parentFrame).resetMainPanel(); // WaitingRoomForm의 초기화 메서드 호출
                }
                parentPanel.revalidate();
                parentPanel.repaint();
            }
        });

        changePasswordButton.addActionListener(e -> {
            // AuthService를 통해 사용자 정보 가져오기
            AuthService authService = new AuthService();
            if (authService.verifyPassword(username, JOptionPane.showInputDialog("현재 비밀번호를 입력하세요:"))) {
                parentPanel.removeAll();
                parentPanel.add(new ChangePasswordForm(username, parentPanel, parentFrame));
                parentPanel.revalidate();
                parentPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(parentFrame, "비밀번호가 일치하지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateInfoButton.addActionListener(e -> {
            // AuthService에서 사용자 정보 가져오기
            AuthService authService = new AuthService();
            User user = authService.getUserInfo(username);

            if (user != null) {
                parentPanel.removeAll();
                parentPanel.add(new UpdateInfoForm(
                    parentPanel,
                    parentFrame,
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getDetailAddress()
                ));
                parentFrame.revalidate();
                parentFrame.repaint();
            } else {
                JOptionPane.showMessageDialog(parentFrame, "사용자 정보를 가져오지 못했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteAccountButton.addActionListener(e -> {
            // AuthService를 통해 사용자 비밀번호 검증
            AuthService authService = new AuthService();
            if (authService.verifyPassword(username, JOptionPane.showInputDialog("현재 비밀번호를 입력하세요:"))) {
                parentPanel.removeAll();
                parentPanel.add(new DeleteAccountForm(username, parentPanel, parentFrame));
                parentPanel.revalidate();
                parentPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(parentFrame, "비밀번호가 일치하지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JButton createStyledButton(String text, int fontSize, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(new Font("고딕", Font.PLAIN, fontSize));
        button.setBackground(new Color(255, 140, 0)); // 조금 더 부드러운 주황색
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 버튼 내부 여백
        button.setMaximumSize(size); // 버튼 크기 제한
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼 중앙 정렬
        return button;
    }
}
