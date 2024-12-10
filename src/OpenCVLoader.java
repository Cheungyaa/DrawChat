// src/OpenCVLoader.java

public class OpenCVLoader {
    static {
        // DLL 파일의 경로를 설정합니다.
        System.load("C:\\Users\\user\\Desktop\\DrawChat\\lib/opencv_java4100.dll");
    }

    public static void main(String[] args) {
        // OpenCV 기능을 사용할 준비가 되었음
        System.out.println("OpenCV 라이브러리가 성공적으로 로드되었습니다!");
    }
}