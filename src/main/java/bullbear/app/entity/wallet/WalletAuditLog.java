package bullbear.app.entity.wallet;

import bullbear.app.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NotNull
@Entity
@Table(name = "wallet_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String action;

    private Double amount;
    private Double balanceBefore;
    private Double balanceAfter;

    private String reference;

    private LocalDateTime createdAt;
}