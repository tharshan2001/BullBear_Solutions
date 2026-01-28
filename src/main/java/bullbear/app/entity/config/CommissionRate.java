package bullbear.app.entity.config;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commission_rate")
public class CommissionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

    private BigDecimal directCommission;
    private BigDecimal commissionPool;
    private String levelCommission; // e.g., "5,3,2" for levels
    private LocalDateTime createdAt;
}

