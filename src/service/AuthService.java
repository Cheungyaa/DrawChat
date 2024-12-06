package service;

import java.net.Socket;
import java.sql.*;

public class AuthService {

    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/drawchat"; // DB 이름을 drawchat으로 수정
    private static final String USER = "admin";
    private static final String PASSWORD = "asdf4567";

    // 데이터베이스 연결을 위한 메서드
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
            throw e;
        }
    }

    // 사용자 등록 메서드
    public void register(String username, String password, String name, String email, String phone, String address, String detailAddress) {
        String sql = "INSERT INTO user (name, id, password, email, phone, face, address, add_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // 테이블 이름 수정
        
        try (Connection connection = getConnection(); 
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setNull(6, java.sql.Types.BLOB);  // face는 기본적으로 NULL로 설정 (나중에 추가될 경우 처리)
            stmt.setString(7, address);
            stmt.setString(8, detailAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 로그인 인증 메서드
    public boolean login(String username, String password, Socket socket) {
        String sql = "SELECT * FROM user WHERE id = ? AND password = ?";  // 테이블 이름 수정

        try (Connection connection = getConnection();
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
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("데이터베이스 연결 성공");
                return true; // 연결 성공
            }
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
        }
        return false; // 연결 실패
    }
}
