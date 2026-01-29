package bullbear.app.controller;

import bullbear.app.dto.auth.ApiResponse;
import bullbear.app.dto.auth.EmailRequest;
import bullbear.app.dto.auth.OtpRequest;
import bullbear.app.service.TempUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/temp")
public class TempUserController {

    @Autowired
    private TempUserService tempUserService;

    // Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> createTempUser(@RequestBody EmailRequest request) {
        try {
            tempUserService.createTempUser(request.getEmail());
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage()));
        }
    }


    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
        try {
            tempUserService.verifyOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(new ApiResponse("OTP verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage()));
        }
    }


}
