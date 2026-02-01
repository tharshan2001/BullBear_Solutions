package bullbear.app.controller;

import bullbear.app.dto.auth.ApiResponse;
import bullbear.app.dto.auth.LoginRequest;
import bullbear.app.dto.auth.RegisterRequest;
import bullbear.app.entity.user.User;
import bullbear.app.security.JwtUtil;
import bullbear.app.service.UserService;
import bullbear.app.utils.NotificationUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final NotificationUtil notificationUtil;
    private final JwtUtil jwtUtil;

    public UserController(
            UserService userService,
            NotificationUtil notificationUtil,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.notificationUtil = notificationUtil;
        this.jwtUtil = jwtUtil;
    }

    // ============================
    // Register User
    // ============================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.registerUser(
                    request.getEmail(),
                    request.getFullName(),
                    request.getNic(),
                    request.getPhoneNumber(),
                    request.getPassword(),
                    request.getSecurityPin(),
                    request.getReferredByCode()
            );

            notificationUtil.notifyUser(
                    newUser.getUserId(),
                    "SYSTEM",
                    "Welcome " + newUser.getFullName() + "!"
            );

            return ResponseEntity.ok(new ApiResponse("Registration successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    // ============================
    // Login User (COOKIE BASED)
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());

            String token = jwtUtil.generateToken(user.getEmail());

            // 🔐 HttpOnly JWT cookie
            response.addHeader("Set-Cookie",
                    "token=" + token +
                            "; HttpOnly; Path=/; Max-Age=86400; SameSite=Lax"
            );

            return ResponseEntity.ok(new ApiResponse("Login successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    // ============================
    // Logout
    // ============================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                "token=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax"
        );
        return ResponseEntity.ok(new ApiResponse("Logged out"));
    }
}