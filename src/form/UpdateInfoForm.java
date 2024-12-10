// src/form/UpdateInfoForm.java

package form;

import javax.swing.*;

import service.AuthService;

import java.awt.*;

public class UpdateInfoForm extends JPanel {
    public UpdateInfoForm(JPanel parentPanel, JFrame parentFrame, String username, String email, String phone, String address, String detailAddress) {
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("사용자 정보 변경", SwingConstants.CENTER);
        headerLabel.setFont(new Font("고딕", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // 필드 추가
        formPanel.add(new JLabel("이름:"));
        JTextField nameField = new JTextField(username); // 이름은 수정하지 않음
        nameField.setEditable(false); // 이름 필드를 비활성화
        formPanel.add(nameField);

        formPanel.add(new JLabel("이메일:"));
        JTextField emailField = new JTextField(email);
        formPanel.add(emailField);

        formPanel.add(new JLabel("전화번호:"));
        JTextField phoneField = new JTextField(phone);
        formPanel.add(phoneField);

        formPanel.add(new JLabel("주소:"));
        JTextField addressField = new JTextField(address);
        formPanel.add(addressField);

        formPanel.add(new JLabel("상세주소:"));
        JTextField detailAddressField = new JTextField(detailAddress);
        formPanel.add(detailAddressField);

        add(formPanel, BorderLayout.CENTER);

        // 버튼
        JButton saveButton = new JButton("저장");
        JButton backButton = new JButton("뒤로가기");

        saveButton.addActionListener(e -> {
            String updatedEmail = emailField.getText();
            String updatedPhone = phoneField.getText();
            String updatedAddress = addressField.getText();
            String updatedDetailAddress = detailAddressField.getText();

            AuthService authService = new AuthService();
            boolean success = authService.updateUserInfo(username, updatedEmail, updatedPhone, updatedAddress, updatedDetailAddress);

            if (success) {
                JOptionPane.showMessageDialog(parentFrame, "사용자 정보가 성공적으로 업데이트되었습니다.");
                backButton.doClick(); // 뒤로가기 버튼 동작 호출
            } else {
                JOptionPane.showMessageDialog(parentFrame, "사용자 정보 업데이트에 실패했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            parentPanel.removeAll();
            parentPanel.add(new UserInfoForm(username, parentPanel, parentFrame));
            parentPanel.revalidate();
            parentPanel.repaint();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
