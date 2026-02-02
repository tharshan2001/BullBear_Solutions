package bullbear.app.entity.wallet;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallet_types")
public class WalletType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletTypeId;

    @Column(unique = true, nullable = false, length = 20)
    private String name; // e.g. CW, USDT

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
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