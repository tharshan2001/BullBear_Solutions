package bullbear.app.service;

import bullbear.app.dto.auth.BlockchainTx;
import bullbear.app.entity.transaction.Transaction;
import bullbear.app.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockchainScannerService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final BlockchainApi blockchainApi; // inject as service

    public BlockchainScannerService(TransactionRepository transactionRepository,
                                    WalletService walletService,
                                    BlockchainApi blockchainApi) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.blockchainApi = blockchainApi;
    }

    @Scheduled(fixedDelay = 60000) // every 60 seconds
    public void scanDeposits() {
        List<Transaction> pending = transactionRepository.findByStatus("PENDING");

        for (Transaction tx : pending) {
            try {
                // Pass your entity Transaction, not any other type
                BlockchainTx found = blockchainApi.findDeposit(tx);

                // Confirmations depending on network
                int requiredConfirmations = tx.getNetwork().equalsIgnoreCase("TRC20") ? 12 : 15;

                if (found != null && found.getConfirmations() >= requiredConfirmations) {
                    walletService.creditDeposit(tx, found.getHash());
                    System.out.println("Credited tx: " + found.getHash());
                }

            } catch (Exception e) {
                System.err.println("Error scanning tx " + tx.getTransactionId() + ": " + e.getMessage());
            }
        }
    }
}
