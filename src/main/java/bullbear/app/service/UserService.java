package bullbear.app.service;

import bullbear.app.entity.user.TempUser;
import bullbear.app.entity.user.User;
import bullbear.app.repository.TempUserRepository;
import bullbear.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TempUserRepository tempUserRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Generate unique referral code for user
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
                             String referredByCode) throws Exception {

        // 1️⃣ Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("User already exists with this email.");
        }

        // 2️⃣ Check if email is verified via TempUser
        TempUser tempUser = tempUserRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Email not verified via OTP"));

        if (!tempUser.isVerified()) {
            throw new Exception("Email not verified via OTP");
        }

        // 3️⃣ Handle referral via code
        User referringUser = null;
        if (referredByCode != null && !referredByCode.isEmpty()) {
            referringUser = userRepository.findByCode(referredByCode)
                    .orElseThrow(() -> new Exception("Invalid referral code"));
        }

        // 4️⃣ Create new user
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setNic(nic);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setSecurityPin(passwordEncoder.encode(securityPin));

        // Generate unique referral code
        user.setCode(generateUniqueCode());

        // Set referral relationship
        if (referringUser != null) {
            user.setReferredBy(referringUser);
            referringUser.getReferences().add(user); // add this user to referrer's references
            userRepository.save(referringUser);      // save referring user
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
    public User login(String email, String password) throws Exception {
        // 1️⃣ Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Invalid email or password"));

        // 2️⃣ Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new Exception("Invalid email or password");
        }

        // 3️⃣ Return user object on successful login
        return user;
    }
}