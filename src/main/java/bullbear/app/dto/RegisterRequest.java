package bullbear.app.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;
    private String securityPin;
    private Integer referredBy; // Optional userId of referrer
}

