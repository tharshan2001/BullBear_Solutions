package bullbear.app.service;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletAuditLog;
import bullbear.app.repository.WalletAuditLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletAuditService {

    private final WalletAuditLogRepository auditLogRepository;

    public WalletAuditService(WalletAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            User user,
            Wallet wallet,
            String action,
            BigDecimal amount,
            BigDecimal before,
            BigDecimal after,
            String reference
    ) {
        WalletAuditLog log = WalletAuditLog.builder()
                .user(user)
                .wallet(wallet)
                .action(action)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .reference(reference)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}