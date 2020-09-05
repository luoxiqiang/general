package per.qiang.common.feign.config;

import com.alibaba.cloud.sentinel.feign.CustomSentinelFeign;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

public class FeignConfigurer {

    @Primary
    @Bean
    @Scope("prototype")
    @ConditionalOnProperty(name = "feign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return CustomSentinelFeign.builder();
    }

    // 使用springSecurity 需配置这个
    /*@Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return requestTemplate -> {
            String authorizationToken = AuthUtil.getCurrentTokenValue();
            if (StringUtils.isNotBlank(authorizationToken)) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, AuthCons.OAUTH2_TOKEN_TYPE + authorizationToken);
            }
        };
    }*/
}
