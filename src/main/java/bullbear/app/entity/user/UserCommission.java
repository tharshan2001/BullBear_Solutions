package bullbear.app.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_commissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50)
    private String type;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    private User sourceUser;

    @Column
    private Integer level;

    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}