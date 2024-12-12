// src/form/SignUpForm.java

package form;

import etc.RoundedButton;
import service.AuthService;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SignUpForm extends JFrame {
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField detailAddressField;
    private RoundedButton signUpButton;
    private RoundedButton cancelButton;
    private RoundedButton faceRecognitionButton;

    private AuthService authService;
    private Socket socket;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // OpenCV 라이브러리 로드
    }

    // 생성자
    public SignUpForm(Socket socket) {
        this.socket = socket;
        authService = new AuthService();

        setTitle("DrawChat - SignUp");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 폼 패널 설정
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 필드 추가
        formPanel.add(createLabel("이름:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(createLabel("아이디:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(createLabel("비밀번호:"));
        pwField = new JPasswordField();
        formPanel.add(pwField);

        formPanel.add(createLabel("이메일:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(createLabel("전화번호:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(createLabel("주소:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(createLabel("상세주소:"));
        detailAddressField = new JTextField();
        formPanel.add(detailAddressField);

        faceRecognitionButton = new RoundedButton("얼굴 인식 등록");
        formPanel.add(faceRecognitionButton);

        // 버튼 패널 설정
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        signUpButton = new RoundedButton("회원가입");
        cancelButton = new RoundedButton("취소");

        buttonPanel.add(signUpButton);
        buttonPanel.add(cancelButton);

        // 이벤트 설정
        signUpButton.addActionListener(e -> {
            String name = nameField.getText();
            String username = idField.getText();
            String password = new String(pwField.getPassword());
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String detailAddress = detailAddressField.getText();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || detailAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!authService.validateId(username)) {
                JOptionPane.showMessageDialog(this, "아이디는 1~20글자, 영문자와 숫자만 가능합니다.", "아이디 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!authService.validatePassword(password)) {
                JOptionPane.showMessageDialog(this, "비밀번호는 1~20글자, 문자+숫자+특수문자의 조합이어야 합니다.", "비밀번호 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.isIdDuplicate(username)) {
                JOptionPane.showMessageDialog(this, "아이디가 이미 존재합니다.", "아이디 중복", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.register(username, password, name, email, phone, address, detailAddress);
            if (success) {
                JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "회원가입 실패. 다시 시도해주세요.", "실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> { // 로그인 화면으로 돌아가기
            dispose();
            new LoginForm(socket);
        });

        faceRecognitionButton.addActionListener(e -> startFaceRecognition());

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setForeground(Color.BLACK); // 글자 색상을 검정색으로 변경
        return label;
    }

    // OpenCV Mat 객체를 BufferedImage로 변환
    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);

        // BGR -> RGB로 변환
        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, data);
        return image;
    }

    // 얼굴 인식 및 카메라 창 띄우기
    private void startFaceRecognition() {
        try {
            CascadeClassifier faceDetector = new CascadeClassifier("src/FaceRegistration/cascade/haarcascade_frontalface_default.xml");
            if (faceDetector.empty()) {
                JOptionPane.showMessageDialog(this, "얼굴 인식 모델이 로드되지 않았습니다.");
                return;
            }

            // 카메라 시작
            VideoCapture capture = new VideoCapture(0);
            if (!capture.isOpened()) {
                JOptionPane.showMessageDialog(this, "카메라를 열 수 없습니다.");
                return;
            }

            Mat frame = new Mat();
            JFrame cameraFrame = new JFrame("얼굴 인식");
            cameraFrame.setSize(640, 480);
            cameraFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JLabel cameraLabel = new JLabel();
            cameraFrame.add(cameraLabel);
            cameraFrame.setVisible(true);

            // 타이머를 사용하여 카메라 프레임 갱신
            Timer timer = new Timer(50, e -> {
                if (capture.read(frame)) {
                    MatOfRect faces = new MatOfRect();
                    faceDetector.detectMultiScale(frame, faces);
                    Rect[] facesArray = faces.toArray();

                    for (Rect rect : facesArray) {
                        Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 0, 255), 3); // 빨간색 사각형 그리기
                    }

                    // OpenCV의 Mat 객체를 BufferedImage로 변환하여 JLabel에 표시
                    BufferedImage img = matToBufferedImage(frame);
                    cameraLabel.setIcon(new ImageIcon(img));
                }
            });
            timer.start();

            // 얼굴 인식 완료 후 처리
            JOptionPane.showMessageDialog(this, "얼굴 인식이 완료되었습니다.");

            // 얼굴 이미지를 데이터베이스에 저장
            saveFaceToDatabase(frame);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "얼굴 인식 처리 중 오류가 발생했습니다: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveFaceToDatabase(Mat faceImage) {
        try {
            // Mat을 byte[]로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage img = matToBufferedImage(faceImage);
            ImageIO.write(img, "jpg", baos);
            byte[] imageData = baos.toByteArray();
    
            // MySQL 연결
            String url = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/drawchat";
            String username = "admin";
            String password = "asdf4567";
            Connection conn = DriverManager.getConnection(url, username, password);
    
            // SQL 쿼리 준비
            String sql = "INSERT INTO user (name, username, password, email, phone, face, address, detail_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setString(2, idField.getText());
            stmt.setString(3, new String(pwField.getPassword()));
            stmt.setString(4, emailField.getText());
            stmt.setString(5, phoneField.getText());
            stmt.setBytes(6, imageData);
            stmt.setString(7, addressField.getText());
            stmt.setString(8, detailAddressField.getText());
    
            // 쿼리 실행
            stmt.executeUpdate();
    
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}    