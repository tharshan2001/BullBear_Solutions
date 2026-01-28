package bullbear.app.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;
    private String nic;
    private String phoneNumber;
    private String passwordHash;
    private String securityPin;

    // Optional referral
    @ManyToOne
    @JoinColumn(name = "referer")
    private User referredBy;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToMany(mappedBy = "referredBy", cascade = CascadeType.ALL)
    private List<User> references = new ArrayList<>();

    // Premium info
    private boolean premiumActive;
    private LocalDateTime premiumActivatedDate;
    private LocalDateTime premiumExpiryDate;

}
