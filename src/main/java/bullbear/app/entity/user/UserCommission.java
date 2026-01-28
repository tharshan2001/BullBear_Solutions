package bullbear.app.entity.user;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_commissions")
public class UserCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commissionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type; // direct, level, etc.
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "source_user_id")
    private User sourceUser;

    private Integer level; // referral level
    private String status; // pending, paid, etc.
    private LocalDateTime createdAt;
}
