package per.qiang.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import per.qiang.common.security.constant.AuthCons;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(AuthCons.LOGIN_PAGE_URL).setViewName(AuthCons.LOGIN_VIEW);
    }

}
