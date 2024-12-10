// src/service/AuthService.java

package service;

import java.net.Socket;
import java.sql.*;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import dto.User;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class AuthService {  

    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://rds-mysql-metamong.cnku2aekidka.ap-northeast-2.rds.amazonaws.com:3306/drawchat"; // DB 이름을 drawchat으로 수정
    private static final String USER = "admin";
    private static final String PASSWORD = "asdf4567";

    // 아이디 및 비밀번호 검증 기준
    private static final int ID_MIN_LENGTH = 1;
    private static final int ID_MAX_LENGTH = 20;
    private static final String ID_REGEX = "^[a-zA-Z0-9]+$"; // 영문자, 숫자만 허용

    private static final int PASSWORD_MIN_LENGTH = 1;
    private static final int PASSWORD_MAX_LENGTH = 20;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$"; // 문자, 숫자, 특수문자 조합

    // 데이터베이스 연결을 위한 메서드 (외부에서 URL, 사용자명, 비밀번호를 인자로 받도록 수정)
    public Connection getConnection(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);  // 인자값을 사용하여 연결
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
            throw e;
        }
    }

    // 아이디 검증 메서드
    public boolean validateId(String username) {
        if (username == null || username.length() < ID_MIN_LENGTH || username.length() > ID_MAX_LENGTH) {
            return false; // 길이 검증 실패
        }
        return Pattern.matches(ID_REGEX, username); // 정규식 검증
    }

    // 비밀번호 검증 메서드
    public boolean validatePassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            return false; // 길이 검증 실패
        }
        return Pattern.matches(PASSWORD_REGEX, password); // 정규식 검증
    }

    // 아이디 중복 확인 메서드
    public boolean isIdDuplicate(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // 결과가 0보다 크면 중복
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 사용자 등록 메서드
    public boolean register(String username, String password, String name, String email, String phone, String address, String detailAddress) {
        
        if (!validateId(username)) {
            JOptionPane.showMessageDialog(null,"아이디는 1~20글자, 영문자와 숫자만 가능합니다.","아이디 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (isIdDuplicate(username)) {
            JOptionPane.showMessageDialog(null,"아이디가 중복되었습니다.", "아이디 중복 오류",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!validatePassword(password)) {
            JOptionPane.showMessageDialog(null,"비밀번호는 1~20글자, 문자+숫자+특수문자의 조합이어야 합니다.","비밀번호 오류",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        
        String sql = "INSERT INTO user (username, name, password, email, phone, face, address, detail_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // id를 제외한 수정된 쿼리

        try (Connection connection = getConnection(URL, USER, PASSWORD);  // 수정된 메서드 호출
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);    // 사용자 ID
            stmt.setString(2, name);        // 이름
            stmt.setString(3, password);    // 비밀번호
            stmt.setString(4, email);       // 이메일
            stmt.setString(5, phone);       // 전화번호                
            stmt.setNull(6, java.sql.Types.BLOB);  // face는 기본적으로 NULL로 설정 (추후 얼굴 등록 시 값 추가)
            stmt.setString(7, address);     // 주소
            stmt.setString(8, detailAddress); // 상세 주소
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "사용자 등록 중 오류가 발생했습니다: " + e.getMessage(), "등록 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    // 사용자 정보 추출 메서드
    public User getUserInfo(String username) {
        String sql = "SELECT name, email, phone, address, detail_address FROM user WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // 사용자 정보 객체 생성 및 반환
                    return new User(
                        username,
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("detail_address")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("사용자 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // 사용자를 찾을 수 없으면 null 반환
    }

    // 사용자 정보 업데이트 메서드
    public boolean updateUserInfo(String username, String email, String phone, String address, String detailAddress) {
        String sql = "UPDATE user SET email = ?, phone = ?, address = ?, detail_address = ? WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setString(4, detailAddress);
            stmt.setString(5, username);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // 업데이트 성공 여부 반환
        } catch (SQLException e) {
            System.out.println("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // 업데이트 실패 시 false 반환
    }

    // 비밀번호 검증 메서드
    public boolean verifyPassword(String username, String password) {
        String sql = "SELECT password FROM user WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return storedPassword.equals(password); // 비밀번호 일치 여부 확인
                }
            }
        } catch (SQLException e) {
            System.out.println("비밀번호 검증 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // 비밀번호 불일치 또는 오류 발생 시 false 반환
    }

    // 비밀번호 업데이트 메서드
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // 업데이트 성공 여부 반환
        } catch (SQLException e) {
            System.out.println("비밀번호 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // 업데이트 실패 시 false 반환
    }

    // 회원 탈퇴 메서드
    public boolean deleteAccount(String username) {
        String sql = "DELETE FROM user WHERE username = ?";
        try (Connection connection = getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0; // 삭제 성공 여부 반환
        } catch (SQLException e) {
            System.out.println("회원 탈퇴 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // 삭제 실패 시 false 반환
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
