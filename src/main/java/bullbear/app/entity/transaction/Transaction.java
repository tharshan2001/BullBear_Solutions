package bullbear.app.entity.transaction;

import bullbear.app.entity.user.Admin;
import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private String type; // DEPOSIT, WITHDRAW, TRANSFER, COMMISSION

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal amount; // changed from Double to BigDecimal

    private String currency;
    private String network;
    private String txHash;

    @Column(nullable = false)
    private String status; // PENDING, SUCCESS, FAILED

    private boolean adminApproved;

    @ManyToOne
    @JoinColumn(name = "admin_approved_by")
    private Admin adminApprovedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
