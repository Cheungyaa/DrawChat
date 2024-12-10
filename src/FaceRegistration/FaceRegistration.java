// src/FaceRegistration/FaceRegistration.java

package FaceRegistration;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import service.AuthService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FaceRegistration {

    // 얼굴 이미지와 사용자 정보를 데이터베이스에 저장하는 메소드
    public static void registerFace(Mat faceImage, String username, String password, String name, String email,
                                    String phone, String address, String detailAddress, String dbUrl, String dbUser, String dbPassword) {

        // 얼굴 이미지를 JPG 형식으로 인코딩하여 바이너리 데이터로 변환
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", faceImage, matOfByte);  // 얼굴 이미지를 JPG 형식으로 인코딩
        byte[] byteArray = matOfByte.toArray();  // byte[]로 변환

        // AuthService 인스턴스를 통해 데이터베이스 연결
        AuthService authService = new AuthService();

        // 데이터베이스 연결을 위한 처리
        try (Connection connection = authService.getConnection(dbUrl, dbUser, dbPassword)) {  
            // 사용자 정보와 얼굴 이미지를 데이터베이스에 저장하는 SQL 쿼리
            String sql = "INSERT INTO user (name, id, password, email, phone, face, address, add_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // PreparedStatement 생성
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);            // 사용자 이름
                stmt.setString(2, username);        // 사용자 ID
                stmt.setString(3, password);        // 사용자 비밀번호
                stmt.setString(4, email);           // 사용자 이메일
                stmt.setString(5, phone);           // 사용자 전화번호
                stmt.setBytes(6, byteArray);        // 얼굴 이미지를 BLOB 컬럼으로 저장
                stmt.setString(7, address);         // 주소
                stmt.setString(8, detailAddress);   // 상세 주소
                
                // 데이터베이스에 삽입
                int rowsAffected = stmt.executeUpdate();  
                if (rowsAffected > 0) {
                    System.out.println("얼굴 등록 및 사용자 정보 저장 완료!");
                } else {
                    System.out.println("사용자 등록 실패.");
                }
            }
        } catch (SQLException e) {
            System.err.println("데이터베이스 오류: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("얼굴 등록 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
