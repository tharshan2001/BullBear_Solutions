package bullbear.app.repository;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUser(User user);

    Optional<Wallet> findByUserAndWalletType(User user, WalletType walletType);

    Optional<Wallet> findByUser_IdAndWalletType_WalletTypeId(Long userId, Long walletTypeId);

}
