package service;

import java.net.Socket;
import java.sql.*;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class AuthService {  

    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/drawchat"; // DB 이름을 drawchat으로 수정
    private static final String USER = "admin";
    private static final String PASSWORD = "asdf4567";

    // 데이터베이스 연결을 위한 메서드 (외부에서 URL, 사용자명, 비밀번호를 인자로 받도록 수정)
    public Connection getConnection(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);  // 인자값을 사용하여 연결
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
            throw e;
        }
    }

    // 사용자 등록 메서드
    public void register(String username, String password, String name, String email, String phone, String address, String detailAddress) {
        String sql = "INSERT INTO user (name, id, password, email, phone, face, address, add_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // 테이블 이름 수정
        
        try (Connection connection = getConnection(URL, USER, PASSWORD);  // 수정된 메서드 호출
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);          // 이름
            stmt.setString(2, username);      // 사용자 ID
            stmt.setString(3, password);      // 비밀번호
            stmt.setString(4, email);         // 이메일
            stmt.setString(5, phone);         // 전화번호
            stmt.setNull(6, java.sql.Types.BLOB);  // face는 기본적으로 NULL로 설정 (추후 얼굴 등록 시 값 추가)
            stmt.setString(7, address);       // 주소
            stmt.setString(8, detailAddress); // 상세 주소
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 로그인 인증 메서드
    public boolean login(String username, String password, Socket socket) {
        String sql = "SELECT * FROM user WHERE id = ? AND password = ?";  // 테이블 이름 수정

        try (Connection connection = getConnection(URL, USER, PASSWORD);  // 수정된 메서드 호출
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("로그인 성공: " + username);
                    return true;  // 사용자 존재하면 true 반환
                } else {
                    System.out.println("로그인 실패: 사용자 정보 없음");
                    return false;  // 사용자 존재하지 않으면 false 반환
                }
            }
        } catch (SQLException e) {
            System.out.println("로그인 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 얼굴 이미지와 사용자 정보를 데이터베이스에 등록하는 메서드 (추가)
    public void registerFace(String username, Mat faceImage) {
        String sql = "UPDATE user SET face = ? WHERE id = ?"; // 얼굴 이미지 컬럼 업데이트
        
        try (Connection connection = getConnection(URL, USER, PASSWORD);  // 수정된 메서드 호출
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 얼굴 이미지를 byte[]로 변환
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", faceImage, matOfByte);  // 얼굴 이미지를 JPG 형식으로 인코딩
            byte[] byteArray = matOfByte.toArray();  // byte[]로 변환

            stmt.setBytes(1, byteArray);  // 얼굴 이미지를 BLOB 컬럼으로 저장
            stmt.setString(2, username); // 사용자 ID를 기준으로

            stmt.executeUpdate();
            System.out.println("얼굴 이미지 등록 완료!");
        } catch (SQLException e) {
            System.out.println("얼굴 이미지 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 리소스 정리 메서드
    public static void cleanup() {
        System.out.println("AuthService 리소스 정리 중...");
        // 필요시 데이터베이스 연결 종료, 기타 정리 작업 추가 가능
        try {
            // 여기에서 데이터베이스 연결 종료 등을 구현할 수 있습니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 데이터베이스 연결 상태 확인 메서드
    public boolean checkConnection() {
        try (Connection connection = getConnection(URL, USER, PASSWORD)) {  // 수정된 메서드 호출
            if (connection != null) {
                System.out.println("데이터베이스 연결 성공");
                return true; // 연결 성공
            }
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
        }
        return false; // 연결 실패
    }

    public boolean signUp(String username, String password2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'signUp'");
    }

    public boolean signUp(String username, String password2, Socket socket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'signUp'");
    }
}
