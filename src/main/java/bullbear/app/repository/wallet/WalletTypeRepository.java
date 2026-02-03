package bullbear.app.repository.wallet;

import bullbear.app.entity.wallet.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface WalletTypeRepository extends JpaRepository<WalletType, Long> {
    Optional<WalletType> findByName(String name);
}
