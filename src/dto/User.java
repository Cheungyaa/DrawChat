// src/dto/User.java

package dto;

public class User {
    private String username;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String detailAddress;

    // 생성자
    public User(String username, String name, String email, String phone, String address, String detailAddress) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    // Getter 메서드
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }
}
