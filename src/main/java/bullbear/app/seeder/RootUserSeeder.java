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

            // Generate unique code
            root.setCode(generateUniqueCode());

            // Premium always active for root
            LocalDateTime now = LocalDateTime.now();
            root.setPremiumActive(true);
            root.setPremiumActivatedDate(now);
            root.setPremiumExpiryDate(now.plusYears(100)); // practically permanent

            userRepository.save(root);
            System.out.println("✅ Root user created: " + rootEmail);
        } else {
            root = existingRoot.get();
            System.out.println("ℹ Root user already exists: " + rootEmail);
        }

        // Always print the code
        System.out.println("🔑 Root user code: " + root.getCode());
    }

    // Generate a unique BB code
    private String generateUniqueCode() {
        String code;
        Random random = new Random();
        do {
            code = "BB" + (100000 + random.nextInt(900000));
        } while (userRepository.findByCode(code).isPresent());
        return code;
    }
}
