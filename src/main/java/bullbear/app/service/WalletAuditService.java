package bullbear.app.service;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletAuditLog;
import bullbear.app.repository.wallet.WalletAuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WalletAuditService {

    private final WalletAuditLogRepository auditLogRepository;

    public WalletAuditService(WalletAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Logs a single wallet audit entry for main balance or locked balance.
     *
     * @param user           the user performing the action
     * @param wallet         the wallet affected
     * @param action         action type (CREDIT, DEBIT, LOCK, UNLOCK)
     * @param amount         the amount changed
     * @param balanceBefore  balance before change
     * @param balanceAfter   balance after change
     * @param reference      reference ID or note
     */
    public void log(
            User user,
            Wallet wallet,
            String action,
            Double amount,
            Double balanceBefore,
            Double balanceAfter,
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
     *
     * @param user             the user performing the action
     * @param wallet           the wallet affected
     * @param actionMain       action for main balance
     * @param mainAmount       main balance change
     * @param actionLocked     action for locked balance
     * @param lockedAmount     locked balance change
     * @param reference        reference ID or note
     */
    public void logDouble(
            User user,
            Wallet wallet,
            String actionMain,
            Double mainAmount,
            String actionLocked,
            Double lockedAmount,
            String reference
    ) {
        LocalDateTime now = LocalDateTime.now();

        // Log main balance change
        if (mainAmount != null && mainAmount != 0) {
            WalletAuditLog mainLog = WalletAuditLog.builder()
                    .user(user)
                    .wallet(wallet)
                    .action(actionMain)
                    .amount(mainAmount)
                    .balanceBefore(wallet.getBalance())
                    .balanceAfter(wallet.getBalance() + mainAmount)
                    .reference(reference)
                    .createdAt(now)
                    .build();
            auditLogRepository.save(mainLog);

            // Update wallet main balance
            wallet.setBalance(wallet.getBalance() + mainAmount);
        }

        // Log locked balance change
        if (lockedAmount != null && lockedAmount != 0) {
            WalletAuditLog lockedLog = WalletAuditLog.builder()
                    .user(user)
                    .wallet(wallet)
                    .action(actionLocked)
                    .amount(lockedAmount)
                    .balanceBefore(wallet.getLockedBalance())
                    .balanceAfter(wallet.getLockedBalance() + lockedAmount)
                    .reference(reference)
                    .createdAt(now)
                    .build();
            auditLogRepository.save(lockedLog);

            // Update wallet locked balance
            wallet.setLockedBalance(wallet.getLockedBalance() + lockedAmount);
        }
    }
}
