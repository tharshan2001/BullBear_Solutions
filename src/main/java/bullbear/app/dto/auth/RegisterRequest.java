package bullbear.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "NIC is required")
    private String nic;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Security PIN is required")
    private String securityPin;

    @NotBlank(message = "Referral code is required")
    private String referredByCode;
}

