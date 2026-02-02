package bullbear.app.repository;

import bullbear.app.entity.wallet.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTypeRepository extends JpaRepository<WalletType, Integer> {

    Optional<WalletType> findByName(String name);
}