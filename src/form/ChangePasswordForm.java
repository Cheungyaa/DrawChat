// src/form/ChangePasswordForm.java

package form;

import service.AuthService;
import dto.User;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordForm extends JPanel {
    public ChangePasswordForm(String username, JPanel parentPanel, JFrame parentFrame) {
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("비밀번호 변경", SwingConstants.CENTER);
        headerLabel.setFont(new Font("고딕", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        // 중앙 정렬을 위한 패널 생성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80)); // 외부 여백
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 새 비밀번호 필드
        JLabel newPasswordLabel = new JLabel("새 비밀번호:");
        newPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setMaximumSize(new Dimension(250, 30)); // 크기 조정

        // 비밀번호 확인 필드
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(250, 30));

        // 버튼들
        JButton changeButton = createStyledButton("변경", 14, new Dimension(200, 40));
        JButton backButton = createStyledButton("뒤로가기", 14, new Dimension(200, 40));

        // 필드 및 버튼 추가
        centerPanel.add(newPasswordLabel);
        centerPanel.add(newPasswordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 간격
        centerPanel.add(confirmPasswordLabel);
        centerPanel.add(confirmPasswordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(changeButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);

        changeButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "모든 필드를 입력해주세요.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(parentFrame, "새 비밀번호가 일치하지 않습니다.");
            } else {
                AuthService authService = new AuthService();
                boolean updated = authService.updatePassword(username, newPassword); // 비밀번호 업데이트
                if (updated) {
                    JOptionPane.showMessageDialog(parentFrame, "비밀번호가 성공적으로 변경되었습니다.");
                    backButton.doClick();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "비밀번호 변경에 실패했습니다.");
                }
            }
        });
        

        backButton.addActionListener(e -> {
            parentPanel.removeAll();
            parentPanel.add(new UserInfoForm(username, parentPanel, parentFrame));
            parentPanel.revalidate();
            parentPanel.repaint();
        });
        
    }

    private JButton createStyledButton(String text, int fontSize, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(new Font("고딕", Font.PLAIN, fontSize));
        button.setBackground(new Color(255, 140, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}
