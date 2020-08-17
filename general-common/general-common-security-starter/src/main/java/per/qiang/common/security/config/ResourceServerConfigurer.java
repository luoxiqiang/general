package per.qiang.common.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import per.qiang.common.security.properties.Oauth2Properties;


@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(Oauth2Properties.class)
public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {
    private final Oauth2Properties oauth2Properties;
    private final RedisConnectionFactory redisConnectionFactory;

    public ResourceServerConfigurer(Oauth2Properties oauth2Properties, RedisConnectionFactory redisConnectionFactory) {
        this.oauth2Properties = oauth2Properties;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        if (oauth2Properties.getRemote().isEnabled()) resources.tokenServices(remoteTokenServices());
        else resources.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }

    @Bean(name = "remoteTokenServices")
    @ConditionalOnProperty(name = "oauth2.remote.enabled", havingValue = "true")
    public ResourceServerTokenServices remoteTokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(oauth2Properties.getRemote().getAccess());
        remoteTokenServices.setClientId(oauth2Properties.getClientId());
        remoteTokenServices.setClientSecret(oauth2Properties.getClientSecret());
        return remoteTokenServices;
    }

    @Bean
    @ConditionalOnMissingBean(name = "remoteTokenServices")
    public TokenStore tokenStore() {
        if (oauth2Properties.getJwt().isEnabled()) {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            jwtAccessTokenConverter.setVerifier(new MacSigner(oauth2Properties.getJwt().getAccessKey()));
            return new JwtTokenStore(jwtAccessTokenConverter);
        }
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
