package per.qiang.auth.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import per.qiang.auth.exception.VerificationCodeException;
import per.qiang.auth.handler.CustomLoginFailureHandler;
import per.qiang.auth.service.VerificationService;
import per.qiang.common.security.constant.AuthCons;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class VerificationCodeFilter extends OncePerRequestFilter {

    private final VerificationService verificationService;

    private final CustomLoginFailureHandler customLoginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        RequestMatcher matcher = new AntPathRequestMatcher(AuthCons.LOGIN_URL, HttpMethod.POST.toString());
        if (matcher.matches(request)){
            try {
                String key = request.getParameter(AuthCons.VERIFY_KEY);
                String code = request.getParameter(AuthCons.VERIFY_CODE);
                verificationService.check(key, code);
                chain.doFilter(request,response);
            } catch (VerificationCodeException e) {
                customLoginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }else chain.doFilter(request,response);
    }
}
