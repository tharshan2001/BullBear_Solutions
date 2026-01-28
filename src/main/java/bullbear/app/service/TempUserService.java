package bullbear.app.service;

import bullbear.app.entity.user.TempUser;
import bullbear.app.repository.TempUserRepository;
import bullbear.app.utils.EmailSender;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service
public class TempUserService {

    @Autowired
    private TempUserRepository tempUserRepository;

    @Autowired
    private EmailSender emailSender;

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void createTempUser(String email) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Optional<TempUser> existingTemp = tempUserRepository.findByEmail(email);

        if (existingTemp.isPresent() && existingTemp.get().getOtpExpires() != null
                && existingTemp.get().getOtpExpires().isAfter(now)) {
            throw new Exception("OTP already sent. Please wait until it expires.");
        }

        String otp = generateOtp();
        LocalDateTime otpExpires = now.plusMinutes(2);

        TempUser tempUser = existingTemp.orElse(new TempUser());
        tempUser.setEmail(email);
        tempUser.setOtp(otp);
        tempUser.setOtpExpires(otpExpires);
        tempUser.setVerified(false);

        tempUserRepository.save(tempUser);

        // Send OTP via Email
        try {
            emailSender.sendEmail(email, "Your OTP Code",
                    "<p>Your OTP code is <strong>" + otp + "</strong>. It will expire in 2 minutes.</p>",
                    Collections.emptyList());
        } catch (MessagingException e) {
            throw new Exception("Failed to send OTP email: " + e.getMessage());
        }
    }

    public void verifyOtp(String email, String otp) throws Exception {
        TempUser tempUser = tempUserRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("No OTP request found for this email"));

        if (tempUser.getOtpExpires() == null || tempUser.getOtpExpires().isBefore(LocalDateTime.now())) {
            throw new Exception("OTP has expired. Please request a new one.");
        }

        if (!tempUser.getOtp().equals(otp)) {
            throw new Exception("Invalid OTP");
        }

        tempUser.setVerified(true);
        tempUserRepository.save(tempUser);
    }

    public boolean isVerified(String email) {
        return tempUserRepository.findByEmail(email).map(TempUser::isVerified).orElse(false);
    }

    public void deleteTempUser(String email) {
        tempUserRepository.findByEmail(email).ifPresent(tempUserRepository::delete);
    }
}
