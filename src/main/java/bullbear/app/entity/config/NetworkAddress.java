package bullbear.app.entity.config;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "network_addresses")
public class NetworkAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    private String network; // e.g., TRC20, BEP20
    private String address;
    private String label;
    private Boolean active = true;
    private LocalDateTime createdAt;
}
