package bullbear.app.service;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletType;
import bullbear.app.repository.WalletRepository;
import bullbear.app.repository.WalletTypeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTypeRepository walletTypeRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletTypeRepository walletTypeRepository) {
        this.walletRepository = walletRepository;
        this.walletTypeRepository = walletTypeRepository;
    }

    // ============================
    // Create Wallet
    // ============================
    public Wallet createWallet(User user, String walletTypeName) {
        WalletType walletType = walletTypeRepository.findByName(walletTypeName)
                .orElseThrow(() -> new RuntimeException("Wallet type not found: " + walletTypeName));

        // Prevent duplicate wallets
        walletRepository.findByUserAndWalletType(user, walletType)
                .ifPresent(w -> {
                    throw new RuntimeException("Wallet already exists for user");
                });

        Wallet wallet = Wallet.builder()
                .user(user)
                .walletType(walletType)
                .balance(BigDecimal.ZERO)
                .lockedBalance(BigDecimal.ZERO)
                .build();

        return walletRepository.save(wallet);
    }

    // ============================
    // Get User Wallets
    // ============================
    public List<Wallet> getUserWallets(User user) {
        return walletRepository.findByUser(user);
    }

    // ============================
    // Balance Operations
    // ============================
    public void credit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    public void debit(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    public void lockAmount(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance to lock");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setLockedBalance(wallet.getLockedBalance().add(amount));
        walletRepository.save(wallet);
    }

    public void unlockAmount(Wallet wallet, BigDecimal amount) {
        if (wallet.getLockedBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient locked balance");
        }
        wallet.setLockedBalance(wallet.getLockedBalance().subtract(amount));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }
}