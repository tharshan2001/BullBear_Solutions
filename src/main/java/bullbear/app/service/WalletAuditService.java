package bullbear.app.service;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletAuditLog;
import bullbear.app.repository.wallet.WalletAuditLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletAuditService {

    private final WalletAuditLogRepository auditLogRepository;

    public WalletAuditService(WalletAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Logs a single wallet audit entry for main balance or locked balance.
     */
    public void log(
            User user,
            Wallet wallet,
            String action,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String reference
    ) {
        WalletAuditLog log = WalletAuditLog.builder()
                .user(user)
                .wallet(wallet)
                .action(action)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .reference(reference)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }

    /**
     * Logs both main balance and locked balance changes in one call.
     */
    public void logDouble(
            User user,
            Wallet wallet,
            String actionMain,
            BigDecimal mainAmount,
            String actionLocked,
            BigDecimal lockedAmount,
            String reference
    ) {
        LocalDateTime now = LocalDateTime.now();

        // Log main balance change
        if (mainAmount != null && mainAmount.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal balanceBefore = wallet.getBalance();
            BigDecimal balanceAfter = balanceBefore.add(mainAmount);

            WalletAuditLog mainLog = WalletAuditLog.builder()
                    .user(user)
                    .wallet(wallet)
                    .action(actionMain)
                    .amount(mainAmount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .reference(reference)
                    .createdAt(now)
                    .build();
            auditLogRepository.save(mainLog);

            // Update wallet main balance
            wallet.setBalance(balanceAfter);
        }

        // Log locked balance change
        if (lockedAmount != null && lockedAmount.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal lockedBefore = wallet.getLockedBalance();
            BigDecimal lockedAfter = lockedBefore.add(lockedAmount);

            WalletAuditLog lockedLog = WalletAuditLog.builder()
                    .user(user)
                    .wallet(wallet)
                    .action(actionLocked)
                    .amount(lockedAmount)
                    .balanceBefore(lockedBefore)
                    .balanceAfter(lockedAfter)
                    .reference(reference)
                    .createdAt(now)
                    .build();
            auditLogRepository.save(lockedLog);

            // Update wallet locked balance
            wallet.setLockedBalance(lockedAfter);
        }
    }
}
