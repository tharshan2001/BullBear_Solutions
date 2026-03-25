package bullbear.app.entity.user;

import bullbear.app.entity.product.Subscription;
import bullbear.app.entity.transaction.Transaction;
import bullbear.app.entity.wallet.Wallet;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;
    private String nic;
    private String phoneNumber;

    @Column(nullable = false)
    private String passwordHash;

    private String securityPin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referer_id")
    private User referredBy;

    @OneToMany(mappedBy = "referredBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> referrals = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_references", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "referenced_user_id")
    private List<Long> references = new ArrayList<>();

    private Integer level = 1;

    private boolean premiumActive;
    private LocalDateTime premiumActivatedDate;
    private LocalDateTime premiumExpiryDate;

    private String code;

    private String role = "ROLE_USER";

    @Column(precision = 19, scale = 8)
    private BigDecimal walletUsdt = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal walletCw = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal personalSales = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal directSponsorSales = BigDecimal.ZERO;

    @Column(precision = 19, scale = 8)
    private BigDecimal groupSales = BigDecimal.ZERO;

    private String otp;
    private LocalDateTime otpExpires;
    private boolean otpVerified = false;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime lastLogin;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommission> commissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getUserId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }
}