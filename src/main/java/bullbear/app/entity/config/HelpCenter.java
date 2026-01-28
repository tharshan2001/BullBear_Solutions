package bullbear.app.entity.config;

import bullbear.app.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "help_center")
public class HelpCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "boolean default false")
    private Boolean read = false;

    private LocalDateTime createdAt;

    // Optional: track admin reply
    @Column(columnDefinition = "TEXT")
    private String adminReply;
}
