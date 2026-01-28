package bullbear.app.entity.user;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

