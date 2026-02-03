package bullbear.app.repository.user;

import bullbear.app.entity.user.TempUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TempUserRepository extends JpaRepository<TempUser, Long> {
    Optional<TempUser> findByEmail(String email);
}
