package bullbear.app.controller;

import bullbear.app.dto.auth.ApiResponse;
import bullbear.app.dto.auth.LoginRequest;
import bullbear.app.entity.user.Admin;
import bullbear.app.security.AdminJwtUtil;
import bullbear.app.security.CurrentAdmin;
import bullbear.app.service.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminJwtUtil jwtUtil;

    public AdminController(AdminService adminService, AdminJwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    // ============================
    // Admin Login
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            // Authenticate admin
            Admin admin = adminService.login(request.getEmail(), request.getPassword());

            // Generate JWT token for admin (using email)
            String token = jwtUtil.generateToken(admin.getEmail());

            // Set token in HTTP-only cookie
            Cookie cookie = new Cookie("ADMIN_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000)); // expiration in seconds
            response.addCookie(cookie);

            return ResponseEntity.ok(new ApiResponse("Admin login successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    // ============================
    // Get Admin Profile
    // ============================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@CurrentAdmin Admin admin) {
        if (admin == null) {
            return ResponseEntity.status(401).body(new ApiResponse("Unauthorized"));
        }
        return ResponseEntity.ok(admin);
    }

    // ============================
    // Admin Logout
    // ============================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Delete the cookie
        Cookie cookie = new Cookie("ADMIN_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete immediately
        response.addCookie(cookie);

        return ResponseEntity.ok(new ApiResponse("Admin logged out successfully"));
    }
}