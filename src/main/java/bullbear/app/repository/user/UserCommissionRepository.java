package bullbear.app.repository.user;

import bullbear.app.entity.user.UserCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCommissionRepository extends JpaRepository<UserCommission, Integer> {

    List<UserCommission> findByUser_Id(Long userId);

    List<UserCommission> findByUser_IdAndStatus(Long userId, String status);

    List<UserCommission> findBySourceUser_Id(Long sourceUserId);
}