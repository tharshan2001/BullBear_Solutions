package bullbear.app.repository;

import bullbear.app.entity.config.NetworkAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NetworkAddressRepository extends JpaRepository<NetworkAddress, Long> {
    Optional<NetworkAddress> findByNetworkAndActiveTrue(String network);
}
