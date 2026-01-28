package bullbear.app.entity.config;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bannerId;

    private String title;
    private String imageUrl;
    private String link;
    private Boolean active;
    private LocalDateTime createdAt;
}
