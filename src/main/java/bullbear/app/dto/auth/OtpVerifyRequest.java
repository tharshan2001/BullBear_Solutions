package bullbear.app.dto.auth;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private String otp;
}
