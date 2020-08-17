package per.qiang.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import per.qiang.auth.filter.VerificationCodeFilter;
import per.qiang.auth.handler.CustomLoginFailureHandler;
import per.qiang.auth.handler.CustomLoginSuccessHandler;
import per.qiang.auth.service.UserDetailService;
import per.qiang.common.security.constant.AuthCons;

@Order(2)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final UserDetailService userDetailService;
    private final CustomLoginSuccessHandler successHandler;
    private  final CustomLoginFailureHandler failureHandler;
    private final VerificationCodeFilter verificationCodeFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .requestMatchers().antMatchers(AuthCons.OAUTH_ALL,AuthCons.LOGIN_PAGE_URL,AuthCons.LOGIN_URL).and()
                .authorizeRequests().anyRequest().authenticated()
                .and().formLogin().loginPage(AuthCons.LOGIN_PAGE_URL).loginProcessingUrl(AuthCons.LOGIN_URL)
                .successHandler(successHandler).failureHandler(failureHandler).permitAll()
                .and().csrf().disable();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/getVerify");
    }
}
