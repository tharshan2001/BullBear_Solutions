package bullbear.app.controller;

import bullbear.app.service.TempUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp-users")
public class TempUserController {

    @Autowired
    private TempUserService tempUserService;

    // Send OTP
    @PostMapping("/otp")
    public ResponseEntity<?> createTempUser(@RequestBody EmailRequest request) {
        try {
            tempUserService.createTempUser(request.getEmail());
            return ResponseEntity.ok("OTP sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Verify OTP
    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
        try {
            tempUserService.verifyOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok("OTP verified successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Request DTOs
    @Data
    static class EmailRequest {
        private String email;
    }

    @Data
    static class OtpRequest {
        private String email;
        private String otp;
    }
}
