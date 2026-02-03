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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletAuditLogRepository auditLogRepository;
    private final TransactionRepository transactionRepository;
    private final WalletTypeRepository walletTypeRepository;

    public WalletService(WalletRepository walletRepository, WalletAuditLogRepository auditLogRepository, TransactionRepository transactionRepository, WalletTypeRepository walletTypeRepository) {
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
        // Avoid duplicate credit
        if (transactionRepository.existsByTxHash(txHash)) return;

        // Get wallet from transaction entity
        Wallet wallet = tx.getWallet();
        if (wallet == null) throw new RuntimeException("Wallet not found");

        // Convert USDT to points (example rate)
        double points = tx.getAmount() * 100;

        double before = wallet.getBalance();
        wallet.setBalance(before + points);
        walletRepository.save(wallet);

        // Create audit log
        auditLogRepository.save(WalletAuditLog.builder().user(wallet.getUser()).wallet(wallet).action("DEPOSIT").amount(points).balanceBefore(before).balanceAfter(wallet.getBalance()).reference(txHash).createdAt(LocalDateTime.now()).build());

        // Update transaction
        tx.setTxHash(txHash);
        tx.setStatus("SUCCESS");
        transactionRepository.save(tx);
    }

    // ===========================
    // REQUEST WITHDRAWAL
    // ===========================
    @Transactional
    public void requestWithdraw(Wallet wallet, double points) {
        if (wallet.getBalance() < points) throw new RuntimeException("Insufficient balance");

        // Lock funds
        wallet.setBalance(wallet.getBalance() - points);
        wallet.setLockedBalance(wallet.getLockedBalance() + points);
        walletRepository.save(wallet);

        // Optional: Create audit log for withdrawal request
        auditLogRepository.save(WalletAuditLog.builder().user(wallet.getUser()).wallet(wallet).action("WITHDRAW_REQUEST").amount(points).balanceBefore(wallet.getBalance() + points).balanceAfter(wallet.getBalance()).reference("REQUEST").createdAt(LocalDateTime.now()).build());
    }

    // ===========================
    // CONFIRM WITHDRAWAL (after blockchain tx)
    // ===========================
    @Transactional
    public void confirmWithdraw(Wallet wallet, double points, String txHash) {
        if (wallet.getLockedBalance() < points) throw new RuntimeException("Insufficient locked balance");

        wallet.setLockedBalance(wallet.getLockedBalance() - points);
        walletRepository.save(wallet);

        // Audit log for completed withdrawal
        auditLogRepository.save(WalletAuditLog.builder().user(wallet.getUser()).wallet(wallet).action("WITHDRAW_COMPLETE").amount(points).balanceBefore(wallet.getLockedBalance() + points).balanceAfter(wallet.getLockedBalance()).reference(txHash).createdAt(LocalDateTime.now()).build());
    }

    // ===========================
    // GET USER WALLETS
    // ===========================
    public List<Wallet> getUserWallets(User user) {
        return walletRepository.findByUser(user);
    }


    @Transactional
    public void createWallet(User user, String walletTypeName) {
        // Find wallet type
        WalletType type = walletTypeRepository.findByName(walletTypeName).orElseThrow(() -> new RuntimeException("Wallet type not found: " + walletTypeName));

        Wallet wallet = Wallet.builder().user(user).walletType(type).balance(0.0).lockedBalance(0.0).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        walletRepository.save(wallet);
    }

}
