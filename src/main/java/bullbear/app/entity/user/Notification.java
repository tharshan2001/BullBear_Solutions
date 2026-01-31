package bullbear.app.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    private Integer userId;

    private String type; // e.g., TRANSACTION, SYSTEM, COMMISSION

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean read = false;

    private LocalDateTime createdAt;
}