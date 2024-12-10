// src/main/Main.java

package main;

import org.opencv.core.Core; // OpenCV의 핵심 기능을 사용하기 위한 클래스
import org.opencv.core.Mat; // 이미지 데이터를 저장하기 위한 행렬 클래스
import org.opencv.core.MatOfRect; // 여러 개의 사각형을 저장하기 위한 클래스
import org.opencv.core.Rect; // 사각형을 정의하는 클래스
import org.opencv.core.Scalar; // 색상 및 픽셀 값 표현을 위한 클래스
import org.opencv.core.Point; // 점을 정의하는 클래스
import org.opencv.imgproc.Imgproc; // 이미지 처리 기능을 제공하는 클래스
import org.opencv.objdetect.CascadeClassifier; // 객체 탐지를 위한 분류기 클래스
import org.opencv.videoio.VideoCapture; // 비디오 캡처 기능을 제공하는 클래스

import FaceRegistration.FaceRegistration;

import org.opencv.highgui.HighGui; // GUI 관련 기능을 제공하는 클래스, imshow 및 waitKey 사용
import org.opencv.imgcodecs.Imgcodecs; // 이미지 파일 입출력을 위한 클래스

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // OpenCV 네이티브 라이브러리를 로드
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Haar Cascade 파일 경로 설정
        String cascadePath = "src/FaceRegistration/cascade/haarcascade_frontalface_default.xml"; // Haar-cascade 파일 경로

        // Haar Cascade 로드
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            // Haar Cascade 파일을 로드하지 못한 경우 오류 메시지 출력
            System.err.println("Error: Could not load Haar Cascade file.");
            return; // 프로그램 종료
        }

        // 비디오 캡처 (기본 카메라)
        VideoCapture camera = new VideoCapture(0); // 첫 번째 연결된 카메라 사용 (인덱스 0)
        if (!camera.isOpened()) {
            // 카메라를 열 수 없는 경우 오류 메시지 출력
            System.err.println("Error: Could not open camera.");
            return; // 프로그램 종료
        }

        // GUI 생성 (얼굴 저장 버튼 추가)
        JFrame frameWindow = new JFrame("실시간 얼굴 인식");
        JButton saveButton = new JButton("얼굴 저장");
        frameWindow.add(saveButton);
        frameWindow.setSize(200, 100);
        frameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameWindow.setVisible(true);

        // 각 프레임을 저장할 행렬 생성
        Mat frame = new Mat();

        try {
            // 버튼 클릭 이벤트 핸들러
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("버튼을 눌렀습니다!");
                    // 모든 매개변수를 전달
        FaceRegistration.registerFace(frame, "admin", "asdf4567", "홍", "길동", "hong.gildong@example.com", 
        "010-1234-5678", "서울시 강남구", "서울", "서울특별시", "한국");
}
});

            // 카메라에서 프레임을 읽는 동안 반복
            while (camera.read(frame)) {
                // 프레임에서 얼굴 탐지
                MatOfRect faces = new MatOfRect(); // 얼굴 영역을 저장할 객체 생성
                faceDetector.detectMultiScale(frame, faces); // 얼굴 탐지 수행

                // 탐지된 얼굴 주위에 사각형 그리기
                for (Rect rect : faces.toArray()) { // 탐지된 얼굴 배열 반복
                    Imgproc.rectangle(frame,
                            new Point(rect.x, rect.y), // 사각형의 왼쪽 상단 좌표
                            new Point(rect.x + rect.width, rect.y + rect.height), // 사각형의 오른쪽 하단 좌표
                            new Scalar(0, 255, 0), // 사각형 색상 (초록색)
                            2); // 선 두께
                }

                // HighGui를 사용하여 프레임을 창에 표시
                HighGui.imshow("Real-Time Face Detection", frame);  // 창에 프레임 표시

                // 'ESC' 키가 눌리면 종료
                if (HighGui.waitKey(1) == 27) { // 27은 ESC 키
                    break; // 반복문 종료
                }
            }
        } finally {
            // 자원 해제
            camera.release(); // 카메라 자원 해제
            HighGui.destroyAllWindows(); // 열려 있는 모든 창 닫기
        }
    }
}
