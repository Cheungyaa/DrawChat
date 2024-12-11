package form;

import javax.swing.*; // GUI 구성 요소를 위한 클래스 임포트
import java.awt.*; // GUI 및 그래픽 처리를 위한 클래스 임포트
import java.awt.event.*; // 이벤트 처리를 위한 클래스 임포트
import java.awt.image.BufferedImage; // 이미지를 처리하기 위한 클래스 임포트
import javax.imageio.ImageIO; // 이미지 입출력을 위한 클래스 임포트
import java.io.File; // 파일 처리를 위한 클래스 임포트
import java.io.IOException; // 입출력 예외 처리를 위한 클래스 임포트
import java.util.Stack; // Undo/Redo 기능을 위한 스택 클래스 임포트

// 메인 프레임 클래스
public class DrawingForm extends JFrame {
    private DrawingPanel drawingPanel; // 그림을 그릴 패널 객체

    public DrawingForm() {
        setTitle("Drawing Form"); // 창 제목 설정
        setSize(800, 600); // 창 크기 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫기 설정

        // 드로잉 패널 생성 및 추가
        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        // 툴바 생성
        JToolBar toolBar = new JToolBar();

        // 색상 선택 버튼
        JButton colorButton = new JButton("색상 선택");
        colorButton.addActionListener(e -> drawingPanel.selectColor()); // 색상 선택 이벤트 처리
        toolBar.add(colorButton);

        // 되돌리기 버튼
        JButton undoButton = new JButton(new ImageIcon("img/back_image.png")); // 되돌리기 버튼 아이콘
        undoButton.setToolTipText("되돌리기"); // 툴팁 설정
        undoButton.addActionListener(e -> drawingPanel.undo()); // 되돌리기 이벤트 처리
        toolBar.add(undoButton);

        // 앞으로 돌리기 버튼
        JButton redoButton = new JButton(new ImageIcon("img/front_image.png")); // 앞으로 돌리기 버튼 아이콘
        redoButton.setToolTipText("앞으로 돌리기"); // 툴팁 설정
        redoButton.addActionListener(e -> drawingPanel.redo()); // 앞으로 돌리기 이벤트 처리
        toolBar.add(redoButton);

        // 툴바 추가
        add(toolBar, BorderLayout.NORTH);

        // 저장 버튼
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveDrawing()); // 저장 이벤트 처리
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true); // 창 표시
    }

    // 그림 저장 메서드
    private void saveDrawing() {
        JFileChooser fileChooser = new JFileChooser(); // 파일 선택 창 생성
        fileChooser.setDialogTitle("새로운 파일 저장"); // 대화 상자 제목 설정
        fileChooser.setAcceptAllFileFilterUsed(false); // 모든 파일 필터 사용 안 함
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG 파일", "png")); // PNG 파일만 선택할 수 있도록 필터 설정

        int result = fileChooser.showSaveDialog(this); // 저장 대화상자 표시
        if (result == JFileChooser.APPROVE_OPTION) { // 파일 선택 확인
            File file = fileChooser.getSelectedFile(); // 선택한 파일 가져오기
            // 파일 확장자가 없으면 자동으로 .png 확장자 추가
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                BufferedImage image = drawingPanel.createImage(); // 드로잉 이미지 가져오기
                ImageIO.write(image, "PNG", file); // PNG 형식으로 선택한 파일에 저장
                System.out.println("File saved: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new DrawingForm(); // 프로그램 실행
    }
}

// 그림을 그리기 위한 패널 클래스
class DrawingPanel extends JPanel {
    private BufferedImage image; // 그리기 내용을 저장할 이미지
    private Graphics2D g2d; // 그래픽 객체
    private Stack<BufferedImage> undoStack = new Stack<>(); // Undo를 위한 스택
    private Stack<BufferedImage> redoStack = new Stack<>(); // Redo를 위한 스택
    private Color currentColor = Color.white; // 현재 색상

    public DrawingPanel() {
        setBackground(Color.WHITE); // 배경색을 하얀색으로 설정
        setPreferredSize(new Dimension(800, 600)); // 패널 크기 설정

        initializeImage(); // 이미지 초기화

        // 마우스 이벤트 리스너 추가
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                saveStateToUndoStack(); // 현재 상태를 Undo 스택에 저장
                drawPoint(e.getX(), e.getY()); // 클릭한 위치에 점 그리기
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawPoint(e.getX(), e.getY()); // 드래그한 위치에 점 그리기
            }
        });
    }

    // 이미지 초기화 메서드
    private void initializeImage() {
        image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB); // 이미지 생성
        g2d = image.createGraphics(); // 그래픽 객체 초기화
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 안티앨리어싱 적용
        g2d.setColor(currentColor); // 초기 색상 설정
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight()); // 배경 초기화
    }

    // 현재 상태를 Undo 스택에 저장
    private void saveStateToUndoStack() {
        BufferedImage copy = copyImage(image); // 이미지 복사
        undoStack.push(copy); // Undo 스택에 추가
        redoStack.clear(); // Redo 스택 초기화
    }

    // 점을 그리는 메서드
    private void drawPoint(int x, int y) {
        g2d.fillOval(x - 5, y - 5, 10, 10); // 점 그리기 (크기를 10x10으로 설정)
        repaint(); // 화면 갱신
    }

    // 컴포넌트 페인트 메서드 오버라이드
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 기본 그리기 호출
        g.drawImage(image, 0, 0, this); // 이미지 그리기
    }

    // 현재 이미지를 반환
    public BufferedImage createImage() {
        return image;
    }

    // 색상 선택 메서드
    public void selectColor() {
        Color newColor = JColorChooser.showDialog(this, "색상 선택", currentColor); // 색상 선택 대화 상자 표시
        if (newColor != null) { // 선택한 색상이 유효하면
            currentColor = newColor; // 현재 색상 업데이트
            g2d.setColor(currentColor); // 그래픽 객체의 색상 업데이트
        }
    }

    // Undo 메서드
    public void undo() {
        if (!undoStack.isEmpty()) { // Undo 스택이 비어있지 않으면
            redoStack.push(copyImage(image)); // 현재 상태를 Redo 스택에 저장
            image = undoStack.pop(); // Undo 스택에서 이전 상태 가져오기
            g2d = image.createGraphics(); // 그래픽 객체 업데이트
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(currentColor); // 현재 색상 설정
            repaint(); // 화면 갱신
        }
    }

    // Redo 메서드
    public void redo() {
        if (!redoStack.isEmpty()) { // Redo 스택이 비어있지 않으면
            undoStack.push(copyImage(image)); // 현재 상태를 Undo 스택에 저장
            image = redoStack.pop(); // Redo 스택에서 다음 상태 가져오기
            g2d = image.createGraphics(); // 그래픽 객체 업데이트
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(currentColor); // 현재 색상 설정
            repaint(); // 화면 갱신
        }
    }

    // 이미지 복사 메서드
    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType()); // 새로운 이미지 생성
        Graphics2D g = copy.createGraphics(); // 그래픽 객체 생성
        g.drawImage(source, 0, 0, null); // 원본 이미지 복사
        g.dispose();
        return copy;
    }
}
