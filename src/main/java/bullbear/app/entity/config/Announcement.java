package bullbear.app.entity.config;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer announcementId;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String imageUrl;
    private Boolean isPublished;
    private LocalDateTime createdAt;
}
