package bullbear.app.service;

import bullbear.app.entity.user.TempUser;
import bullbear.app.entity.user.User;
import bullbear.app.repository.user.TempUserRepository;
import bullbear.app.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TempUserRepository tempUserRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, TempUserRepository tempUserRepository) {
        this.userRepository = userRepository;
        this.tempUserRepository = tempUserRepository;
    }

    // =========================
    // Generate unique referral code
    // =========================
    private String generateUniqueCode() {
        String code;
        Random random = new Random();
        do {
            code = "BB" + (100000 + random.nextInt(900000)); // BB + 6 digits
        } while (userRepository.findByCode(code).isPresent());
        return code;
    }

    // =========================
    // Register User
    // =========================
    public User registerUser(String email, String fullName, String nic,
                             String phoneNumber, String password, String securityPin,
                             String referredByCode) {

        // Validate required inputs
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new RuntimeException("Full name cannot be empty");
        }
        if (nic == null || nic.isBlank()) {
            throw new RuntimeException("NIC cannot be empty");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new RuntimeException("Phone number cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }
        if (securityPin == null || securityPin.isBlank()) {
            throw new RuntimeException("Security PIN cannot be empty");
        }

        // 1️⃣ Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists with this email.");
        }

        // 2️⃣ Check if email is verified via TempUser
        TempUser tempUser = tempUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not verified via OTP"));

        if (!tempUser.isVerified()) {
            throw new RuntimeException("Email not verified via OTP");
        }

        // 3️⃣ Handle referral via code
        User referringUser = null;
        if (referredByCode != null && !referredByCode.isBlank()) {
            referringUser = userRepository.findByCode(referredByCode)
                    .orElseThrow(() -> new RuntimeException("Invalid referral code"));
        }

        // 4️⃣ Create new user
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setNic(nic);
        user.setPhoneNumber(phoneNumber);

        // Encode password and PIN safely
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setSecurityPin(passwordEncoder.encode(securityPin));

        // Generate unique referral code
        user.setCode(generateUniqueCode());

        // Set referral relationship
        if (referringUser != null) {
            user.setReferredBy(referringUser);
            referringUser.getReferences().add(user);
            userRepository.save(referringUser);
        }

        // 5️⃣ Activate Premium for 1 year
        LocalDateTime now = LocalDateTime.now();
        user.setPremiumActive(true);
        user.setPremiumActivatedDate(now);
        user.setPremiumExpiryDate(now.plusYears(1));

        // 6️⃣ Save new user
        User savedUser = userRepository.save(user);

        // 7️⃣ Delete temp user
        tempUserRepository.delete(tempUser);

        return savedUser;
    }

    // =========================
    // Login User
    // =========================
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("Email and password cannot be empty");
        }

        // 1️⃣ Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2️⃣ Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3️⃣ Return user object on successful login
        return user;
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
