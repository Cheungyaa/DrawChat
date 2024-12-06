package etc;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false); // 테두리 비활성화
        setFocusPainted(false); // 포커스 테두리 비활성화
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 색상 설정
        Color normalColor = new Color(255, 247, 242);
        Color hoverColor = new Color(250, 220, 200);
        Color pressedColor = new Color(240, 180, 150);

        // 버튼 상태에 따른 색상 변경
        if (getModel().isPressed()) {
            g2.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        // 버튼의 배경을 둥근 사각형으로 채움
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // 텍스트 색상 설정
        g2.setColor(new Color(247, 99, 12));
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
        g2.drawString(getText(), x, y);

        g2.dispose();
        super.paintComponent(g);
    }
}
