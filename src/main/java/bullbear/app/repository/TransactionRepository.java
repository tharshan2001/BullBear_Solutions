package bullbear.app.repository;

import bullbear.app.entity.wallet.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUser_Id(Integer userId);
}