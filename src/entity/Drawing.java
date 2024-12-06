package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drawings")
public class Drawing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Lob
    private byte[] image;

    @Column(nullable = false)
    private Long timestamp;

    // Getters and setters
}
