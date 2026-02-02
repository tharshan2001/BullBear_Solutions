package bullbear.app.repository;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    List<Wallet> findByUser(User user);

    Optional<Wallet> findByUserAndWalletType(User user, WalletType walletType);
}