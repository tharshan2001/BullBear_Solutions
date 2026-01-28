package bullbear.app.entity.transaction;

import bullbear.app.entity.user.Admin;
import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private String type;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String network;
    private String txHash;
    private Boolean adminApproved;

    @ManyToOne
    @JoinColumn(name = "admin_approved_by")
    private Admin adminApprovedBy;

    private LocalDateTime createdAt;
}
