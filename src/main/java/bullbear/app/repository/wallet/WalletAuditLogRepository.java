package bullbear.app.repository.wallet;

import bullbear.app.entity.wallet.WalletAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletAuditLogRepository extends JpaRepository<WalletAuditLog, Long> {

    List<WalletAuditLog> findByWallet_Id(Long walletId);
}