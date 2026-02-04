package bullbear.app.service;

import bullbear.app.entity.transaction.Transaction;
import bullbear.app.entity.user.User;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ===========================
    // CREATE TRANSACTION
    // ===========================
    public Transaction createTransaction(
            User user,
            Wallet wallet,
            String type,
            BigDecimal amount, // changed from Double to BigDecimal
            String status,
            String reference
    ) {
        Transaction tx = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type(type)
                .amount(amount) // now BigDecimal
                .status(status)
                .currency(wallet.getWalletType().getName())
                .txHash(reference)
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(tx);
    }

    // ===========================
    // GET USER TRANSACTIONS
    // ===========================
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUser_Id(userId);
    }
}
