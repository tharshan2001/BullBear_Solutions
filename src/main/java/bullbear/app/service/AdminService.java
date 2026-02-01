package bullbear.app.service;

import bullbear.app.entity.user.Admin;
import bullbear.app.repository.AdminRepository;
import bullbear.app.security.AdminJwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import bullbear.app.security.AdminJwtAuthFilter;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminJwtUtil adminJwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(AdminRepository adminRepository, AdminJwtUtil adminJwtUtil) {
        this.adminRepository = adminRepository;
        this.adminJwtUtil = adminJwtUtil;
    }

    // ============================
    // Authenticate Admin
    // ============================
    public String login(String email, String password) throws Exception {

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Admin not found"));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new Exception("Invalid credentials");
        }

        return adminJwtUtil.generateToken(admin.getEmail());
    }

    // ============================
    // Create Admin (Bootstrap / Internal)
    // ============================
    public Admin createAdmin(String username, String email, String password, String role) {

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setRole(role);
        admin.setPasswordHash(passwordEncoder.encode(password));

        return adminRepository.save(admin);
    }
}