package bullbear.app.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullName;
    private String nic;
    private String phoneNumber;
    private String password;
    private String securityPin;
    private String referredByCode;
}

