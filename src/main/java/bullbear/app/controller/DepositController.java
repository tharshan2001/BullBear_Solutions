package bullbear.app.controller;

import bullbear.app.entity.config.NetworkAddress;
import bullbear.app.entity.transaction.Transaction;
import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.repository.NetworkAddressRepository;
import bullbear.app.repository.TransactionRepository;
import bullbear.app.repository.user.UserRepository;
import bullbear.app.repository.wallet.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/deposit")
public class DepositController {

    private final NetworkAddressRepository addressRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public DepositController(NetworkAddressRepository addressRepo,
                             TransactionRepository transactionRepo,
                             UserRepository userRepository,
                             WalletRepository walletRepository) {
        this.addressRepo = addressRepo;
        this.transactionRepo = transactionRepo;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping("/init")
    public DepositResponse initDeposit(
            @RequestParam Long userId,
            @RequestParam Long walletId,
            @RequestParam Double points,
            @RequestParam String network
    ) {
        // Find network address
        NetworkAddress addr = addressRepo.findByNetworkAndActiveTrue(network)
                .orElseThrow(() -> new RuntimeException("Network not supported"));

        // Convert points to USDT (example)
        double usdt = points / 100 + Math.random() / 1000;

        // Fetch entities
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Create transaction
        Transaction tx = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type("DEPOSIT")
                .amount(usdt)
                .currency("USDT")
                .network(network)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepo.save(tx);

        return new DepositResponse(addr.getAddress(), network, usdt, tx.getTransactionId());
    }

    @Data
    @AllArgsConstructor
    static class DepositResponse {
        private String address;
        private String network;
        private Double amount;
        private Long transactionId;
    }
}
