package bullbear.app.repository;

import bullbear.app.entity.wallet.WalletAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletAuditLogRepository extends JpaRepository<WalletAuditLog, Integer> {

    List<WalletAuditLog> findByWallet_WalletId(Integer walletId);
}