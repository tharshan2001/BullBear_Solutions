package bullbear.app.security;

import bullbear.app.entity.user.Admin;
import bullbear.app.repository.AdminRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.WebUtils;

@Component
public class CurrentAdminArgumentResolver implements HandlerMethodArgumentResolver {

    private final AdminJwtUtil jwtUtil;
    private final AdminRepository adminRepository;

    public CurrentAdminArgumentResolver(AdminJwtUtil jwtUtil, AdminRepository adminRepository) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAdmin.class) &&
                Admin.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) return null;

        Cookie cookie = WebUtils.getCookie(request, "ADMIN_TOKEN");
        if (cookie == null) return null;

        String token = cookie.getValue();
        if (!jwtUtil.validateToken(token)) return null;

        String email = jwtUtil.extractUsername(token);
        return adminRepository.findByEmail(email).orElse(null);
    }
}