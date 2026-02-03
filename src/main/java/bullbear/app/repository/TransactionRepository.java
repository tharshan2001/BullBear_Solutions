package bullbear.app.repository;

import bullbear.app.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser_Id(Long userId);

    boolean existsByTxHash(String txHash);

    List<Transaction> findByStatus(String status);
}