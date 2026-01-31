package bullbear.app.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@NullMarked
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

    @ManyToOne
    @JoinColumn(name = "referer")
    private User referredBy;

    @OneToMany(mappedBy = "referredBy", cascade = CascadeType.ALL)
    private List<User> references = new ArrayList<>();

    private boolean premiumActive;
    private LocalDateTime premiumActivatedDate;
    private LocalDateTime premiumExpiryDate;

    @Column(unique = true, nullable = false)
    private String code;

    // Example role field for future expansion
    private String role = "ROLE_USER"; // default role

    // =============================
    // UserDetails implementation
    // =============================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    public Integer getUserId() {
        return id != null ? id.intValue() : null;
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
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
