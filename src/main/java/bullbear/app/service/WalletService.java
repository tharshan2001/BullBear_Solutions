package bullbear.app.service;

import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletType;
import bullbear.app.repository.WalletRepository;
import bullbear.app.repository.WalletTypeRepository;
import org.springframework.stereotype.Service;

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
                .balance(0.0)
                .lockedBalance(0.0)
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
    public void credit(Wallet wallet, double amount) {
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    public void debit(Wallet wallet, double amount) {
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
    }

    public void lockAmount(Wallet wallet, double amount) {
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance to lock");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setLockedBalance(wallet.getLockedBalance() + amount);
        walletRepository.save(wallet);
    }

    public void unlockAmount(Wallet wallet, double amount) {
        if (wallet.getLockedBalance() < amount) {
            throw new RuntimeException("Insufficient locked balance");
        }
        wallet.setLockedBalance(wallet.getLockedBalance() - amount);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }
}
