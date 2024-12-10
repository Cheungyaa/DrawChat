// src/form/DrawingForm.java

package form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DrawingForm extends JFrame {
    private DrawingPanel drawingPanel; // 드로잉 패널

    public DrawingForm() {
        setTitle("Drawing Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 드로잉 패널 생성
        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // 저장 버튼
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        // BufferedImage로 변환 후 저장
                        BufferedImage image = new BufferedImage(drawingPanel.getWidth(), drawingPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = image.createGraphics();
                        drawingPanel.paint(g2d); // 패널의 내용을 이미지로 렌더링
                        g2d.dispose();
                        ImageIO.write(image, "PNG", file); // PNG 형식으로 저장
                        System.out.println("File chosen: " + file.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        add(saveButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        new DrawingForm();
    }
}

class DrawingPanel extends JPanel {
    private Image image;
    private Graphics2D g2d;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        // 빈 이미지를 만들어서 그 위에 그림을 그릴 수 있도록 설정
        image = createImage(getWidth(), getHeight());
        g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);

        // 마우스 리스너로 드로잉 처리
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                g2d.fillOval(e.getX(), e.getY(), 10, 10); // 클릭한 위치에 점 찍기
                repaint();
            }
        });

        // 마우스 드래그 리스너로 그림 그리기
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                g2d.fillOval(e.getX(), e.getY(), 10, 10); // 드래그하는 동안 그리기
                repaint();
            }
        });
    }

    // 패널의 그림을 그려주는 메소드
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
