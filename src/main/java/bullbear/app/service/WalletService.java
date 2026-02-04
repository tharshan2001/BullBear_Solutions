package bullbear.app.service;

import bullbear.app.entity.transaction.Transaction;
import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletAuditLog;
import bullbear.app.entity.wallet.WalletType;
import bullbear.app.repository.TransactionRepository;
import bullbear.app.repository.wallet.WalletAuditLogRepository;
import bullbear.app.repository.wallet.WalletRepository;
import bullbear.app.repository.wallet.WalletTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletAuditLogRepository auditLogRepository;
    private final TransactionRepository transactionRepository;
    private final WalletTypeRepository walletTypeRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletAuditLogRepository auditLogRepository,
                         TransactionRepository transactionRepository,
                         WalletTypeRepository walletTypeRepository) {
        this.walletRepository = walletRepository;
        this.auditLogRepository = auditLogRepository;
        this.transactionRepository = transactionRepository;
        this.walletTypeRepository = walletTypeRepository;
    }

    // ===========================
    // CREDIT DEPOSIT
    // ===========================
    @Transactional
    public void creditDeposit(Transaction tx, String txHash) {
        if (transactionRepository.existsByTxHash(txHash)) return;

        Wallet wallet = tx.getWallet();
        if (wallet == null) throw new RuntimeException("Wallet not found");

        BigDecimal points = tx.getAmount().multiply(BigDecimal.valueOf(100));

        BigDecimal before = wallet.getBalance();
        wallet.setBalance(before.add(points));
        walletRepository.save(wallet);

        auditLogRepository.save(
                WalletAuditLog.builder()
                        .user(wallet.getUser())
                        .wallet(wallet)
                        .action("DEPOSIT")
                        .amount(points)
                        .balanceBefore(before)
                        .balanceAfter(wallet.getBalance())
                        .reference(txHash)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        tx.setTxHash(txHash);
        tx.setStatus("SUCCESS");
        transactionRepository.save(tx);
    }

    // ===========================
    // REQUEST WITHDRAWAL
    // ===========================
    @Transactional
    public void requestWithdraw(Wallet wallet, BigDecimal points) {
        if (wallet.getBalance().compareTo(points) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal before = wallet.getBalance();
        wallet.setBalance(before.subtract(points));
        wallet.setLockedBalance(wallet.getLockedBalance().add(points));
        walletRepository.save(wallet);

        auditLogRepository.save(
                WalletAuditLog.builder()
                        .user(wallet.getUser())
                        .wallet(wallet)
                        .action("WITHDRAW_REQUEST")
                        .amount(points)
                        .balanceBefore(before)
                        .balanceAfter(wallet.getBalance())
                        .reference("REQUEST")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // ===========================
    // CONFIRM WITHDRAWAL
    // ===========================
    @Transactional
    public void confirmWithdraw(Wallet wallet, BigDecimal points, String txHash) {
        if (wallet.getLockedBalance().compareTo(points) < 0) {
            throw new RuntimeException("Insufficient locked balance");
        }

        BigDecimal before = wallet.getLockedBalance();
        wallet.setLockedBalance(before.subtract(points));
        walletRepository.save(wallet);

        auditLogRepository.save(
                WalletAuditLog.builder()
                        .user(wallet.getUser())
                        .wallet(wallet)
                        .action("WITHDRAW_COMPLETE")
                        .amount(points)
                        .balanceBefore(before)
                        .balanceAfter(wallet.getLockedBalance())
                        .reference(txHash)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // ===========================
    // GET USER WALLETS
    // ===========================
    public List<Wallet> getUserWallets(User user) {
        return walletRepository.findByUser(user);
    }

    // ===========================
    // CREATE WALLET
    // ===========================
    @Transactional
    public void createWallet(User user, String walletTypeName) {
        WalletType type = walletTypeRepository.findByName(walletTypeName)
                .orElseThrow(() -> new RuntimeException("Wallet type not found: " + walletTypeName));

        Wallet wallet = Wallet.builder()
                .user(user)
                .walletType(type)
                .balance(BigDecimal.ZERO)
                .lockedBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);
    }
}
