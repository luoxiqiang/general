package per.qiang.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import per.qiang.auth.converter.CustomTokenEnhancer;
import per.qiang.auth.converter.CustomUserAuthenticationConverter;
import per.qiang.auth.service.RedisClientDetailsService;
import per.qiang.auth.service.UserDetailService;
import per.qiang.common.security.properties.Oauth2Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(Oauth2Properties.class)
@RequiredArgsConstructor
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    private final ApplicationContext applicationContext;
    private final RedisClientDetailsService redisClientDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;
    private final Oauth2Properties oauth2Properties;
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(redisClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailService)
                .tokenServices(defaultTokenServices());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()");
    }

    @Bean
    @ConditionalOnProperty(name = "oauth2.jwt.enabled", havingValue = "true")
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        DefaultAccessTokenConverter defaultAccessTokenConverter = (DefaultAccessTokenConverter) accessTokenConverter.getAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(userAuthenticationConverter());
        accessTokenConverter.setSigningKey(oauth2Properties.getJwt().getAccessKey());
        return accessTokenConverter;
    }

    @Bean
    public TokenEnhancer customTokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    @Primary
    public DefaultTokenServices defaultTokenServices() {
        Collection<TokenEnhancer> tokenEnhancers = applicationContext.getBeansOfType(TokenEnhancer.class).values();
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(new ArrayList<>(tokenEnhancers));
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setReuseRefreshToken(false);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(redisClientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        if (oauth2Properties.getJwt().isEnabled())
            return new JwtTokenStore(jwtAccessTokenConverter());
        else {
            RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
            redisTokenStore.setAuthenticationKeyGenerator(oAuth2Authentication -> UUID.randomUUID().toString());
            return redisTokenStore;
        }
    }

    @Bean
    public DefaultOAuth2RequestFactory oAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(redisClientDetailsService);
    }


    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        CustomUserAuthenticationConverter userAuthenticationConverter = new CustomUserAuthenticationConverter();
        userAuthenticationConverter.setUserDetailsService(userDetailService);
        return userAuthenticationConverter;
    }

}


