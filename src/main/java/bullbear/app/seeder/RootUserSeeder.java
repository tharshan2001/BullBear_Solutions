package bullbear.app.seeder;


import bullbear.app.entity.user.User;
import bullbear.app.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;



@Component
public class RootUserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        String rootEmail = "bullbear.solution@gmail.com";

        Optional<User> existingRoot = userRepository.findByEmail(rootEmail);

        User root;
        if (existingRoot.isEmpty()) {
            root = new User();
            root.setEmail(rootEmail);
            root.setFullName("Root Admin");
            root.setNic("ROOT0000X");
            root.setPhoneNumber("+0000000000");
            root.setPasswordHash(passwordEncoder.encode("Root@1234")); // default password
            root.setSecurityPin(passwordEncoder.encode("0000"));

            // Premium always active for root
            LocalDateTime now = LocalDateTime.now();
            root.setPremiumActive(true);
            root.setPremiumActivatedDate(now);
            root.setPremiumExpiryDate(now.plusYears(100)); // practically permanent

            root = userRepository.save(root);
            root.setCode("BB" + System.nanoTime());
            root = userRepository.save(root);
            System.out.println("✅ Root user created: " + rootEmail);
        } else {
            root = existingRoot.get();
            if (root.getCode() == null) {
                root.setCode("BB" + System.nanoTime());
                root = userRepository.save(root);
            }
            System.out.println("ℹ Root user already exists: " + rootEmail);
        }

        System.out.println("🔑 Root user referral code (use this to register): " + root.getCode());
    }
}
