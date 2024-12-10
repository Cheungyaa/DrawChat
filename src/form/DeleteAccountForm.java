// src/form/DeleteAccountForm.java

package form;

import javax.swing.*;
import java.awt.*;
import service.AuthService;

public class DeleteAccountForm extends JPanel {
    public DeleteAccountForm(String username, JPanel parentPanel, JFrame parentFrame) {
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("회원 탈퇴", SwingConstants.CENTER);
        headerLabel.setFont(new Font("고딕", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        // 폼 패널
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 비밀번호 라벨 및 필드
        JLabel passwordLabel = new JLabel("현재 비밀번호:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼들
        JButton deleteButton = new JButton("회원 탈퇴");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton backButton = new JButton("뒤로가기");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 폼 컴포넌트 추가
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 간격 추가
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30))); // 간격 추가
        formPanel.add(deleteButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 버튼 사이 간격 추가
        formPanel.add(backButton);

        // 폼 패널을 중앙에 배치
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 버튼 이벤트 리스너
        deleteButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "비밀번호를 입력하세요.");
                return;
            }

            // AuthService를 통해 비밀번호 검증 및 회원 탈퇴
            AuthService authService = new AuthService();
            if (authService.verifyPassword(username, password)) {
                if (authService.deleteAccount(username)) {
                    JOptionPane.showMessageDialog(parentFrame, "회원 탈퇴가 완료되었습니다.");
                    System.exit(0); // 프로그램 종료
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "회원 탈퇴 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "비밀번호가 일치하지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            parentPanel.removeAll();
            parentPanel.add(new UserInfoForm(username, parentPanel, parentFrame));
            parentPanel.revalidate();
            parentPanel.repaint();
        });
    }
}