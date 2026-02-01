package bullbear.app.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver userResolver;
    private final CurrentAdminArgumentResolver adminResolver;

    public WebConfig(CurrentUserArgumentResolver userResolver,
                     CurrentAdminArgumentResolver adminResolver) {
        this.userResolver = userResolver;
        this.adminResolver = adminResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userResolver);   // @CurrentUser
        resolvers.add(adminResolver);  // @CurrentAdmin
    }
}