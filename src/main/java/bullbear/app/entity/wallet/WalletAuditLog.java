package bullbear.app.entity.wallet;

import bullbear.app.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String action; // CREDIT, DEBIT, LOCK, UNLOCK

    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;

    private String reference; // TX_ID, SYSTEM, ADMIN_ACTION

    private LocalDateTime createdAt;
}