package bullbear.app.security;

import bullbear.app.entity.user.Admin;
import bullbear.app.repository.AdminRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
public class AdminJwtAuthFilter extends OncePerRequestFilter {

    private final AdminJwtUtil jwtUtil;
    private final AdminRepository adminRepository;

    public AdminJwtAuthFilter(AdminJwtUtil jwtUtil, AdminRepository adminRepository) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Get cookie
        Cookie cookie = WebUtils.getCookie(request, "ADMIN_TOKEN");
        if (cookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cookie.getValue();

        try {
            // Validate token first
            if (!jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract email from valid token
            String email = jwtUtil.extractUsername(token);

            // Check if not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Admin admin = adminRepository.findByEmail(email).orElse(null);

                if (admin != null) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    admin,
                                    null,
                                    admin.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (JwtException e) {
            // Invalid token → skip authentication
        }

        filterChain.doFilter(request, response);
    }
}