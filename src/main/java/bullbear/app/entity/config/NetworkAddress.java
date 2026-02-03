package bullbear.app.entity.config;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "network_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String network; // TRC20 / BEP20
    private String address; // Your wallet address
    private String label;
    private Boolean active;
    private LocalDateTime createdAt;
}
