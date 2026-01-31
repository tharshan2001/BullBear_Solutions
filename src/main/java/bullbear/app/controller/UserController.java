package bullbear.app.controller;

import bullbear.app.dto.auth.ApiResponse;
import bullbear.app.dto.auth.LoginRequest;
import bullbear.app.dto.auth.RegisterRequest;
import bullbear.app.entity.user.User;
import bullbear.app.service.UserService;
import bullbear.app.utils.NotificationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final NotificationUtil notificationUtil;

    public UserController(UserService userService, NotificationUtil notificationUtil) {
        this.userService = userService;
        this.notificationUtil = notificationUtil;
    }

    // ============================
    // Register User
    // ============================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            // Register user and get the User object
            User newUser = userService.registerUser(
                    request.getEmail(),
                    request.getFullName(),
                    request.getNic(),
                    request.getPhoneNumber(),
                    request.getPassword(),
                    request.getSecurityPin(),
                    request.getReferredByCode()
            );

            // Send notification after successful registration
            notificationUtil.notifyUser(
                    newUser.getUserId(),
                    "SYSTEM",
                    "Welcome " + newUser.getFullName() + "! Your registration was successful."
            );

            // Return response
            return ResponseEntity.ok(new ApiResponse("Registration successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    // ============================
    // Login User
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            // Call service to authenticate user
            User user = userService.login(request.getEmail(), request.getPassword());

            // Return success response (you can return JWT if implemented)
            return ResponseEntity.ok(new ApiResponse("Login successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }
}