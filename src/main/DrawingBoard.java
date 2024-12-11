package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class DrawingBoard extends JFrame {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Image canvasImage;
    private Graphics2D g2d;
    private Point lastPoint = null;

    public DrawingBoard(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("I/O 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        setTitle("그림판");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // BufferedImage를 이용한 캔버스 생성
        canvasImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) canvasImage.getGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 500, 500);

        // 마우스 리스너 설정: 그리기 시작
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });

        // 마우스 드래그 리스너 설정: 그리기
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    g2d.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                    lastPoint = e.getPoint();
                    repaint();

                    // 서버에 그리기 데이터 전송
                    sendDrawingDataToServer();
                }
            }
        });

        // 서버로부터 그림 데이터를 받는 스레드
        new Thread(() -> {
            try {
                while (true) {
                    Object data = in.readObject();
                    if (data instanceof Image) {
                        canvasImage = (Image) data;
                        repaint();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("서버로부터 데이터를 받는 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeResources();
            }
        }).start();
    }

    // 서버에 그리기 데이터 전송
    private void sendDrawingDataToServer() {
        try {
            out.writeObject(canvasImage);
            out.flush();
        } catch (IOException e) {
            System.err.println("서버로 그리기 데이터 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 자원 정리
    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("자원 해제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(canvasImage, 0, 0, null);
    }
}
