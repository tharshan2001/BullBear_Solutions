package bullbear.app.service;

import bullbear.app.entity.user.TempUser;
import bullbear.app.entity.user.User;
import bullbear.app.repository.TempUserRepository;
import bullbear.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TempUserRepository tempUserRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(String email, String fullName, String nic,
                             String phoneNumber, String password, String securityPin,
                             Long referredById) throws Exception {

        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("User already exists with this email.");
        }

        // Check if email is verified via TempUser
        TempUser tempUser = tempUserRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Email not verified via OTP"));

        if (!tempUser.isVerified()) {
            throw new Exception("Email not verified via OTP");
        }

        User referringUser = null;
        if (referredById != null) {
            referringUser = userRepository.findById(referredById)
                    .orElseThrow(() -> new Exception("Invalid referral ID"));
        }

        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setNic(nic);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setSecurityPin(passwordEncoder.encode(securityPin));

        // Handle referral
        if (referringUser != null) {
            user.setReferredBy(referringUser);
            referringUser.getReferences().add(user);
            userRepository.save(referringUser); // save referring user with new reference
        }

        // Activate Premium for 1 year
        LocalDateTime now = LocalDateTime.now();
        user.setPremiumActive(true);
        user.setPremiumActivatedDate(now);
        user.setPremiumExpiryDate(now.plusYears(1));

        User savedUser = userRepository.save(user);

        // Delete temp user record
        tempUserRepository.delete(tempUser);

        return savedUser;
    }
}
