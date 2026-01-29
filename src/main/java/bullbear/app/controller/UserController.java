package bullbear.app.controller;

import bullbear.app.dto.auth.ApiResponse;
import bullbear.app.dto.auth.RegisterRequest;
import bullbear.app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            userService.registerUser(
                    request.getEmail(),
                    request.getFullName(),
                    request.getNic(),
                    request.getPhoneNumber(),
                    request.getPassword(),
                    request.getSecurityPin(),
                    request.getReferredByCode()
            );

            // Only send message
            return ResponseEntity.ok(new ApiResponse("Registration successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

}
