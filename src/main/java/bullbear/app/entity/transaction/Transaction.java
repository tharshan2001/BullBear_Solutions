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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal amount;

    @Column(length = 10)
    private String currency;

    @Column(length = 50)
    private String network;

    @Column(length = 100)
    private String txHash;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    @Builder.Default
    private boolean adminApproved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_approved_by")
    private Admin adminApprovedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}